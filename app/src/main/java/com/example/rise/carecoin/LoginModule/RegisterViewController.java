package com.example.rise.carecoin.LoginModule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Alert.LocationTracker;
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.Model.User;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
 * Created by Rise on 08/02/2018.
 */
public class RegisterViewController extends Activity {
    EditText userNameTextField, passwordTextField, confirmPasswordTextField,otp;
    String st_emailorphone, st_psw, st_confirm_psw;
    RefreshShowingDialog refreshShowingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        ButterKnife.bind(this);


        refreshShowingDialog = new RefreshShowingDialog(this);

        RelativeLayout loginlayout = (RelativeLayout) findViewById(R.id.loginlayout);
        Drawable background = loginlayout.getBackground();
        background.setAlpha(30);

        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();

        SharedPreferences tokenPreferences = getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        final String tokenId = tokenPreferences.getString("tokenid",null);
        final String deviceId = tokenPreferences.getString("deviceid",null);
        Log.e("inregister", "call" + tokenId+""+deviceId);

    }

    private void init() {

        userNameTextField = (EditText) findViewById(R.id.editText_Email);
        passwordTextField = (EditText) findViewById(R.id.editText_password);
        confirmPasswordTextField = (EditText) findViewById(R.id.confirmPasswordTextField);

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
    @OnCheckedChanged(R.id.chk1)
    public void onChecked1(boolean checked) {
        if (checked) {
            confirmPasswordTextField.setTransformationMethod(null);
        } else {
            confirmPasswordTextField.setTransformationMethod(new PasswordTransformationMethod());
        }
        confirmPasswordTextField.setSelection(confirmPasswordTextField.getText().length());
    }
    @OnClick(R.id.signin)
    public void sign() {
        signupAction();
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    // Validations for//
    public void signupAction() {

        st_emailorphone = userNameTextField.getText().toString();
        st_psw = passwordTextField.getText().toString();
        st_confirm_psw = confirmPasswordTextField.getText().toString();
        if (st_emailorphone.length() > 0) {
            if (isValidEmail(st_emailorphone)) {
                if (st_psw.length() > 0) {

                    if (isValidPasword(st_psw)) {
                        if (st_confirm_psw.length() > 0) {
                            if (st_psw.equals(st_confirm_psw)) {
                                if (isConn()) {
                                    refreshShowingDialog.showAlert();
                                    registerApiExecution();
                                } else {
                                    new AlertShowingDialog(RegisterViewController.this, "No Internet connection");
                                }
                            } else {
                                new AlertShowingDialog(RegisterViewController.this, "Please enter the same password as above");
                            }

                        } else {

                            new AlertShowingDialog(RegisterViewController.this, "Please enter confirm password");
                        }

                    } else {


                        new AlertShowingDialog(RegisterViewController.this, "Password must contain at least 1 number, 1 letter, 1 special characters, and minimum of 8 characters in length without space.");
                    }

                } else {
                    new AlertShowingDialog(RegisterViewController.this, "Please enter your password");

                }

            } else {

                new AlertShowingDialog(RegisterViewController.this, "Please enter a valid email");
            }

        } else {

            new AlertShowingDialog(RegisterViewController.this, "Please enter your email");
        }
    }

    //RegisterAps////
    private void registerApiExecution() {

        final UserServerObject requestBody = new UserServerObject();
        Log.e("st_emailorphone", "call" + st_emailorphone.trim());
        String latitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude());
        if (latitude == null) {
            latitude = "0.0";
        }
        String longitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
        if (latitude == null) {
            longitude = "0.0";
        }
        requestBody.mailid = st_emailorphone.trim();
        requestBody.password = st_psw.trim();
        requestBody.latitude = latitude;
        requestBody.longitude = longitude;
        requestBody.register_time = getCurrentTime();

        Log.e("longitude", "" + requestBody.longitude);
        Log.e("mailid", "" + requestBody.mailid);
        Log.e("password", "" + requestBody.password);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.register(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                refreshShowingDialog.hideRefreshDialog();
                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("codefor3", "call" + statusCode);
                Log.e("message", "call" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        alertDialogueForResend();

                    } else if (statusCode.equals("0")) {
                        new AlertShowingDialog(RegisterViewController.this, message);

                    } else if (statusCode.equals("1")) {
                        new AlertShowingDialog(RegisterViewController.this, message);

                    }
                }
            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                refreshShowingDialog.hideRefreshDialog();
                failureRegisteralert();
            }
        });
    }
    /////register verify

    private void VerifyData(String pin) {

        SharedPreferences tokenPreferences = getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        final String tokenId = tokenPreferences.getString("tokenid",null);
       final String deviceId = tokenPreferences.getString("deviceid",null);
        Log.e("tokengetting", "call" + tokenId+""+deviceId);

        final UserServerObject requestBody = new UserServerObject();
        requestBody.mailid = st_emailorphone.trim();
        requestBody.password = st_psw.trim();
        requestBody.attempt_time = getCurrentTime();
        requestBody.otp = (pin.trim());
        requestBody.deviceid=deviceId;
        requestBody.deviceToken=tokenId;
        Log.e("requestBody", "call" + requestBody.deviceToken+""+requestBody.deviceid);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.verify(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, final retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("verifycode", "" + statusCode);
                Log.e("verifymessage", "" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        refreshShowingDialog.hideRefreshDialog();

                        Log.e("verifymessage", "" + response.body().address);
                        Log.e("verifymessage", "" + response.body().walletid);

                        JsonObject transhistory = response.body().transactionsHistory;

                        HandlerThread handlerThread = new HandlerThread("fetchs");
                        handlerThread.start();
                        Handler handler = new Handler(handlerThread.getLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
                                try {
                                    Runnable backgroundTask = new Runnable() {
                                        @Override
                                        public void run() {

                                            loadDataBaseData(deviceId,tokenId,response.body().walletid, response.body().address);

                                            Transaction transaction = new Transaction(UserDataController.getInstance().currentUser,
                                                    "", "Care Coin Reward", getCurrentTime(),"reward",
                                                    50.0, response.body().address,"Care Coin Reward", "", "");
                                            TransactionDataController.getInstance().insertTransactionData(transaction);

                                            startActivity(new Intent(getApplicationContext(),PassCodeViewController.class));
                                            ContactsDataController.getInstance().loadContactsOnSeparateThread();

                                        }
                                    };
                                    taskExecutor.submit(backgroundTask);
                                    taskExecutor.shutdown();
                                    taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                                } catch (InterruptedException e) {

                                }
                            }
                        });


                    } else if (statusCode.equals("0")) {
                        refreshShowingDialog.hideRefreshDialog();

                        new AlertShowingDialog(RegisterViewController.this, message);
                    }else if (statusCode.equals("1")) {
                        refreshShowingDialog.hideRefreshDialog();

                        new AlertShowingDialog(RegisterViewController.this, message);

                    }
                }

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                refreshShowingDialog.hideRefreshDialog();
                failureVerifyalert();

            }
        });

    }
    private void loadTransactioIdsFromServer(JsonArray jsonArray, String walletAddress) {

        String transactionId = "", fromAddress = "", objTransationType = "", toAddress = "";
        String objTimestamp = "", objtransactionName = "", objNotes = "";
        double amount = 0.0;

        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                transactionId = object.get("transactionId").getAsString();
                objTimestamp = object.get("timestamp").getAsString();
                objTransationType = object.get("type").getAsString();

                if (objTransationType.equals("recieved")) {

                    amount = object.get("amount").getAsInt();
                    objtransactionName = object.get("fromAccount").getAsString();
                    fromAddress = object.get("fromAddress").getAsString();
                    toAddress = walletAddress;
                    Log.e("objtransactionName", "" + objtransactionName);
                    Log.e("objNotes", "" + objNotes);

                    if (object.get("notes") == null) {
                        System.out.println("inside null");
                        objNotes = "No Message";


                    } else {
                        System.out.println("inside else part");
                        objNotes = object.get("notes").getAsString();

                    }


                } else if (objTransationType.equals("sent")) {

                    amount = object.get("sentamount").getAsInt();
                    toAddress = object.get("toAddress").getAsString();
                    fromAddress = walletAddress;
                    objtransactionName = object.get("toAccount").getAsString();
                    Log.e("objtransactionName", "" + objtransactionName);
                    if (object.get("notes") == null) {
                        System.out.println("inside null");
                        objNotes = "No Message";


                    } else {
                        System.out.println("inside else part");
                        objNotes = object.get("notes").getAsString();

                    }
                } else if (objTransationType.equals("reward")) {
                    transactionId = object.get("transactionId").getAsString();
                    amount = object.get("amount").getAsInt();
                    fromAddress = walletAddress;
                    toAddress = walletAddress;
                    objtransactionName = "CareCoinReward";
                    if (object.get("notes") == null) {
                        System.out.println("inside null");
                        objNotes = "No Message";


                    } else {
                        System.out.println("inside else part");
                        objNotes = object.get("notes").getAsString();

                    }
                    Log.e("objtransactionName", "" + objtransactionName);
                    Log.e("objNotes", "" + objNotes);
                }
                if (objTransationType.equals("recieved") || objTransationType.equals("sent") || objTransationType.equals("reward")) {

                    Log.e("UserInfo", "" + objtransactionName);
                    Transaction transaction = new Transaction(UserDataController.getInstance().currentUser,
                            transactionId, objtransactionName, objTimestamp, objTransationType,
                            amount, walletAddress, objNotes, toAddress, fromAddress);
                    TransactionDataController.getInstance().insertTransactionData(transaction);
                }
            }
        }
    }
    private void loadDataBaseData(String deviceid,String token,String walletid, String walletaddress) {
        User user = new User();
        user.userid = st_emailorphone.trim();
        user.password = st_psw;
        user.registerTime = getCurrentTime();
        user.registerType = "Manual";
        user.walletId = walletid;
        user.walletAddress = walletaddress;
        user.device=deviceid;
        user.token=token;
        user.avaliablebalance=0.0;
        user.latitude = (String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
        user.longitude = (String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));
        UserDataController.getInstance().insertUserData(user);

    }

    public String getCurrentTime() {
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1000);
        Log.e("attem", "" + attempt_time);
        return attempt_time;
    }
    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    // Validate password
    private boolean isValidPasword(String password) {
        boolean isValid = false;

        String expression = "^(?=.*[a-z])(?=.*[$@$#!%*?&])[A-Za-z\\d$@$#!%*?&]{8,}";
        CharSequence inputStr = password;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            System.out.println("if");
            isValid = true;
        } else {
            System.out.println("else");
        }
        return isValid;
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    public void alertDialogueForResend() {

        Dialog verifyDialog = new Dialog(this);
        verifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyDialog.setCancelable(false);
        verifyDialog.setCanceledOnTouchOutside(false);
        verifyDialog.setCancelable(true);
        verifyDialog.setContentView(R.layout.otp_alert);
        verifyDialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
         otp = (EditText) verifyDialog.findViewById(R.id.ed_code);
        TextView text_auth = (TextView) verifyDialog.findViewById(R.id.textview);
        final Button verify = (Button) verifyDialog.findViewById(R.id.btn_verify);

        Button resend = (Button) verifyDialog.findViewById(R.id.btn_resend);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().length() > 0) {

                    if (isConn()){
                        refreshShowingDialog.showAlert();
                        VerifyData(otp.getText().toString());

                    }else {
                        new AlertShowingDialog(RegisterViewController.this,"No Internet connection" );
                    }

                } else {
                    new AlertShowingDialog(RegisterViewController.this, "Enter the verification code");

                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConn()){
                    refreshShowingDialog.showAlert();
                    registerApiExecution();
                }else {
                    new AlertShowingDialog(RegisterViewController.this,"No Internet connection" );
                }

            }


        });
        verifyDialog.show();


    }

    public void failureRegisteralert() {

        Log.e("responsealert", "call");
        final Dialog  failurealert = new Dialog(this);
        failurealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failurealert.setCancelable(false);
        failurealert.setCanceledOnTouchOutside(false);
        failurealert.setCancelable(true);
        failurealert.setContentView(R.layout.activity_failurealert);
        failurealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        failurealert.show();

        TextView text = (TextView) failurealert.findViewById(R.id.text_error);

        TextView text1 = (TextView) failurealert.findViewById(R.id.requestfail);

        Button cancel = (Button) failurealert.findViewById(R.id.btn_failurecancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failurealert.dismiss();

            }


        });
        Button retry = (Button) failurealert.findViewById(R.id.btn_failureretry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failurealert.dismiss();

                if (isConn()){
                    refreshShowingDialog.showAlert();
                    registerApiExecution();
                }else {
                    new AlertShowingDialog(RegisterViewController.this,"No Internet connection" );

                }
            }


        });
    }
    public void failureVerifyalert() {

        Log.e("responsealert", "call");
        final Dialog  failurealert = new Dialog(this);
        failurealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failurealert.setCancelable(false);
        failurealert.setCanceledOnTouchOutside(false);
        failurealert.setCancelable(true);
        failurealert.setContentView(R.layout.activity_failurealert);
        failurealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        failurealert.show();

        TextView text = (TextView) failurealert.findViewById(R.id.text_error);

        TextView text1 = (TextView) failurealert.findViewById(R.id.requestfail);

        Button cancel = (Button) failurealert.findViewById(R.id.btn_failurecancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failurealert.dismiss();

            }

        });
        Button retry = (Button) failurealert.findViewById(R.id.btn_failureretry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failurealert.dismiss();

                if (otp.getText().toString().length() > 0) {

                    if (isConn()){
                        refreshShowingDialog.showAlert();
                        VerifyData(otp.getText().toString());

                    }else {
                        new AlertShowingDialog(RegisterViewController.this,"No Internet connection" );
                    }

                } else {
                    new AlertShowingDialog(RegisterViewController.this, "Enter the verification code");

                }
            }


        });
    }
}