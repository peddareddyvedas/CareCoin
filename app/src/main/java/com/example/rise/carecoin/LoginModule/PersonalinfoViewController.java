package com.example.rise.carecoin.LoginModule;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.Model.User;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.PersonalInfoServerObjects;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 27/03/2018.
 */

public class PersonalinfoViewController extends AppCompatActivity {


    CircleImageView profileImage;
    private static final int CAMERA_REQUEST = 1880;
    public static final int PICK_IMAGE = 1889;
    public byte[] imageInByte;
    RelativeLayout relativeLayout;
    EditText email, name, phonenumber, country;
    TextView profile;
    public static RefreshShowingDialog refreshShowingDialog;
    String profileBase64Obj, oldProfielBase64Obj;
    User objUser;
    CircleImageView beforeImage;

    // Animation
    Animation animZoomIn;
    public static boolean isFromProfile = false;
    RelativeLayout normal, zoom, galary, camera, delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpersonalinfo);
        ButterKnife.bind(this);

        init();
        refreshShowingDialog = new RefreshShowingDialog(this);
        loadDefaultImage();
        setUserProfileData();


    }

    @OnClick(R.id.back)
    public void back() {

        if (!isFromProfile) {

            TextView toolbartext = (TextView) findViewById(R.id.profile);
            toolbartext.setText("Profile Photo");
            finish();

        } else {

            normal.setVisibility(View.VISIBLE);
            zoom.setVisibility(View.GONE);
            isFromProfile = false;
            TextView toolbartext = (TextView) findViewById(R.id.profile);
            toolbartext.setText("Profile");
        }


    }

    private void init() {

        delete = (RelativeLayout) findViewById(R.id.rl1);
        camera = (RelativeLayout) findViewById(R.id.rl3);
        galary = (RelativeLayout) findViewById(R.id.rl2);

        normal = (RelativeLayout) findViewById(R.id.relate);

        zoom = (RelativeLayout) findViewById(R.id.relativeview);
        final ImageView zoomimage = (ImageView) findViewById(R.id.imagezoom);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);

        email.setText("" + UserDataController.getInstance().currentUser.userid);

        phonenumber = (EditText) findViewById(R.id.phnumber);
        country = (EditText) findViewById(R.id.country);

        profileImage = (CircleImageView) findViewById(R.id.imageView_profile);


        relativeLayout = (RelativeLayout) findViewById(R.id.relat);
        relativeLayout.setBackgroundResource(R.drawable.recyclerviewborders);
        GradientDrawable gd = (GradientDrawable) relativeLayout.getBackground().getCurrent();
        gd.setColor(Color.parseColor("#f0f0f0"));
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromProfile = true;
                Drawable background = normal.getBackground();
                background.setAlpha(30);
                normal.setVisibility(View.GONE);
                zoom.setVisibility(View.VISIBLE);
                zoomimage.setImageDrawable(profileImage.getDrawable());

               // zoomimage.getLayoutParams().width = 1500;
               // zoomimage.getLayoutParams().height = 500;

              /*  Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight() /1;
                zoomimage.getLayoutParams().height = height;
                zoomimage.getLayoutParams().width = width;*/


                //  setQrImageWidthAndHeight(zoomimage);
                TextView toolbartext = (TextView) findViewById(R.id.profile);
                toolbartext.setText("Profile Photo");
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "call");
                loadDefaultImage();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "camera");

                ClickImageFromCamera();
                //captureImage();
            }
        });
        galary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "galery");

                GetImageFromGallery();

            }
        });

    }

    public void setQrImageWidthAndHeight(ImageView zoomimage) {
        zoomimage.setScaleType(ImageView.ScaleType.FIT_XY);
        zoomimage.setScaleType(ImageView.ScaleType.CENTER_CROP);


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "call");

    }

    @OnClick(R.id.save)
    public void savecontinue() {
        isAllFeidsHavingText();
    }

    public void loadDefaultImage() {

        profileImage.setImageResource(R.drawable.ic_profile);
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile);
        loadEncoded64ImageStringFromBitmap(resource);

    }

    public void GetImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }

    public void ClickImageFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);

    }


    public void loadEncoded64ImageStringFromBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageInByte = stream.toByteArray();
        profileBase64Obj = Base64.encodeToString(imageInByte, Base64.NO_WRAP);
        Log.e("base64Image", "call" + profileBase64Obj);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);

        if (data == null) {
            return;
        } else if (requestcode == CAMERA_REQUEST && resultcode == Activity.RESULT_OK) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photo = getResizedBitmap(photo, 300);
                convertBitmapToByteArray(photo);
                profileImage.setImageBitmap(photo);
                loadEncoded64ImageStringFromBitmap(photo);


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestcode == PICK_IMAGE && resultcode == Activity.RESULT_OK) {

            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap photo = BitmapFactory.decodeStream(inputStream);
                photo = getResizedBitmap(photo, 300);
                convertBitmapToByteArray(photo);
                profileImage.setImageBitmap(photo);
                loadEncoded64ImageStringFromBitmap(photo);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }


    }


    private void convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageInByte = stream.toByteArray();
        Log.e("imageInByte", "call" + imageInByte);

    }

    public void isAllFeidsHavingText() {

        if (name.getText().toString().isEmpty()) {

            new AlertShowingDialog(PersonalinfoViewController.this, "Please Enter Name");
        } else if (email.getText().toString().trim().isEmpty()) {

            new AlertShowingDialog(PersonalinfoViewController.this, "Please enter your email");


        } else if (!isValidEmail(email.getText().toString().trim())) {


            new AlertShowingDialog(PersonalinfoViewController.this, "Please enter a valid email");


        } else if (phonenumber.getText().toString().trim().isEmpty()) {


            new AlertShowingDialog(PersonalinfoViewController.this, "Please enter Phonenumber");


        } else {
            if (objUser != null) {
                if (checkProfielDataNotChanged()) {
                    startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
                    return;
                }
            }
            if (isConn()) {
                refreshShowingDialog.showAlert();

                insertPersonInfoToServer(email.getText().toString().trim(), name.getText().toString(), phonenumber.getText().toString(), country.getText().toString());
            } else {
                new AlertShowingDialog(PersonalinfoViewController.this, "Check Internet Connection");

            }
        }
    }


    public void insertPersonInfoToServer(final String email, final String name, final String phonenumber, final String countryCode) {

        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageInByte);
        final PersonalInfoServerObjects requestBody = new PersonalInfoServerObjects();

        MultipartBody.Part image1 = MultipartBody.Part.createFormData("userPhoto", "UserPhoto", imageBody);


        RequestBody bla1 = RequestBody.create(MediaType.parse("text/plain"), email.trim());
        RequestBody bla2 = RequestBody.create(MediaType.parse("text/plain"), name.trim());
        RequestBody bla3 = RequestBody.create(MediaType.parse("text/plain"), countryCode + "-" + phonenumber.trim());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<PersonalInfoServerObjects> callable = api.personalinfo(image1, bla1, bla2, bla3);

        callable.enqueue(new Callback<PersonalInfoServerObjects>() {
            @Override
            public void onResponse(Call<PersonalInfoServerObjects> call, retrofit2.Response<PersonalInfoServerObjects> response) {
                refreshShowingDialog.hideRefreshDialog();
                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("codeforperson", "call" + statusCode);
                Log.e("message", "call" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        updateUserData();
                        startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
                        Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();

                    } else if (statusCode.equals("0")) {
                        new AlertShowingDialog(PersonalinfoViewController.this, message);

                    } else if (statusCode.equals("1")) {
                        new AlertShowingDialog(PersonalinfoViewController.this, message);

                    }
                }
            }

            @Override
            public void onFailure(Call<PersonalInfoServerObjects> call, Throwable t) {
                refreshShowingDialog.hideRefreshDialog();
                Log.e("on", "" + t.getMessage());
                failurealert();
            }
        });

    }

    private void updateUserData() {
        objUser.username = name.getText().toString().trim();
        objUser.phonenumber = country.getText().toString() + "-" + phonenumber.getText().toString().trim();
        objUser.mprofilepicturepath = imageInByte;
        UserDataController.getInstance().updateUserData(objUser);
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


    ////////////

    public void setUserProfileData() {
        country.setText(GetCountryZipCode());
        objUser = UserDataController.getInstance().currentUser;

        if (objUser != null) {
            if (objUser.mprofilepicturepath != null) {
                name.setText("" + objUser.username);
                email.setText("" + objUser.userid);

                try {
                    String[] mobileNumberArray = objUser.phonenumber.toString().trim().split("-");
                    country.setText(mobileNumberArray[0]);
                    phonenumber.setText(mobileNumberArray[1]);
                    Log.e("ArrayMobileno", "" + mobileNumberArray.length);
                } catch (Exception e) {
                    phonenumber.setText("");
                }

                profileImage.setImageBitmap(convertByteArrayTOBitmap(objUser.mprofilepicturepath));
                beforeImage = profileImage;
                loadEncoded64ImageStringFromBitmap(convertByteArrayTOBitmap(objUser.mprofilepicturepath));
                oldProfielBase64Obj = profileBase64Obj;
                Log.e("profilPic", "" + objUser.mprofilepicturepath);

            } else {

            }
        } else {


        }
    }

    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = new String();

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        if (CountryZipCode.isEmpty()) {
            CountryZipCode = "";
        } else {
            CountryZipCode = "+" + CountryZipCode;
        }
        return CountryZipCode;
    }

    public Bitmap convertByteArrayTOBitmap(byte[] profilePic) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;

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
                } else {
                    new AlertShowingDialog(PersonalinfoViewController.this, "No Internet connection");
                }

            }


        });
    }


    public boolean checkProfielDataNotChanged() {


        Log.e("CheckImageOld", "" + oldProfielBase64Obj);
        Log.e("CheckImageNew", "" + profileBase64Obj);
        Log.e("CheckPhone", "" + objUser.phonenumber);
        Log.e("CheckPhoneNew", "" + country.getText().toString() + "-" + phonenumber.getText().toString());


        if (objUser.username != null && objUser.phonenumber != null && oldProfielBase64Obj != null && objUser.username.trim().equals(name.getText().toString().trim())
                && objUser.phonenumber.equals(country.getText().toString() + "-" + phonenumber.getText().toString())
                && oldProfielBase64Obj.equals(profileBase64Obj)) {
            Log.e("checkProfielData", "call");
            return true;
        }
        return false;

    }


    public void onBackPressed() {
        Log.e("CDA", "onBackPressed Called");
        if (!isFromProfile) {
            finish();
            //Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        } else {

            normal.setVisibility(View.VISIBLE);
            zoom.setVisibility(View.GONE);
            isFromProfile = false;
        }
        return;
    } //for reducing the filesize of image

   /* public void requestPermissions() {
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
*/
    //for reducing the filesize of image

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}