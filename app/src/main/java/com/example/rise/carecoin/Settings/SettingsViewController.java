package com.example.rise.carecoin.Settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Controllers.ApisController;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.LoginModule.ChangePasswordViewController;
import com.example.rise.carecoin.LoginModule.PassCodeViewController;
import com.example.rise.carecoin.LoginModule.RegisterViewController;
import com.example.rise.carecoin.LoginModule.SplashScreenViewController;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.SideMenu.AddpayeeActivity;
import com.example.rise.carecoin.SideMenu.ContactsViewController;
import com.example.rise.carecoin.SideMenu.CurrencySettingActivity;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by WAVE on 3/30/2018.
 */

public class SettingsViewController extends AppCompatActivity {
    Toolbar toolbar;
    ImageView back;
    TextView tool_text, txt_currencyType, txt_language, seekbarprogress,tVProgress;
    ToggleButton pinToggle;
    public static boolean isFromChangePasscode = false;
    public static boolean isFromSettings = false;
    private SeekBar simpleSeekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        ContactsDataController.getInstance().loadContactsOnSeparateThread();
        pinToggle = (ToggleButton) findViewById(R.id.toggle);
        txt_currencyType = (TextView) findViewById(R.id.currencttype);
        txt_language = (TextView) findViewById(R.id.language_txt);

        setToolbar();
        setToggleActions();
        setCurrenctType();
        setLanguage();

        tVProgress = (TextView) findViewById(R.id.tVProgress);
        seekbarprogress = (TextView) findViewById(R.id.seekbarprogress);
        simpleSeekBar = (SeekBar) findViewById(R.id.simpleSeekBar);
        simpleSeekBar.setMax(9);
        simpleSeekBar.setProgress(0);
        simpleSeekBar.getBackground().setColorFilter(Color.parseColor("#A6B2CA"), PorterDuff.Mode.SRC_IN);
        simpleSeekBar.getThumb().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        simpleSeekBar.getProgressDrawable().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int requiredProgress = progress + 1;
                tVProgress.setText("" + requiredProgress);

                String value = "0.";
                for (int i = 1; i <= requiredProgress; i++) {
                    value = value.concat("0");
                    Log.e("for", "" + value);
                }
                seekbarprogress.setText(value);
                UserDataController.getInstance().currentUser.decimalvalue=value;
                UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
               }
        });
}

    @Override
    public void onResume() {
        super.onResume();
        setCurrenctType();
        setLanguage();
    }

    private void setCurrenctType() {
        if (CurrencyController.getInstance().selectedCurrencyModel != null) {
            txt_currencyType.setText("" + CurrencyController.getInstance().selectedCurrencyModel.getCurrencyShortName());
        }

    }

    private void setLanguage() {
        if (UserDataController.getInstance().currentUser.preferdlanguage != null) {
            txt_language.setText("" + UserDataController.getInstance().currentUser.preferdlanguage);
        }
    }

    private void setToggleActions() {
        if (UserDataController.getInstance().allUsers.size() > 0) {
            Log.e("isSetPin", "call" + UserDataController.getInstance().currentUser.isSetPin);
            if (UserDataController.getInstance().currentUser.isSetPin) {
                pinToggle.setChecked(true);
                pinToggle.setBackgroundResource(R.drawable.ic_on);
            } else {
                pinToggle.setChecked(false);
                pinToggle.setBackgroundResource(R.drawable.ic_off);

            }
        }

        pinToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                if (isChecked) {

                    if (UserDataController.getInstance().currentUser.pinpasscode != null && !UserDataController.getInstance().currentUser.pinpasscode.isEmpty()) {
                        if (UserDataController.getInstance().currentUser.isSetPin) {
                            Log.e("offcall", "call");
                            UserDataController.getInstance().updatePinPasscode(UserDataController.getInstance().currentUser.pinpasscode, false);
                            pinToggle.setChecked(false);
                            pinToggle.setBackgroundResource(R.drawable.ic_off);
                        } else {
                            Log.e("ishavingpasscode", "call" + UserDataController.getInstance().currentUser.pinpasscode);
                            UserDataController.getInstance().updatePinPasscode(UserDataController.getInstance().currentUser.pinpasscode, true);
                            pinToggle.setChecked(true);
                            pinToggle.setBackgroundResource(R.drawable.ic_on);
                        }
                    } else {
                        Log.e("alertcall", "call");
                        alertDialogueDisablepasscode("Do you want to set your passcode ?");
                    }
                }

            }
        });

    }

    @OnClick(R.id.rl_aboutUs)
    public void aboutUsAction() {
        startActivity(new Intent(getApplicationContext(), AboutUsViewController.class));
    }

    @OnClick(R.id.rl_faq)
    public void faqAction() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerApisInterface.aboutus_url));
        startActivity(browserIntent);
    }

    @OnClick(R.id.rl_psw)
    public void changepasswordAction() {
        startActivity(new Intent(getApplicationContext(), ChangePasswordViewController.class));
    }

    @OnClick(R.id.rl_currency)
    public void currencyAction() {
        startActivity(new Intent(getApplicationContext(), CurrencySettingActivity.class));
    }
    @OnClick(R.id.rl_contacts)
    public void contacts() {
        startActivity(new Intent(getApplicationContext(), ContactsViewController.class));
    }

    @OnClick(R.id.rl_languages)
    public void languages() {
        startActivity(new Intent(getApplicationContext(), LanguageViewController.class));
    }

    @OnClick(R.id.rl_changepin)
    public void changepinaction() {
        if (UserDataController.getInstance().currentUser.pinpasscode != null && !UserDataController.getInstance().currentUser.pinpasscode.isEmpty()) {
            isFromChangePasscode = true;
            startActivity(new Intent(getApplicationContext(), PassCodeViewController.class));
        } else {
            new AlertShowingDialog(SettingsViewController.this, "you are not yet set your passcode", "info");
        }
    }

    @OnClick(R.id.rl_forgotpin)
    public void forgotpinaction() {
        if (UserDataController.getInstance().currentUser.pinpasscode != null && !UserDataController.getInstance().currentUser.pinpasscode.isEmpty()) {
            showingForgotPasscodeAlert();
        } else {
            new AlertShowingDialog(SettingsViewController.this, "you are not yet set your passcode", "info");

        }
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_back);

        tool_text = (TextView) toolbar.findViewById(R.id.toolbar_text);
        tool_text.setText("Settings");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void alertDialogueDisablepasscode(String message1) {
        Log.e("alert", "call");
        final Dialog emailDilog = new Dialog(this);
        emailDilog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        emailDilog.setCancelable(false);
        emailDilog.setCanceledOnTouchOutside(false);
        emailDilog.setCancelable(true);
        emailDilog.setContentView(R.layout.email_alert);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(emailDilog.getWindow().getAttributes());
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        emailDilog.getWindow().setAttributes(lp);
        emailDilog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        emailDilog.show();

        final TextView title = (TextView) emailDilog.findViewById(R.id.textview);
        final TextView message = (TextView) emailDilog.findViewById(R.id.message);
        title.setText("Message");
        message.setText(message1);

        final Button no = (Button) emailDilog.findViewById(R.id.btn_no);
        Button yes = (Button) emailDilog.findViewById(R.id.btn_yes);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinToggle.setChecked(false);
                pinToggle.setBackgroundResource(R.drawable.ic_off);
                emailDilog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinToggle.setChecked(true);
                pinToggle.setBackgroundResource(R.drawable.ic_on);
                emailDilog.dismiss();
                isFromSettings = true;
                startActivity(new Intent(getApplicationContext(), PassCodeViewController.class));
            }
        });
    }


    public void showingForgotPasscodeAlert() {
        final Dialog verifyDialog = new Dialog(this);
        verifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyDialog.setCancelable(false);
        verifyDialog.setCanceledOnTouchOutside(false);
        verifyDialog.setCancelable(true);
        verifyDialog.setContentView(R.layout.forgot_passcode_alert);
        verifyDialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        final EditText passwordTextFeild = (EditText) verifyDialog.findViewById(R.id.ed_code);
        TextView text_auth = (TextView) verifyDialog.findViewById(R.id.textview);
        final Button setPin = (Button) verifyDialog.findViewById(R.id.btn_setpin);
        CheckBox checkBox = (CheckBox) verifyDialog.findViewById(R.id.chk);
        Button cancel = (Button) verifyDialog.findViewById(R.id.btn_cancel);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    passwordTextFeild.setTransformationMethod(null);
                } else {
                    passwordTextFeild.setTransformationMethod(new PasswordTransformationMethod());
                }
                // cursor reset his position so we need set position to the end of text
                passwordTextFeild.setSelection(passwordTextFeild.getText().length());
            }
        });
        setPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordTextFeild.getText().toString().length() > 0) {
                    if (UserDataController.getInstance().currentUser.password.equals(passwordTextFeild.getText().toString())) {
                        verifyDialog.dismiss();
                        isFromSettings=true;
                        startActivity(new Intent(getApplicationContext(), PassCodeViewController.class));
                    } else {
                        new AlertShowingDialog(SettingsViewController.this, "Enter correct password", "Error");

                    }
                } else {
                    new AlertShowingDialog(SettingsViewController.this, "Please enter your password", "Error");

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDialog.dismiss();
            }
        });
        verifyDialog.show();

        /*@OnCheckedChanged(R.id.chk)
        public void onChecked(boolean checked) {
            if (checked) {
                passwordTextFeild.setTransformationMethod(null);
            } else {
                passwordTextFeild.setTransformationMethod(new PasswordTransformationMethod());

            }
            // cursor reset his position so we need set position to the end of text
            passwordTextFeild.setSelection(passwordTextFeild.getText().length());
        }*/

    }


}
