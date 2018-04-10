package com.example.rise.carecoin.HomeModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.example.rise.carecoin.SideMenu.ContactsViewController;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.IllegalFormatCodePointException;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Vedas on 11/11/2016.
 */
public class SendFragment extends Fragment {
    View view;
    LinearLayout layout;
    ImageView qrcodeimage;
    TextView txt_amount, txt_currencyVal, tv_qr_readTxt;
    EditText ed_email, ed_note, ed_amount;
    String st_amount;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    String contact_email, contact_walletAddres, str_walletAddress, st_email;
    Button sendnow, sendnow1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.send_fragment, container, false);
        ids(view);

        ButterKnife.bind(this, view);

        layout = (LinearLayout) view.findViewById(R.id.layout);

        layout.setBackgroundResource(R.drawable.recyclerviewborders);
        GradientDrawable gd = (GradientDrawable) layout.getBackground().getCurrent();
        gd.setColor(Color.parseColor("#d7f1ed"));

        UserDataController.getInstance().fetchUserData();
        refreshSelectedCurrencyInformation();

        sharedPreferences = getActivity().getSharedPreferences("transactionDetails", Context.MODE_PRIVATE);

        gettingContactsFromContactsPage();
        ////TEXT WATCHER///////////////new AlertShowingDialog(getActivity(), "kdskdmakd");

        ed_email.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (UserDataController.getInstance().currentUser.userid.equals(ed_email.getText().toString()) && isValidEmail(ed_email.getText().toString().trim())) {
                        Log.e("UserDataController", "call");
                        new AlertShowingDialog(getActivity(), "Care coin Wallet is not allowed to transfer coins to yourself");
                    } else if (!isValidEmail(ed_email.getText().toString())){
                        new AlertShowingDialog(getActivity(), "Please enter a valid email");
                    }

                    return true;
                }
                return false;
            }
        });
        ed_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!UserDataController.getInstance().currentUser.userid.equals(ed_email.getText().toString()) && isValidEmail(ed_email.getText().toString().trim())) {
                    useremailbalanceApiExecution(ed_email.getText().toString());
                    tv_qr_readTxt.setText(str_walletAddress);
                    tv_qr_readTxt.setTextColor(Color.parseColor("#000000"));
                    sendnow.setVisibility(View.VISIBLE);
                } else if (UserDataController.getInstance().currentUser.userid.equals(ed_email.getText().toString()) && isValidEmail(ed_email.getText().toString().trim())) {
                    ed_email.setText("");
                    tv_qr_readTxt.setText("");
                    new AlertShowingDialog(getActivity(), "Care coin Wallet is not allowed to transfer coins to yourself");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isValidEmail(ed_email.getText().toString().trim())) {
                    tv_qr_readTxt.setText("Care Coin Address");
                    tv_qr_readTxt.setTextColor(Color.parseColor("#A6B2CA"));

                } else {
                    tv_qr_readTxt.setText(null);

                }


            }

        });

        ed_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ed_amount.getText().toString().length()>0 && ed_amount.getText().toString().equals("0")){
                    ed_amount.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResumeActivity", "call");
        refreshSelectedCurrencyInformation();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStartsend", "call");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "call");
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("onDestroyView", "call");

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "call");

    }
    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ReceiveFragment.MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);
        String resultData = event.message.trim();
        if (resultData.equals("refreshMoney")) {
            Log.e("sidemenuMessageevent", "call" + event.message);
            refreshSelectedCurrencyInformation();

        } else if (resultData.equals("removeContacts")) {
            Log.e("removeContacts", "Called");
            ed_email.setText("");
            tv_qr_readTxt.setText("");
        }
    }

    public void gettingContactsFromContactsPage() {
        Log.e("FromContactsPage", "Called");
        // getting contacts
        if (ContactsDataController.getInstance().selectedContactModel!=null){
            contact_email = ContactsDataController.getInstance().selectedContactModel.email;
            contact_walletAddres = ContactsDataController.getInstance().selectedContactModel.walletAddress;
            ed_email.setText(contact_email);
            tv_qr_readTxt.setText(contact_walletAddres);
            tv_qr_readTxt.setTextColor(Color.parseColor("#000000"));

        }
        ConfirmSendViewController.isFromConfirmPage=false;

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

            txt_amount.setText("" + UserDataController.getInstance().currentUser.avaliablebalance);
            if (CurrencyController.getInstance().selectedCurrencyModel != null) {
                double selectedCurrencyValue = UserDataController.getInstance().currentUser.avaliablebalance * CurrencyController.getInstance().selectedCurrencyModel.getPriceByUSD();
                String selectedValue = df.format(selectedCurrencyValue);
                Log.e("selectedValue", "call" + selectedValue);
                txt_currencyVal.setText("" + selectedValue + CurrencyController.getInstance().selectedCurrencyModel.getCurrencySymbol());

            }
        } else {
            txt_amount.setText("568.43");
        }
    }

    public void ids(View view) {
        txt_amount = (TextView) view.findViewById(R.id.textnumber);
        txt_currencyVal = (TextView) view.findViewById(R.id.txt_currencyVal);
        qrcodeimage = (ImageView) view.findViewById(R.id.qrcodeimage);
        tv_qr_readTxt = (TextView) view.findViewById(R.id.tv_qr_readTxt);
        ed_email = (EditText) view.findViewById(R.id.ed_email);
        ed_amount = (EditText) view.findViewById(R.id.ed_amount);
        ed_note = (EditText) view.findViewById(R.id.ed_note);
        sendnow = (Button) view.findViewById(R.id.sendnow);
        sendnow1 = (Button) view.findViewById(R.id.sendnow1);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#A6B2CA"));
        gd.setCornerRadius(7);
        sendnow1.setBackground(gd);

        qrcodeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator.forSupportFragment(SendFragment.this)
                        .setBeepEnabled(false)
                        .setCameraId(0).setOrientationLocked(true).setPrompt("Scan")
                        .setCaptureActivity(CaptureActivityPortrait.class)
                        .initiateScan();

            }
        });


    }

    @OnClick(R.id.pickcontact)
    public void pickContactAction() {
        startActivity(new Intent(getActivity(), ContactsViewController.class));

    }

    @OnClick(R.id.sendnow)
    public void sendAction() {

        Log.e("sendAction", "call");
        Double Stramount = 0.0;
        st_amount = ed_amount.getText().toString();
        String walletAddress = tv_qr_readTxt.getText().toString();
        Log.e("length", "call" + tv_qr_readTxt.getText().toString());

        //Double amount=Double.parseDouble(st_amount);
        if (ed_email.getText().toString().length() > 0) {

            if (isValidEmail(ed_email.getText().toString())) {
                if (walletAddress.length() > 0) {
                    Log.e("length1", "call" + walletAddress);

                    if (st_amount.length() > 0) {

                        Stramount = UserDataController.getInstance().currentUser.avaliablebalance;

                        if (Double.parseDouble(st_amount) > 0.0 && Double.parseDouble(st_amount) < Stramount) {
                            if (!walletAddress.equals("CareCoin Address")) {
                                loadDetailsInSharedPreference(ed_email.getText().toString(), st_amount, ed_note.getText().toString(), tv_qr_readTxt.getText().toString(), UserDataController.getInstance().currentUser.walletAddress);
                                startActivity(new Intent(getActivity(), ConfirmSendViewController.class));

                            } else {
                                new AlertShowingDialog(getActivity(), "Please select walletaddress");
                            }
                        } else {
                            new AlertShowingDialog(getActivity(), "Insufficient balance");

                        }
                    } else {
                        new AlertShowingDialog(getActivity(), "Please enter amount");

                    }
                } else {
                    new AlertShowingDialog(getActivity(), "Please select walletaddress");

                }

            } else {
                new AlertShowingDialog(getActivity(), "Please enter a valid email");
            }
        } else {
            new AlertShowingDialog(getActivity(), "Please enter email");
        }
    }
    public void loadDetailsInSharedPreference(String toEmail, String amount, String notes, String toAddress, String fromAddress) {
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("toEmail", toEmail);
        sharedPreferencesEditor.putString("amount", amount);
        sharedPreferencesEditor.putString("notes", notes);
        sharedPreferencesEditor.putString("toAddress", toAddress);
        sharedPreferencesEditor.putString("fromAddress", fromAddress);
        sharedPreferencesEditor.commit();

    }

    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.e("Scan", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");
                String carecoinAddress = result.getContents();
                if (carecoinAddress.contains("http") || carecoinAddress.contains(".") || carecoinAddress.contains("\\") || carecoinAddress.contains("/") || carecoinAddress.contains("www")) {
                    new AlertShowingDialog(getActivity(), "unable to scan this type of QRcode", "Scan Results");
                } else {
                    tv_qr_readTxt.setText(result.getContents());
                    tv_qr_readTxt.setTextColor(Color.parseColor("#000000"));
                    useraddressbalanceApiExecution(tv_qr_readTxt.getText().toString());
                }


            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //useraddressbalanceAps////
    public void useraddressbalanceApiExecution(String userAddress) {

        final UserServerObject requestBody = new UserServerObject();
        requestBody.address = userAddress;
        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<UserServerObject> callable = api.useraddress(userAddress);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().response;
                String message = response.body().mailid;
                Log.e("code", "" + statusCode);
                Log.e("walletmail", "" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        Log.e("walletaddress", "" + response.body().mailid);
                        Log.e("addre", "" + response.raw().request().tag().toString());

                        st_email = response.body().mailid;
                        ed_email.setText(st_email);


                    } else if (statusCode.equals("0")) {
                        new AlertShowingDialog(getApplicationContext(), message);

                    }
                }
            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                Log.e("onFailure", "call");
            }
        });
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
                if (response.body() != null) {
                    String statusCode = response.body().response;
                    String message = response.body().message;
                    Log.e("balancecode", "call" + statusCode);
                    Log.e("message", "call" + message);

                    if (!statusCode.equals(null)) {
                        if (statusCode.equals("3")) {

                            Log.e("addressid", "" + response.body().address);
                            Log.e("addre", "" + response.raw().request().tag().toString());
                            String walletaddress = response.body().address;
                            tv_qr_readTxt.setText(walletaddress);
                            tv_qr_readTxt.setTextColor(Color.parseColor("#000000"));

                        } else if (statusCode.equals("0")) {
                            if (!message.equals(null)) {
                                new AlertShowingDialog(getActivity(), message);
                                tv_qr_readTxt.setText("Care Coin Address");
                                tv_qr_readTxt.setTextColor(Color.parseColor("#A6B2CA"));
                                ed_email.setText("");
                            }
                        }
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

