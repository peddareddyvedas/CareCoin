package com.example.rise.carecoin.LoginModule;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.rise.carecoin.Alert.LocationTracker;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.Controllers.ApisController;
import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.Controllers.LoginServerDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.PushNotification.NotificationUtils;
import com.example.rise.carecoin.R;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rise on 08/02/2018.
 */
public class SplashScreenViewController extends AppCompatActivity {
    Handler handler;
    public static SharedPreferences sharedPreferencesTOken;
    public static SharedPreferences.Editor sharedPreferencesTOkenEditor;
    public static boolean isFromNotification = false;
    private static final int REQUEST = 112;
    Context mContext = this;
    public static  boolean ishavingPasscode=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        handler = new Handler();

        sharedPreferencesTOken = getApplicationContext().getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);
        sharedPreferencesTOkenEditor = sharedPreferencesTOken.edit();

        // CurrencyController.getInstance().fillContext(getApplicationContext());
        ApisController.getInstance().fillContext(getApplicationContext());


        //Get User Location
        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();

        //for contacts info
        ContactsDataController.getInstance().fillContext(getApplicationContext());

        // GET USer Informaion.
        UserDataController.getInstance().fillContext(getApplicationContext());
        UserDataController.getInstance().fetchUserData();
        CurrencyController.getInstance().fillContext(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        Log.e("FirstCalledInfo", "" + getIntent().getExtras());
        if (extras != null) {
            String message = extras.getString("message");
            Log.e("MessageInBundle", "" + message);
            if (message != null) {
                isFromNotification = true;
                LoginServerDataController.getInstance().executeTransactionsRefreshServerAPI();
            }
        }
        checkRuntimepermissions();

    }
    private void checkRuntimepermissions(){
        /// check runtime permissions.
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            Log.e("Version", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");
            String[] PERMISSIONS = {
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,

            };
            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG", "@@@ IN IF hasPermissions");
                requestCameraPermissionsForAllMobiles();
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);
            } else {
                Log.d("TAG", "@@@ IN ELSE hasPermissions");
                checkIfUserHavingData();

            }
        } else {
            Log.d("TAG", "@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            checkIfUserHavingData();
        }

    }
private void checkIfUserHavingData(){
    if (UserDataController.getInstance().allUsers.size() > 0) {
        moveToNextPages();
    } else {
        SharedPreferences prefs = getSharedPreferences("AdvertiseInfo", MODE_PRIVATE);
        String restoredValue = prefs.getString("isStored", null);
        if (restoredValue == null || restoredValue.isEmpty()) {
            startActivity(new Intent(getApplicationContext(), CareCoinAdvertizeViewController.class));
        } else {
            startActivity(new Intent(getApplicationContext(), LoginViewController.class));
        }
    }
}
    @Override
    public void onResume() {
        super.onResume();

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    private void moveToNextPages() {
        if (UserDataController.getInstance().allUsers.size() > 0) {
            if (UserDataController.getInstance().currentUser.isSetPin)
            {
                Log.e("isSetPin","call"+UserDataController.getInstance().currentUser.isSetPin);
                ishavingPasscode=true;
                startActivity(new Intent(getApplicationContext(),PassCodeViewController.class));
            }else {
                ApisController.getInstance().currentbalanceApiExecution();
                ContactsDataController.getInstance().loadContactsOnSeparateThread();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), HomeActivityViewController.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }

        } else {
            startActivity(new Intent(getApplicationContext(), LoginViewController.class));
        }
    }
    public void requestCameraPermissionsForAllMobiles() {
        Permissions.check(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                "Camera and storage permissions are required because...", new Permissions.Options()
                        .setRationaleDialogTitle("Info"),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        Log.e("grant permissions", "Permissions are granted");
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(getApplicationContext(), "Permissions are necessary,Please grant the permissions", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                        Log.e("abcdefdg", Arrays.toString(deniedPermissions.toArray()));

                    }

                    @Override
                    public boolean onBlocked(Context context, ArrayList<String> blockedList) {
                        Toast.makeText(context, "Camera+Storage blocked:\n" + Arrays.toString(blockedList.toArray()),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public void onJustBlocked(Context context, ArrayList<String> justBlockedList,
                                              ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Camera+Storage just blocked:\n" + Arrays.toString(deniedPermissions.toArray()),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "@@@ PERMISSIONS grant");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkIfUserHavingData();
                        }

                    }, 2000);
                    ContactsDataController.getInstance().loadContactsOnSeparateThread();
                } else {
                    Log.d("TAG", "@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    ////////////Multiple Premession and alerts//////
    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onBackPressed() {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
