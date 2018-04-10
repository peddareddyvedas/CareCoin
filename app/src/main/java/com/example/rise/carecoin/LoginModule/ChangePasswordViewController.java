package com.example.rise.carecoin.LoginModule;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.ConfirmSendViewController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.UserServerObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 20/09/2017.
 */

public class ChangePasswordViewController extends AppCompatActivity {

    Button done;
    Button back;
    EditText name, oldPasswordTextField, newoldPasswordTextField;
    String st_email, st_password, st_newpassword;
    RefreshShowingDialog refreshShowingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        ButterKnife.bind(this);
        UserDataController.getInstance().fetchUserData();

        init();

        RelativeLayout loginlayout=(RelativeLayout)findViewById(R.id.loginlayout);
        Drawable background = loginlayout.getBackground();
        background.setAlpha(30);

        refreshShowingDialog=new RefreshShowingDialog(this);
        st_email = UserDataController.getInstance().currentUser.userid;
        Log.e("gmai", "" + st_email);
    }

    private void init() {

        oldPasswordTextField = (EditText) findViewById(R.id.editoldpassword);
        newoldPasswordTextField = (EditText) findViewById(R.id.editnewpassword);

        back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        done=(Button)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChange();

            }
        });
    }
    @OnCheckedChanged(R.id.chk)
    public void onChecked(boolean checked) {
        if (checked) {
            oldPasswordTextField.setTransformationMethod(null);
        } else {
            oldPasswordTextField.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        oldPasswordTextField.setSelection(oldPasswordTextField.getText().length());
    }
    @OnCheckedChanged(R.id.chk1)
    public void onChecked1(boolean checked) {
        if (checked) {
            newoldPasswordTextField.setTransformationMethod(null);
        } else {
            newoldPasswordTextField.setTransformationMethod(new PasswordTransformationMethod());
        }
        newoldPasswordTextField.setSelection(newoldPasswordTextField.getText().length());
    }
    public void mChange() {

        st_password = oldPasswordTextField.getText().toString();
        st_newpassword = newoldPasswordTextField.getText().toString();

        if (st_password.length() > 0) {
            if (st_newpassword.length() > 0) {
                if (isValidPasword(st_newpassword)) {

                    if (!st_password.equals(st_newpassword)) {
                        if (isConn()) {
                             refreshShowingDialog.showAlert();
                            changepasswordApiExecution(st_email,st_password,st_newpassword);

                        } else {
                            new AlertShowingDialog(ChangePasswordViewController.this, "No Internet connection");

                        }

                    } else {
                        new AlertShowingDialog(ChangePasswordViewController.this, "New password must be different from the old one");
                    }

                } else {
                    new AlertShowingDialog(ChangePasswordViewController.this, "Password must contain at least 1 number, 1 letter, 1 special characters, and minimum of 8 characters in length without space.");
                }
            } else {
                new AlertShowingDialog(ChangePasswordViewController.this, "Please enter a new password");

            }
        } else {
            new AlertShowingDialog(ChangePasswordViewController.this, "Please enter your password");

        }

    }
    private void changepasswordApiExecution(String email, String password, String newpassword) {

        UserServerObject requestBody = new UserServerObject();
        requestBody.mailid=email;
        Log.e("emaill", "" + email);
        requestBody.currentpassword=password;
        Log.e("pass", "" + password);
        requestBody.newpassword=newpassword;
        Log.e("newpassword", "" + newpassword);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.changepassword(requestBody);
        callable.enqueue(new retrofit2.Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {

                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("codefor3", "call" + statusCode +""+message);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        showChangePasswordSuccessAlert(message);
                        UserDataController.getInstance().currentUser.password=st_newpassword;
                        UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);

                    } else if (statusCode.equals("0")) {
                        new AlertShowingDialog(ChangePasswordViewController.this,message);

                    }
                    else if (statusCode.equals("1")) {
                        new AlertShowingDialog(ChangePasswordViewController.this,message);

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
                    changepasswordApiExecution(st_email,st_password,st_newpassword);
                } else {

                    new AlertShowingDialog(ChangePasswordViewController.this, "No Internet Connection");

                }


            }


        });


    }
    public void showChangePasswordSuccessAlert(String message) {

        final Dialog dialog = new Dialog(ChangePasswordViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.transactionsuccess_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent_selector);
        TextView success = (TextView) dialog.findViewById(R.id.success);
        success.setText("Success");
        TextView txt_msg = (TextView) dialog.findViewById(R.id.txt_msg);
        txt_msg.setText(message);
        txt_msg.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        Button ok = (Button) dialog.findViewById(R.id.btn_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
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
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
}
