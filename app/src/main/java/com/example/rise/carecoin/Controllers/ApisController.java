package com.example.rise.carecoin.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.ReceiveFragment;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.BalanceServerObjects;
import com.example.rise.carecoin.ServerObjects.SettingsServerObjects;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WAVE on 2/15/2018.
 */

public class ApisController {

    public static ApisController myObj;
    Context context;

    public static ApisController getInstance() {
        if (myObj == null) {
            myObj = new ApisController();
        }

        return myObj;
    }
    public void fillContext(Context context1) {
        context = context1;
    }
    //balanceAps////
    public void currentbalanceApiExecution() {
        UserDataController.getInstance().fetchUserData();
        final BalanceServerObjects requestBody = new BalanceServerObjects();
        requestBody.addressId = UserDataController.getInstance().currentUser.walletAddress;
        Log.e("calladdress", "" + requestBody.addressId);
        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<BalanceServerObjects> callable = api.getBalance(requestBody.addressId);
        callable.enqueue(new Callback<BalanceServerObjects>() {
            @Override
            public void onResponse(Call<BalanceServerObjects> call, retrofit2.Response<BalanceServerObjects> response) {
                if (response.body() != null) {
                    String statusCode = response.body().response;
                    Log.e("balancecode", "call" + statusCode);

                    if (!statusCode.equals(null)) {
                        if (statusCode.equals("3")) {
                            Log.e("balanceenquire", "call" + response.body().balance);
                            String balance = response.body().balance;
                            UserDataController.getInstance().currentUser.avaliablebalance = Double.parseDouble(balance);
                            UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);
                            EventBus.getDefault().post(new ReceiveFragment.MessageEvent("refreshMoney"));
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<BalanceServerObjects> call, Throwable t) {
                Log.e("onFailure", "call");
            }
        });
    }

    public void executeSettingsApi() {
        final SettingsServerObjects requestBody = new SettingsServerObjects();
        requestBody.mailid = UserDataController.getInstance().currentUser.userid;
        requestBody.decimal=UserDataController.getInstance().currentUser.decimalvalue;
        requestBody.language=UserDataController.getInstance().currentUser.preferdlanguage;
        requestBody.pin=UserDataController.getInstance().currentUser.pinpasscode;
        requestBody.security=String.valueOf(UserDataController.getInstance().currentUser.isSetPin);
        Log.e("calladdress", "" + requestBody.security);
        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<SettingsServerObjects> callable = api.settingsApi(requestBody);
        callable.enqueue(new Callback<SettingsServerObjects>() {
            @Override
            public void onResponse(Call<SettingsServerObjects> call, retrofit2.Response<SettingsServerObjects> response) {
                if (response.body() != null) {
                    String statusCode = response.body().response;
                    String message = response.body().message;

                    Log.e("settings", "call" + statusCode);

                    if (!statusCode.equals(null)) {
                        if (statusCode.equals("3")) {
                            Log.e("settings1", "call" + message);

                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<SettingsServerObjects> call, Throwable t) {
                Log.e("onFailure", "call");
            }
        });
    }
    public Bitmap TextToImageEncode(ImageView qrcodeimage, String Value, int
            QRcodeWidth, Resources resources) throws WriterException {
        Log.e("TextToImageEncode","call");
        BitMatrix bitMatrix;
        setQrImageWidthAndHeight(qrcodeimage);
        try {

            // bitMatrix = encodeAsBitmap(barcode_content, BarcodeFormat.QR_CODE, 150, 150);

            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        resources.getColor(R.color.colorBlack) : resources.getColor(R.color.material_stepper);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;

    }
    public  void setQrImageWidthAndHeight(ImageView qrcodeimage)
    {
        qrcodeimage.setScaleType(ImageView.ScaleType.FIT_XY);
        qrcodeimage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        /*if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            int width = 600;
            int height = 600;

            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
            parms.topMargin = 60;
            qrcodeimage.setLayoutParams(parms);
            parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else {
            int width = 700;
            int height = 700;

            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
            parms.topMargin = 60;
            qrcodeimage.setLayoutParams(parms);
            parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }*/
    }
}
