package com.example.rise.carecoin.LoginModule;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.UserServerObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 20/09/2017.
 */

public class NewpasswordViewController extends AppCompatActivity {

    Button back, done;
    EditText passwordTextField;
    String st_password;
    RefreshShowingDialog refreshShowingDialog;
    String emailString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpassword);
        ButterKnife.bind(this);

        init();

        RelativeLayout loginlayout = (RelativeLayout) findViewById(R.id.loginlayout);
        Drawable background = loginlayout.getBackground();
        background.setAlpha(30);
        refreshShowingDialog = new RefreshShowingDialog(this);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            emailString = (String) bd.get("email");

        }
    }
    @OnCheckedChanged(R.id.chk1)
    public void onChecked1(boolean checked) {
        if (checked) {
            passwordTextField.setTransformationMethod(null);
        } else {
            passwordTextField.setTransformationMethod(new PasswordTransformationMethod());
        }
        passwordTextField.setSelection(passwordTextField.getText().length());
    }
    private void init() {

        passwordTextField = (EditText) findViewById(R.id.editText_Password);


        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(NewpasswordViewController.this, ForgotpasswordViewController.class);
                startActivity(in);


            }
        });
        done = (Button) findViewById(R.id.newpassword);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConformpassword();

            }
        });
    }

    public void mConformpassword() {

        st_password = passwordTextField.getText().toString();

        if (st_password.length() > 0) {
            if (isValidPasword(st_password)) {
                if (isConn()) {
                    refreshShowingDialog.showAlert();
                    newpasswordData(emailString, st_password);
                } else {
                    new AlertShowingDialog(NewpasswordViewController.this, "No Connection");
                }


            } else {
                new AlertShowingDialog(NewpasswordViewController.this, "Password must contain at least 1 number, 1 letter, 1 special characters, and minimum of 8 characters in length without space.");
            }
        } else {
            new AlertShowingDialog(NewpasswordViewController.this, "Enter password");

        }


    }

    private void newpasswordData(String email, String password) {
        UserServerObject requestBody = new UserServerObject();

        requestBody.mailid = email;
        Log.e("email", "" + email);
        requestBody.password = password;
        Log.e("pass", "" + password);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<UserServerObject> callable = api.newpassword(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("codefor3", "call" + statusCode);
                Log.e("message", "call" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginViewController.class));

                    } else if (statusCode.equals("4")) {
                        new AlertShowingDialog(NewpasswordViewController.this, message);

                    }
                }
                refreshShowingDialog.hideRefreshDialog();

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                refreshShowingDialog.hideRefreshDialog();
                failurealert();
            }
        });

    }

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
    public void failurealert() {

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
                    newpasswordData(emailString,st_password);
                } else {
                    new AlertShowingDialog(NewpasswordViewController.this, "No Internet Connection");

                }


            }


        });


    }
}
