package com.example.rise.carecoin.HomeModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rise.carecoin.Controllers.ApisController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.R;
import com.google.zxing.WriterException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;

/**
 * Created by Vedas on 11/10/2016.
 */
public class ReceiveFragment extends Fragment {
    View view;
    TextView qrtext, avilable_amount;
    ImageView qrcodeimage;
    int QRcodeWidth = 350;
    Bitmap bitmap;
    Button generatetext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recive_fragment, container, false);
        ids(view);
        Log.e("onCreateView", "call");
        loadDbData();
        sharedPreferences = getActivity().getSharedPreferences("bitmap", Context.MODE_PRIVATE);
        if (UserDataController.getInstance().currentUser!=null) {
            loadBitMapImage(qrtext.getText().toString());
        }
        gettingQrFromPreference();
        return view;
    }
    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);
        String resultData = event.message.trim();
        if(resultData.equals("refreshMoney"))
        {
            loadDbData();
            Log.e("sidemenuMessageevent", "call" + event.message);

        }

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResumeReceive", "call");
         EventBus.getDefault().register(this);
        loadDbData();
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart", "call");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "call");
        EventBus.getDefault().unregister(this);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onPause", "call");
        EventBus.getDefault().unregister(this);

    }
    private void ids(View view) {
        qrcodeimage = (ImageView) view.findViewById(R.id.qrcodeimage);
        qrtext = (TextView) view.findViewById(R.id.qrtext);
        generatetext = (Button) view.findViewById(R.id.btn_generatetext);
        avilable_amount = (TextView) view.findViewById(R.id.avilable);
        generatetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void loadBitMapImage(String qrText) {
        if (!qrText.isEmpty()) {

            try {
                bitmap = ApisController.getInstance().TextToImageEncode(qrcodeimage, qrText, QRcodeWidth, getResources());
                qrcodeimage.setImageBitmap(bitmap);
                String bitmapString = encodeTobase64(bitmap);
                Log.e("bitmapString", "call" + bitmapString);
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("imagePreferance", bitmapString);
                sharedPreferencesEditor.commit();

            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            qrcodeimage.requestFocus();
        }
    }

    public void loadDbData() {
        Log.e("loadDbData", "call");
        UserDataController.getInstance().fetchUserData();
        if (UserDataController.getInstance().currentUser != null) {
            Log.e("UserDataController", "call" + UserDataController.getInstance().currentUser.avaliablebalance);
            qrtext.setText(UserDataController.getInstance().currentUser.walletAddress);
            avilable_amount.setText("" + UserDataController.getInstance().currentUser.avaliablebalance+" "+"CCN");
        } else {
            avilable_amount.setText("568.43CCN");

        }

    }

    public void gettingQrFromPreference() {
        String imagePreferance = sharedPreferences.getString("imagePreferance", null);
        Log.e("imagePreferance", "call" + imagePreferance);
        if (imagePreferance!=null){
            Bitmap bitmap = decodeBase64(imagePreferance);
            ApisController.getInstance().setQrImageWidthAndHeight(qrcodeimage);
            qrcodeimage.setImageBitmap(bitmap);

        }

    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // method for bitmap to base64
    public String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }
    ///////////
    public static class MessageEvent {
        public final String message;

        public MessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}