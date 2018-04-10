package com.example.rise.carecoin.Controllers;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.ConfirmSendViewController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.HomeModule.ReceiveFragment;
import com.example.rise.carecoin.LoginModule.LoginViewController;
import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.Model.User;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.PersonalInfoServerObjects;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WAVE on 3/22/2018.
 */

public class LoginServerDataController extends AppCompatActivity {
    public static LoginServerDataController myObj;
    //Context context;
    String st_email, password;
    public static LoginServerDataController getInstance() {
        if (myObj == null) {
            myObj = new LoginServerDataController();
        }

        return myObj;
    }

    /*public void fillContext(Context context1) {
        context = context1;
    }*/
    //loginAps////
    public void loginApiExecution(final String st_emailandphone, final String st_password, final Context context) {
        st_email=st_emailandphone;
        password=st_password;
        SharedPreferences tokenPreferences = context.getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        String tokenId = tokenPreferences.getString("tokenid",null);
        String deviceId = tokenPreferences.getString("deviceid",null);
        Log.e("tokendeviceids", "call" + tokenId+""+deviceId);


        final UserServerObject requestBody = new UserServerObject();
        requestBody.mailid = st_emailandphone.trim();
        requestBody.password = st_password.trim();
        requestBody.deviceid=deviceId;
        requestBody.deviceToken=tokenId;

        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<UserServerObject> callable = api.login(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, final retrofit2.Response<UserServerObject> response) {
                LoginViewController.refreshShowingDialog.hideRefreshDialog();

                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("logincode", "call" + statusCode);
                Log.e("message", "call" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        JsonObject transhistory = response.body().transactionsHistory;
                        final JsonArray transactionsArray = transhistory.getAsJsonArray("transactions");
                        final ArrayList<JsonObject> personalInfoArray = response.body().personalinfo;

                        Log.e("transhistory", "" + transactionsArray);
                        Log.e("walletidinlogin", "" + response.body().walletid);
                        Log.e("personalInfo", "" + personalInfoArray);
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
                                            processUserResponseData(personalInfoArray,context,transactionsArray,response,requestBody);
                                        }
                                    };
                                    taskExecutor.submit(backgroundTask);
                                    taskExecutor.shutdown();
                                    taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                                } catch (InterruptedException e) {

                                }
                            }
                        });

                    } else if (statusCode.equals("1")) {

                        new AlertShowingDialog(context, message);

                    } else if (statusCode.equals("0")) {

                        new AlertShowingDialog(context, message);

                    }
                }
            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                LoginViewController.refreshShowingDialog.hideRefreshDialog();
                Log.e("onFailure", ""+t.getMessage());

                failurealert(context);
            }
        });
    }
    private void processUserResponseData(ArrayList<JsonObject> personalInfoArray, final Context context,
                                         final JsonArray transactionsArray,
                                         final Response<UserServerObject> response,final UserServerObject requestBody){
        Bitmap resource = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_profile);
        byte[] profieimageByteArray = getByteArrayForBitMapImage(resource);
        String nickName = "";
        String phoneNumber = "";
        if (personalInfoArray.size()>0) {
            JsonObject objPersonalInfo = personalInfoArray.get(0);
            final String nickName1 = objPersonalInfo.get("name").getAsString();
            final String phoneNumber1 = objPersonalInfo.get("mobileno").getAsString();
            Log.e("PersonalInfoIn", "" + nickName + phoneNumber);
            final String imageURl = ServerApisInterface.home_Image_Main_URL + objPersonalInfo.get("image").getAsString();

            HandlerThread handlerThread = new HandlerThread("fetchData");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final URL url;
                    try {
                        url = new URL(imageURl);
                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        byte[] profieimageByteArray1 = getByteArrayForBitMapImage(image);
                        processUserData(requestBody.deviceid, requestBody.deviceToken, response.body().walletid, response.body().address, requestBody.mailid, requestBody.password, phoneNumber1, nickName1, profieimageByteArray1);
                        loadTransactioIdsFromServer(transactionsArray, response.body().address);
                        ContactsDataController.getInstance().loadContactsOnSeparateThread();

                        Intent intent = new Intent();
                        intent.setClass(context, HomeActivityViewController.class);
                        context.startActivity(intent);
                        LoginViewController.refreshShowingDialog.hideRefreshDialog();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },1000);


        } else {
            Log.e("PersonalInfoOut", "" + nickName + phoneNumber);

            processUserData(requestBody.deviceid, requestBody.deviceToken, response.body().walletid, response.body().address, requestBody.mailid, requestBody.password, phoneNumber,nickName,profieimageByteArray);
            loadTransactioIdsFromServer(transactionsArray, response.body().address);
            ContactsDataController.getInstance().loadContactsOnSeparateThread();

            Intent intent = new Intent();
            intent.setClass(context, HomeActivityViewController.class);
            context.startActivity(intent);
        }
    }
    private void processUserData(String deviceid, String token, String walletid, String walletaddress, String st_emailandphone, String st_password, String mobileno,String nickName,byte[] imageData) {
        User user = new User();
        user.userid = st_emailandphone.trim();
        user.password = st_password;
        user.walletId = walletid;
        user.walletAddress = walletaddress;
        user.device=deviceid;
        user.token=token;
        user.phonenumber =mobileno;
        user.username = nickName;
        user.mprofilepicturepath = imageData;
        UserDataController.getInstance().insertUserData(user);
    }
    public void failurealert(final Context context) {

        Log.e("responsealert", "call");
        final Dialog failurealert = new Dialog(context);
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
                if (isConn(context)) {
                    LoginViewController.refreshShowingDialog.showAlert();
                    loginApiExecution(st_email,password,context);

                } else {
                    new AlertShowingDialog(context, "No Internet Connection");

                }
            }
        });

    }
    public boolean isConn(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
  public void executeTransactionsRefreshServerAPI(){
      final UserServerObject requestBody = new UserServerObject();
      requestBody.mailid = UserDataController.getInstance().currentUser.userid;
      requestBody.password = UserDataController.getInstance().currentUser.password;
      requestBody.deviceid=UserDataController.getInstance().currentUser.device;
      requestBody.deviceToken=UserDataController.getInstance().currentUser.token;
      requestBody.from="android";
      Log.e("fortesting","call"+requestBody.deviceToken+""+requestBody.deviceid);

      // Set the custom client when building adapter
      Retrofit retrofit = new Retrofit.Builder()
              .baseUrl(ServerApisInterface.home_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
      ServerApisInterface api = retrofit.create(ServerApisInterface.class);

      Call<UserServerObject> callable = api.login(requestBody);
      callable.enqueue(new Callback<UserServerObject>() {
          @Override
          public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
              ConfirmSendViewController.isTransactionDone=false;
              String statusCode = response.body().response;
              String message = response.body().message;
              Log.e("logincode", "call" + statusCode);
              Log.e("message", "call" + message);
              if (!statusCode.equals(null)) {
                  if (statusCode.equals("3")) {

                      JsonObject transhistory = response.body().transactionsHistory;
                      JsonArray jsonArray = transhistory.getAsJsonArray("transactions");
                      Log.e("transhistory", "" + jsonArray);

                      TransactionDataController.getInstance().deleteTransactionData(TransactionDataController.getInstance().allTransactions);
                      TransactionDataController.getInstance().allTransactions.removeAll(TransactionDataController.getInstance().allTransactions);
                      Log.e("allTransactions", "transactiondata sucessfully"+TransactionDataController.getInstance().allTransactions.size());

                      loadTransactioIdsFromServer(jsonArray, response.body().address);
                      EventBus.getDefault().post(new ReceiveFragment.MessageEvent("refreshNotification"));

                  }
              }
          }

          @Override
          public void onFailure(Call<UserServerObject> call, Throwable t) {
              Log.e("onFailure", "call");
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
                    fromAddress = object.get("fromAccount").getAsString();
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
                    toAddress = object.get("toAccount").getAsString();
                    fromAddress = walletAddress;
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


    public byte[] getByteArrayForBitMapImage(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
         return stream.toByteArray();

    }

}
