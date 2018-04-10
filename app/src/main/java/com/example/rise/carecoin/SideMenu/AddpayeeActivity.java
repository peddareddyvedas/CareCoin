package com.example.rise.carecoin.SideMenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
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
 * Created by Rise on 13/02/2018.
 */
public class AddpayeeActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView home;
    Button btn_save, btn_save1;
    ImageView qrcodeimage;
    TextView tv_qr_readTxt, walletaddress;
    EditText ed_email, name;
    SharedPreferences sharedPreferencesAddress;
    String str_walletAddress;
    RelativeLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpayee);
        ButterKnife.bind(this);
        setToolbar();
        init();
    }

    private void init() {
        qrcodeimage = (ImageView) findViewById(R.id.qrcodeimage);
        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);
        ed_email = (EditText) findViewById(R.id.ed_email);
        name = (EditText) findViewById(R.id.name);
        walletaddress = (TextView) findViewById(R.id.walletaddress);
        btn_save = (Button) findViewById(R.id.btn_save);

        btn_save1 = (Button) findViewById(R.id.btn_save1);
        btn_save1.setBackgroundResource(R.drawable.signin);
        GradientDrawable gd = (GradientDrawable) btn_save1.getBackground().getCurrent();
        gd.setColor(Color.parseColor("#A6B2CA"));
        ed_email.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    isAllFeidsHavingText();

                    return true;
                }
                return false;
            }
        });
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        home = (ImageView) toolbar.findViewById(R.id.toolbar_icon);
        home.setImageResource(R.drawable.ic_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
            }
        });

        TextView toolbartext = (TextView) toolbar.findViewById(R.id.toolbar_text);
        toolbartext.append(getString(R.string.addpayees));

    }

    @OnClick(R.id.btn_save)
    public void save() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("addressdetails", Context.MODE_PRIVATE);
        settings.edit().clear().commit();

        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        contactIntent
                .putExtra(ContactsContract.Intents.Insert.NAME, name.getText().toString())
                .putExtra(ContactsContract.Intents.Insert.EMAIL, ed_email.getText().toString());

        startActivityForResult(contactIntent, 1);
        ContactsDataController.getInstance().loadContactsOnSeparateThread();

        finish();
        // startActivity(new Intent(getApplicationContext(),ContactsViewController.class));
    }


    public void isAllFeidsHavingText() {

        if (name.getText().toString().isEmpty()) {


            new AlertShowingDialog(AddpayeeActivity.this, "Please Enter Name");
        } else if (ed_email.getText().toString().trim().isEmpty()) {

            new AlertShowingDialog(AddpayeeActivity.this, "Please Enter Email");

        } else if (!isValidEmail(ed_email.getText().toString().trim())) {


            new AlertShowingDialog(AddpayeeActivity.this, "Please Enter Valid Email");

        } else {

            if (isConn()) {

                useremailbalanceApiExecution(ed_email.getText().toString());
                sharedPreferencesAddress = getApplicationContext().getSharedPreferences("addressdetails", Context.MODE_PRIVATE);
                str_walletAddress = sharedPreferencesAddress.getString("walletAddress", null);
                Log.e("walleadd", "" + str_walletAddress);

                walletaddress.setText(str_walletAddress);
                walletaddress.setTextColor(Color.parseColor("#000000"));


                btn_save.setVisibility(View.VISIBLE);
                btn_save1.setVisibility(View.GONE);


            } else {
                new AlertShowingDialog(AddpayeeActivity.this, "Check Internet Connection");

            }


        }
    }

    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }


    //useraddressbalanceAps////
    public void useremailbalanceApiExecution(String userId) {
        final UserServerObject requestBody = new UserServerObject();

        requestBody.mailid = userId;
        Log.e("calluserid", "" + userId);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<UserServerObject> callable = api.useremail(userId);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().response;
                String message = response.body().address;
                Log.e("balancecode", "call" + statusCode);
                Log.e("message", "call" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        Log.e("addressid", "" + response.body().address);
                        Log.e("addre", "" + response.raw().request().tag().toString());
                        String walletadd = response.body().address;
                        walletaddress.setText(walletadd);


                    } else if (statusCode.equals("0")) {
                        //new AlertShowingDialog(getApplicationContext(), message);

                    }
                }
            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                Log.e("onFailure", "call");
            }
        });
    }
}




