package com.example.rise.carecoin.LoginModule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.Model.User;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.UserServerObject;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 19/09/2017.
 */

public class ForgotpasswordViewController extends AppCompatActivity {
    Button back;
    EditText userNameTextField,otp;
    String st_emailandphone;
    RefreshShowingDialog refreshShowingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ButterKnife.bind(this);
        init();

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            String emailString  = (String) bd.get("email");
            userNameTextField.setText(emailString);

        }

        refreshShowingDialog=new RefreshShowingDialog(this);

        RelativeLayout loginlayout = (RelativeLayout) findViewById(R.id.loginlayout);
        Drawable background = loginlayout.getBackground();
        background.setAlpha(30);

    }

    private void init() {
        userNameTextField = (EditText) findViewById(R.id.editText_Email);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.forgotpassword)
    public void getpasword() {
        mForgotpsw();

    }
    public void mForgotpsw() {
        st_emailandphone = userNameTextField.getText().toString();
        if (st_emailandphone.length() > 0) {
            if (isValidEmail(st_emailandphone)) {

                if (isConn()) {
                    refreshShowingDialog.showAlert();
                    forgotApiExecution();

                } else {
                    new AlertShowingDialog(ForgotpasswordViewController.this, "No Internet Connection");
                }
            } else {
                new AlertShowingDialog(ForgotpasswordViewController.this, "Please enter a valid email");
            }
        } else {

            new AlertShowingDialog(ForgotpasswordViewController.this, "Please enter your email");
        }
    }
    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    private boolean isValidPhone(String pass) {
        return pass != null && pass.length() == 13;

    }
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }


    ////////////////////////////ForgotAps/////////////////////////////////
    private void forgotApiExecution() {
        UserServerObject requestBody = new UserServerObject();
        requestBody.mailid = st_emailandphone.trim();
        requestBody.register_time=getCurrentTime();
        Log.e("ema", "" + st_emailandphone);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.forgot(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("codefor3", "call" + statusCode);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        alertDialogueForResend();
                    } else if (statusCode.equals("0")) {
                        new AlertShowingDialog(ForgotpasswordViewController.this, message);
                    }
                    else if (statusCode.equals("1")) {
                        new AlertShowingDialog(ForgotpasswordViewController.this, message);
                    }
                }

             refreshShowingDialog.hideRefreshDialog();
            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                refreshShowingDialog.hideRefreshDialog();
                failureAlertForForgotAPi();
            }
        });
    }
    public void alertDialogueForResend() {
        Log.e("alert", "call");
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
                        new AlertShowingDialog(ForgotpasswordViewController.this,"No Internet connection" );
                    }

                } else {
                    new AlertShowingDialog(ForgotpasswordViewController.this, "Enter the verification code");

                }

            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConn()){
                    refreshShowingDialog.showAlert();
                    forgotApiExecution();
                }else {
                    new AlertShowingDialog(ForgotpasswordViewController.this,"No Internet connection" );
                }
            }


        });
        verifyDialog.show();
    }

/////forgot verify

    private void VerifyData(String pin) {

        String to = "verify";
        UserServerObject requestBody = new UserServerObject();
        requestBody.mailid = st_emailandphone.trim();
        requestBody.attempt_time = getCurrentTime();
        requestBody.otp = (pin.trim());
        requestBody.to=(to);
        Log.e("otp", "" + requestBody.otp);
        Log.e("mail", "" + requestBody.mailid);
        Log.e("atmpt", "" + requestBody.attempt_time);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.forgotverify(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                refreshShowingDialog.hideRefreshDialog();
                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("verifycode", "" + statusCode);
                Log.e("verifymessage", "" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        User user = new User();
                        user.userid=st_emailandphone;
                        Intent intent = new Intent(ForgotpasswordViewController.this, NewpasswordViewController.class);
                        intent.putExtra("email", userNameTextField.getText().toString().trim());
                        startActivity(intent);

                    } else if (statusCode.equals("1")) {
                        new AlertShowingDialog(ForgotpasswordViewController.this, message);
                    }
                    else if (statusCode.equals("0")) {
                        new AlertShowingDialog(ForgotpasswordViewController.this, message);
                    }
                }

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                refreshShowingDialog.hideRefreshDialog();
                failureAlertForVerifyPassword();
            }
        });

    }
    public String getCurrentTime() {
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1L);
        Log.e("attem", "" + attempt_time);
        return attempt_time;
    }
    public void failureAlertForForgotAPi() {
        Log.e("responsealert", "call");
        final Dialog failurealert = new Dialog(this);
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

                if (isConn()) {

                    refreshShowingDialog.showAlert();
                    forgotApiExecution();
                } else {

                    new AlertShowingDialog(ForgotpasswordViewController.this, "No Internet Connection");

                }


            }


        });
    }
    public void failureAlertForVerifyPassword() {
        Log.e("responsealert", "call");
        final Dialog failurealert = new Dialog(this);
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
                        new AlertShowingDialog(ForgotpasswordViewController.this,"No Internet connection" );
                    }

                } else {
                    new AlertShowingDialog(ForgotpasswordViewController.this, "Enter the verification code");

                }



            }


        });
    }
}
