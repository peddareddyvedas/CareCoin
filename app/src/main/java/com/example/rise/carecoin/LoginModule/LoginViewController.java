package com.example.rise.carecoin.LoginModule;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.Controllers.LoginServerDataController;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.ConfirmSendViewController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.Model.CurrencyModel;
import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.Model.User;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 19/09/2017.
 */

public class LoginViewController extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    EditText userNameTextField, passwordTextField;
    String st_emailandphone, st_password;
    ProgressDialog mProgress;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private int RC_SIGN_IN = 100;
    ImageView fb;
    private CallbackManager callbackManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    String socialmediaLoginEmail;
    public static RefreshShowingDialog refreshShowingDialog;
    TelephonyManager telephonyManager;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        ButterKnife.bind(this);

        refreshShowingDialog = new RefreshShowingDialog(this);

        ConfirmSendViewController.isFromConfirmPage = false;

        mProgress = new ProgressDialog(LoginViewController.this);
        mProgress.setMessage("Loading...");
        mProgress.setProgress(Color.BLACK);
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);

        sharedPreferences = getApplicationContext().getSharedPreferences("socialMediaLoginDetails", Context.MODE_PRIVATE);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.e("sdf", "asdfas" + newToken);
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        RelativeLayout loginlayout = (RelativeLayout) findViewById(R.id.loginlayout);
        Drawable background = loginlayout.getBackground();
        background.setAlpha(30);

        gettingDeviceId();
    }

    private void init() {

        userNameTextField = (EditText) findViewById(R.id.editText_Email);
        passwordTextField = (EditText) findViewById(R.id.editText_password);
        fb = (ImageView) findViewById(R.id.fb);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConn()) {
                    mProgress.show();
                    if (v == fb) {
                        LoginManager.getInstance().logInWithReadPermissions(LoginViewController.this, Arrays.asList("public_profile", "user_friends", "email"));
                        LoginManager.getInstance().registerCallback(callbackManager, callback);
                    }
                } else {
                    mProgress.dismiss();
                    new AlertShowingDialog(LoginViewController.this, "No Internet connection");
                }
            }
        });

    }
    @OnCheckedChanged(R.id.chk)
    public void onChecked(boolean checked) {
        if (checked) {
            passwordTextField.setTransformationMethod(null);
        } else {
            passwordTextField.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        passwordTextField.setSelection(passwordTextField.getText().length());
    }
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("FBStatus", "onSuccess Called");

            System.out.println("onSuccess");

            String accessToken = loginResult.getAccessToken()
                    .getToken();
            Log.i("accessToken", accessToken);

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {

                            Log.i("LoginActivity",
                                    response.toString());
                            try {
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String gender = object.getString("gender");

                                if (email != null) {
                                    mProgress.dismiss();
                                    sharedPreferencesEditor = sharedPreferences.edit();
                                    sharedPreferencesEditor.putString("name", name);
                                    Log.e("username", "" + name);
                                    sharedPreferencesEditor.putString("gender", gender);
                                    String imageURLString = "http://graph.facebook.com/" + id + "/picture?type=large";
                                    sharedPreferencesEditor.putString("picture", imageURLString);
                                    sharedPreferencesEditor.putString("Type", "Facebook");
                                    Log.e("gender", "" + gender);
                                    Log.e("gender", "" + imageURLString);
                                    Log.e("emailfacebook", "call" + name + "" + email + "" + gender);
                                    socialmediaLoginEmail = email;
                                    sharedPreferencesEditor.commit();
                                    LoginManager.getInstance().logOut();
                                    mProgress.dismiss();
                                    startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));

                                } else {
                                    Log.e("emailnull", "call");
                                    LoginManager.getInstance().logOut();
                                    mProgress.dismiss();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields",
                    "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {
            mProgress.dismiss();

            Log.e("FBStatus", "OnCancel Called");
        }

        @Override
        public void onError(FacebookException e) {
            Log.e("FBStatus", "OnCancel Called" + e);
            mProgress.dismiss();

        }
    };

    @OnClick(R.id.signin)
    public void login() {
        mLogin();

    }

    @OnClick(R.id.forgotpassword)
    public void forgot() {
        moveToForgetPasswordPage(userNameTextField.getText().toString());
    }

    private void moveToForgetPasswordPage(String emailInfo) {

        Intent intent = new Intent(LoginViewController.this, ForgotpasswordViewController.class);
        intent.putExtra("email", emailInfo);
        startActivity(intent);

    }

    @OnClick(R.id.signup)
    public void signup() {
        Intent in = new Intent(LoginViewController.this, RegisterViewController.class);
        startActivity(in);
    }

    @OnClick(R.id.google)
    public void google() {
        mProgress.show();
        if (isConn()) {
            mProgress.show();
            signIn();
        } else {
            new AlertShowingDialog(LoginViewController.this, "No Internet Connection");
            mProgress.dismiss();

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("requestcode", "" + requestCode + " " + resultCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.e("statuscode", "calldd" + statusCode);

            handleSignInResult(result);
        } else {
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("handleSignInResult", "" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            socialmediaLoginEmail = acct.getEmail();
            String idString = acct.getId();
            String NameString = acct.getDisplayName();
            socialmediaLoginEmail = acct.getEmail();
            Log.e("email", "" + socialmediaLoginEmail);
            Log.e("id", "" + idString);
            Log.e("Name", "" + NameString);
            mProgress.dismiss();
            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString("name", acct.getDisplayName());
            sharedPreferencesEditor.putString("gender", "Female");
            if (acct.getPhotoUrl() == null) {
                //set default image
                Log.e("empptyImag", "call" + acct.getPhotoUrl());
            } else {
                Log.e("pptyImag", "call" + acct.getPhotoUrl().toString());
                sharedPreferencesEditor.putString("picture", acct.getPhotoUrl().toString());
            }

            sharedPreferencesEditor.putString("Type", "Google");

            Log.e("username", "" + acct.getDisplayName());
            Log.e("username1", "" + acct.getEmail());
            sharedPreferencesEditor.commit();

            startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        } else {

            mProgress.dismiss();
            Toast.makeText(getApplicationContext(), "Failed login", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void mLogin() {

        st_emailandphone = userNameTextField.getText().toString();
        st_password = passwordTextField.getText().toString();


        if (st_emailandphone.length() > 0) {

            if (st_password.length() > 0) {

                if (isConn()) {
                    refreshShowingDialog.showAlert();
                    LoginServerDataController.getInstance().loginApiExecution(st_emailandphone,st_password,this);

                } else {
                    new AlertShowingDialog(LoginViewController.this, "No Internet Connection");
                }

            } else {
                new AlertShowingDialog(LoginViewController.this, "Please enter your password");
            }
        } else {

            new AlertShowingDialog(LoginViewController.this, "Please enter your email");
        }


    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public  String gettingDeviceId(){

        if(checkPermission()){

            telephonyManager    = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String id = telephonyManager.getDeviceId();
            Log.e("tellid",""+telephonyManager.getDeviceId());

            SplashScreenViewController.sharedPreferencesTOkenEditor.putString("deviceid",telephonyManager.getDeviceId());
            SplashScreenViewController.sharedPreferencesTOkenEditor.commit();
            return id;
        } else {

            requestPermission();
        }


        return null;
    }


    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission(){

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    gettingDeviceId();

                } else {
                    
                }
                break;
        }
    }

}
