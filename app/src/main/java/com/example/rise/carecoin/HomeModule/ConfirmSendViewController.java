package com.example.rise.carecoin.HomeModule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.Controllers.ApisController;
import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.TransferServerObject;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WAVE on 2/13/2018.
 */

public class ConfirmSendViewController extends Activity {
    RelativeLayout recyclerlayout;
    SharedPreferences Preferences;
    TextView txt_email, txt_wallet, txt_notes, txt_amount, txt_availablebal,txt_converVal;
    String  amountString, notesString, toEmailString,fromAddressString,toAddressString;
    RefreshShowingDialog refreshShowingDialog;
    public static  boolean isFromConfirmPage=false;
    public static  boolean isTransactionDone=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_send);
        ButterKnife.bind(this);
        refreshShowingDialog = new RefreshShowingDialog(this);

        ids();
        recyclerlayout.setBackgroundResource(R.drawable.recyclerviewborders);
        GradientDrawable gd = (GradientDrawable) recyclerlayout.getBackground().getCurrent();
        gd.setColor(Color.parseColor("#e0e0e0"));

        // from SendFragemt class class.
        Preferences = getSharedPreferences("transactionDetails", Context.MODE_PRIVATE);
        loadTransactionDetails(Preferences);
        refreshSelectedCurrencyInformation();

    }
    public void refreshSelectedCurrencyInformation() {
        DecimalFormat df ;
        if (UserDataController.getInstance().currentUser.decimalvalue!=null && !UserDataController.getInstance().currentUser.decimalvalue.isEmpty()){
            String decimalval=UserDataController.getInstance().currentUser.decimalvalue;
            Log.e("decimalval1","call"+decimalval);
            df = new DecimalFormat(decimalval);
        }else {
            df = new DecimalFormat("0.0");
        }
        if (UserDataController.getInstance().currentUser != null) {

            txt_availablebal.setText("" + UserDataController.getInstance().currentUser.avaliablebalance);
            if (CurrencyController.getInstance().selectedCurrencyModel != null) {
                double selectedCurrencyValue = UserDataController.getInstance().currentUser.avaliablebalance * CurrencyController.getInstance().selectedCurrencyModel.getPriceByUSD();
                String selectedValue= df.format(selectedCurrencyValue);
                Log.e("selectedValue","call"+selectedValue);
                txt_converVal.setText("" + selectedValue + CurrencyController.getInstance().selectedCurrencyModel.getCurrencySymbol());

            }
        } else {
            txt_amount.setText("568.43");
        }
    }
    private void ids() {
        recyclerlayout = (RelativeLayout) findViewById(R.id.layout);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_wallet = (TextView) findViewById(R.id.txt_wallet);
        txt_amount = (TextView) findViewById(R.id.txt_total);
        txt_notes = (TextView) findViewById(R.id.txt_notes);
        txt_availablebal = (TextView) findViewById(R.id.txt_availablebal);
        txt_converVal = (TextView) findViewById(R.id.txt_convertVal);

    }
    @OnClick(R.id.back)
    public void backAction() {
      //isFromConfirmPage=true;

        SharedPreferences preferences = getSharedPreferences("contactsDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
         finish();
        startActivity(new Intent(getApplicationContext(),HomeActivityViewController.class));
    }

    @OnClick(R.id.btn_return)
    public void returnAction() {
        //isFromConfirmPage=true;
        SharedPreferences preferences = getSharedPreferences("contactsDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        finish();
        startActivity(new Intent(getApplicationContext(),HomeActivityViewController.class));
    }
    @Override
    public void onBackPressed() {
        //isFromConfirmPage=true;

        SharedPreferences preferences = getSharedPreferences("contactsDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        finish();
        startActivity(new Intent(getApplicationContext(),HomeActivityViewController.class));
    }
    @OnClick(R.id.btn_confrim)
    public void confirmAction() {
        if (UserDataController.getInstance().currentUser!=null) {
            refreshShowingDialog.showAlert();
             moneyTrasferApiExecution(Integer.parseInt(amountString));
        }else {
            finish();
        }
    }


    public void loadTransactionDetails(SharedPreferences sharedPreferences) {
        UserDataController.getInstance().fetchUserData();
        toEmailString = sharedPreferences.getString("toEmail", null);
        amountString = sharedPreferences.getString("amount", null);
        notesString = sharedPreferences.getString("notes", null);
        fromAddressString = sharedPreferences.getString("fromAddress", null);
        toAddressString = sharedPreferences.getString("toAddress", null);
        if (notesString.length()>0)
        {
            txt_notes.setText(notesString);
            Log.e("notes",""+txt_notes.getText().toString());
        } else{
            txt_notes.setText("");
            notesString="No Message";
        }
        txt_amount.setText(amountString);
        txt_email.setText(toEmailString);
        txt_wallet.setText(UserDataController.getInstance().currentUser.userid);
    }

    public void moneyTrasferApiExecution(final int amount) {
        UserDataController.getInstance().fetchUserData();
        Log.e("walletId", "call" + UserDataController.getInstance().currentUser.walletId);
        final TransferServerObject requestBody = new TransferServerObject();
        requestBody.walletId = UserDataController.getInstance().currentUser.walletId;
        requestBody.mailid = UserDataController.getInstance().currentUser.userid;
        requestBody.password = UserDataController.getInstance().currentUser.password;
        requestBody.toAddress = toAddressString;
        requestBody.fromAddress = UserDataController.getInstance().currentUser.walletAddress;
        requestBody.changeAddress = UserDataController.getInstance().currentUser.walletAddress;
        requestBody.amount = amount;
        requestBody.notes = notesString;
        Log.e("notes","call"+notesString);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<TransferServerObject> callable = api.getTransfer(requestBody);
        callable.enqueue(new Callback<TransferServerObject>() {
            @Override
            public void onResponse(Call<TransferServerObject> call, retrofit2.Response<TransferServerObject> response) {
                refreshShowingDialog.hideRefreshDialog();
                String statusCode = response.body().response;
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        String transactionId = response.body().id;
                        String message = response.body().message;
                        showTransactionSuccessAlert();
                        Transaction transaction = new Transaction(UserDataController.getInstance().currentUser,
                                transactionId, "", getCurrentTime(), "sent"
                                , amount, UserDataController.getInstance().currentUser.walletId
                                , notesString,toEmailString , UserDataController.getInstance().currentUser.userid);
                        TransactionDataController.getInstance().insertTransactionData(transaction);
                        ApisController.getInstance().currentbalanceApiExecution();
                        refreshSelectedCurrencyInformation();

                        //clear data from contacts preference.
                        SharedPreferences preferences = getSharedPreferences("contactsDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Log.e("codefor3Transfer", "call" + statusCode + "" + transactionId + "" + message);
                    }
                }
            }

            @Override
            public void onFailure(Call<TransferServerObject> call, Throwable t) {
                refreshShowingDialog.hideRefreshDialog();
                Log.e("onFailure", "call"+t.getMessage());
                failurealert();
            }
        });
    }
    public String getCurrentTime() {
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1000);
        Log.e("attem", "" + attempt_time);
        return attempt_time;
    }
    public void showTransactionSuccessAlert() {


        final Dialog dialog = new Dialog(ConfirmSendViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.transactionsuccess_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent_selector);
        TextView success = (TextView) dialog.findViewById(R.id.success);
        success.setText("Success");
        TextView txt_msg = (TextView) dialog.findViewById(R.id.txt_msg);
        txt_msg.setText("Successfully send "+" "+txt_amount.getText().toString()+" CCN "+" to "+"\t"+txt_email.getText().toString());

        Button ok = (Button) dialog.findViewById(R.id.btn_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isTransactionDone=true;
                startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
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
                    moneyTrasferApiExecution(Integer.parseInt(amountString));
                } else {

                    new AlertShowingDialog(ConfirmSendViewController.this, "No Internet Connection");

                }


            }


        });


    }
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
}
