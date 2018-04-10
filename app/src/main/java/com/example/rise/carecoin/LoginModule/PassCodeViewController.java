package com.example.rise.carecoin.LoginModule;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.Settings.SettingsViewController;

import in.arjsna.passcodeview.PassCodeView;

public class PassCodeViewController extends AppCompatActivity {
    PassCodeView passCodeView, confrimpassCodeView, changePasscode;
    String PASSCODE = "";
    String PASSCODE1 = "";
    RelativeLayout relativeLayout, relativeLayout1, relativeLayout2;
    Toolbar toolbar;
    ImageView home;
    TextView toolbartext;
    Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        ContactsDataController.getInstance().loadContactsOnSeparateThread();

        relativeLayout = (RelativeLayout) findViewById(R.id.rl_one);
        relativeLayout1 = (RelativeLayout) findViewById(R.id.rl_two);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.rl_three);

        setToolbar();
        Log.e("passdoecall", ",fdf,d,f" + SplashScreenViewController.ishavingPasscode);
        passCodeView = (PassCodeView) findViewById(R.id.pass_code_view);
        changePasscode = (PassCodeView) findViewById(R.id.pass_code_view2);

        //enter passcode view
        typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Regular.ttf");
        passCodeView.setTypeFace(typeFace);
        passCodeView.setEmptyDrawable(R.drawable.empty_dot);
        passCodeView.setFilledDrawable(R.drawable.filled_dot);
        passCodeView.setKeyTextColor(Color.parseColor("#ffffff"));

        if (SplashScreenViewController.ishavingPasscode) {
            setUserPasscode();
            //SplashScreenViewController.ishavingPasscode = false;
        } else {
            bindEvents();
            // renter passcodeview
            confrimpassCodeView = (PassCodeView) findViewById(R.id.pass_code_view1);
            confrimpassCodeView.setTypeFace(typeFace);
            confrimpassCodeView.setEmptyDrawable(R.drawable.empty_dot);
            confrimpassCodeView.setFilledDrawable(R.drawable.filled_dot);
            confrimpassCodeView.setKeyTextColor(Color.parseColor("#ffffff"));

            bindEvents1();
        }

        if (SettingsViewController.isFromChangePasscode) {
            SettingsViewController.isFromChangePasscode = false;
            changePasscode();
        }

        TextView textView = (TextView) findViewById(R.id.txt_skip);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserDataController.getInstance().currentUser.pinpasscode != null && !UserDataController.getInstance().currentUser.pinpasscode.isEmpty()) {
                    if (SplashScreenViewController.ishavingPasscode) {
                        //update passcide values  fro user
                        SplashScreenViewController.ishavingPasscode = false;
                        finish();
                        startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));

                    } else {
                        finish();
                    }
                } else {
                    //update passcide values  fro user
                    UserDataController.getInstance().updatePinPasscode("", false);
                    finish();
                    startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
                }

            }
        });
    }

    private void setToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        home = (ImageView) toolbar.findViewById(R.id.back);

        home.setImageResource(R.drawable.ic_back);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbartext = (TextView) toolbar.findViewById(R.id.toolbar_text);
        toolbartext.setText("Passcode");
    }

    private void bindEvents() {
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                PASSCODE = text;
                if (PASSCODE.length() > 0 && PASSCODE.length() == 4) {
                    relativeLayout.setVisibility(View.GONE);
                    relativeLayout1.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bindEvents1() {
        confrimpassCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                PASSCODE1 = text;
                if (PASSCODE1.length() > 0 && PASSCODE1.length() == 4) {
                    if (PASSCODE.equals(PASSCODE1)) {
                        //update passcide values  fro user
                        if (!SettingsViewController.isFromSettings) {
                            Log.e("isFromSettings1", "call" + SettingsViewController.isFromSettings);
                            UserDataController.getInstance().updatePinPasscode(PASSCODE, true);
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
                        } else {
                            Log.e("isFromSettings", "call" + SettingsViewController.isFromSettings);
                            UserDataController.getInstance().updatePinPasscode(PASSCODE, true);
                            finish();
                            SettingsViewController.isFromSettings = false;
                        }

                    } else {
                        confrimpassCodeView.setError(true);
                    }
                }

            }
        });
    }

    public void setUserPasscode() {
        if (UserDataController.getInstance().allUsers.size() > 0) {
            if (UserDataController.getInstance().currentUser.isSetPin) {
                final String UserPasscode = UserDataController.getInstance().currentUser.pinpasscode;
                Log.e("UserPasscode", "call" + UserPasscode);
                passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
                    @Override
                    public void onTextChanged(String text) {
                        if (UserPasscode.length() > 0 && text.length() > 0 && text.length() == 4) {
                            if (UserPasscode.equals(text)) {
                                SplashScreenViewController.ishavingPasscode = false;
                                startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
                            } else {
                                passCodeView.setError(true);
                            }
                        }
                    }
                });

            }

        }
    }

    private void changePasscode() {
        relativeLayout.setVisibility(View.GONE);
        relativeLayout1.setVisibility(View.GONE);
        relativeLayout2.setVisibility(View.VISIBLE);

        changePasscode.setTypeFace(typeFace);
        changePasscode.setEmptyDrawable(R.drawable.empty_dot);
        changePasscode.setFilledDrawable(R.drawable.filled_dot);
        changePasscode.setKeyTextColor(Color.parseColor("#ffffff"));

        changePasscode.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                if (text.length() > 0 && text.length() == 4) {
                    final String UserPasscode = UserDataController.getInstance().currentUser.pinpasscode;
                    if (UserPasscode.equals(text)) {
                        relativeLayout2.setVisibility(View.GONE);
                        relativeLayout1.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    } else {
                        changePasscode.setError(true);
                    }
                }
            }
        });
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                PASSCODE = text;
                if (PASSCODE.length() > 0 && PASSCODE.length() == 4) {
                    relativeLayout2.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                    relativeLayout1.setVisibility(View.VISIBLE);

                }
            }
        });

        confrimpassCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                PASSCODE1 = text;
                if (PASSCODE1.length() > 0 && PASSCODE1.length() == 4) {
                    if (PASSCODE.equals(PASSCODE1)) {
                        //update passcide values  fro user
                        UserDataController.getInstance().updatePinPasscode(PASSCODE, true);
                        finish();
                    } else {
                        confrimpassCodeView.setError(true);
                    }
                }

            }
        });

    }


    @Override
    public void onBackPressed() {    //when click on phone backbutton
        if (SplashScreenViewController.ishavingPasscode) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            finish();
        }

    }

}