package com.example.rise.carecoin.HomeModule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Alert.AlertShowingDialog;
import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.LoginModule.LoginViewController;
import com.example.rise.carecoin.LoginModule.PassCodeViewController;
import com.example.rise.carecoin.LoginModule.PersonalinfoViewController;
import com.example.rise.carecoin.Model.User;
import com.example.rise.carecoin.PushNotification.NotificationUtils;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.example.rise.carecoin.Settings.SettingsViewController;
import com.example.rise.carecoin.SideMenu.ContactsViewController;
import com.example.rise.carecoin.SideMenu.CurrencySettingActivity;
import com.example.rise.carecoin.SideMenu.SupportActivity;
import com.example.rise.carecoin.Transaction.TransactionTabsActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 *
 */
public class SideMenuViewController extends Fragment {

    View view;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    FragmentDrawerListener drawerListener;
    View containerView;
    TextView txt_email, edit_username;
    LinearLayout main;
    TextView addpayee, currentsett, tansfer, support, logout, changepass;
    RelativeLayout rlUserLogin, rl_cgPsw, rladdpayee, rlcurrentset, rltransfer, rlsupport, rllogout;
    CircleImageView profileImage;

    RefreshShowingDialog refreshShowingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment_drawer_list, container, false);
        ButterKnife.bind(this, view);
        refreshShowingDialog = new RefreshShowingDialog(getActivity());

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "call");
        if (UserDataController.getInstance().currentUser != null) {
            loadAdminData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart", "call");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop", "call");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "call");

    }

    @OnClick({R.id.rlUserLogin})
    public void onClickaction(View view) {
        mDrawerLayout.closeDrawer(containerView);
        ContactsDataController.getInstance().selectedContactModel = null;
        startActivity(new Intent(getActivity(), PersonalinfoViewController.class));

    }

    public void init() {
        rlUserLogin = (RelativeLayout) view.findViewById(R.id.rlUserLogin);
        rladdpayee = (RelativeLayout) view.findViewById(R.id.rl_addpayes);
        rlcurrentset = (RelativeLayout) view.findViewById(R.id.rl_currentsett);
        rltransfer = (RelativeLayout) view.findViewById(R.id.rl_transfer);
        rlsupport = (RelativeLayout) view.findViewById(R.id.rl_support);
        rl_cgPsw = (RelativeLayout) view.findViewById(R.id.rl_cgpsw);
        rllogout = (RelativeLayout) view.findViewById(R.id.rl_logout);
        main = (LinearLayout) view.findViewById(R.id.main);
        main = (LinearLayout) view.findViewById(R.id.main);
        profileImage = (CircleImageView) view.findViewById(R.id.imageView_profile);
        txt_email = (TextView) view.findViewById(R.id.txt_email);
        edit_username = (TextView) view.findViewById(R.id.textUsername);
        addpayee = (TextView) view.findViewById(R.id.addpayes);
        currentsett = (TextView) view.findViewById(R.id.currentsett);
        logout = (TextView) view.findViewById(R.id.logout);
        tansfer = (TextView) view.findViewById(R.id.transfer);
        //   support = (TextView) changepass.findViewById(R.id.support);
        logout = (TextView) view.findViewById(R.id.logout);
        changepass = (TextView) view.findViewById(R.id.changepassword);

        profileImage.setOnClickListener(mProfileListener);

    }

    public void loadAdminData() {
        CircleImageView adminPic = (CircleImageView) view.findViewById(R.id.imageView_profile);
        TextView txt_name = (TextView) view.findViewById(R.id.textUsername);
        TextView txt_email = (TextView) view.findViewById(R.id.txt_email);

        UserDataController.getInstance().fetchUserData();
        User user = UserDataController.getInstance().currentUser;
        if (user != null) {
            String email = user.userid;
            txt_email.setText("" + email);

            if (user.username != null) {
                String name = user.username;
                txt_name.setText("" + name);
                Log.e("name", "" + name);

            }
            if (UserDataController.getInstance().currentUser.mprofilepicturepath != null) {
                Log.e("loadcurrentuser", "call");
                adminPic.setImageBitmap(convertByteArrayTOBitmap(UserDataController.getInstance().currentUser.mprofilepicturepath));
            }
        }
    }

    View.OnClickListener mProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            startActivity(new Intent(getActivity(), PersonalinfoViewController.class));

        }
    };

    @OnClick({R.id.rl_addpayes})
    public void addpayes() {
        mDrawerLayout.closeDrawer(containerView);
        ContactsDataController.getInstance().selectedContactModel = null;

       // startActivity(new Intent(getActivity(), PassCodeViewController.class));
    }

    @OnClick({R.id.rl_currentsett})
    public void currentsett(View view) {
        ContactsDataController.getInstance().selectedContactModel = null;
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), CurrencySettingActivity.class));

    }

    @OnClick({R.id.rl_transfer})
    public void transfer(View view) {
        ContactsDataController.getInstance().selectedContactModel = null;
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), TransactionTabsActivity.class));
    }

    @OnClick({R.id.rl_support})
    public void support() {
        EventBus.getDefault().post(new ContactsViewController.MessageEvent("removeContacts"));
        ContactsDataController.getInstance().selectedContactModel = null;
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), SupportActivity.class));
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }

    @OnClick({R.id.rl_cgpsw})
    public void ChangePsw() {
        ContactsDataController.getInstance().selectedContactModel = null;
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), SettingsViewController.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.rl_logout)
    public void logout() {
        mDrawerLayout.closeDrawer(containerView);
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent_selector);

        Button no = (Button) dialog.findViewById(R.id.btn_no);
        Button yes = (Button) dialog.findViewById(R.id.btn_yes);
        TextView txt_msg = (TextView) dialog.findViewById(R.id.textview);
        TextView txt_alert = (TextView) dialog.findViewById(R.id.textview1);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                // deleteDatabaseData();
                if (isConn()) {
                    refreshShowingDialog.showAlert();
                    loadLogoutServerApi();

                } else {
                    new AlertShowingDialog(getActivity(), "No Internet Connection");
                }

            }
        });
    }

    ////////////////////////////////////logoutaps/////////////////////////////
    private void loadLogoutServerApi() {

        SharedPreferences tokenPreferences = getActivity().getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        String tokenId = tokenPreferences.getString("tokenid", null);
        String deviceId = tokenPreferences.getString("deviceid", null);

        Log.e("tokenPreferences", "call " + tokenId + "ids" + deviceId);


        final UserServerObject requestBody = new UserServerObject();
        requestBody.mailid = UserDataController.getInstance().currentUser.userid;

        requestBody.deviceid = deviceId;
        Log.e("logtellid", "" + deviceId);

        requestBody.deviceToken = tokenId;
        Log.e("logfirebase", "" + tokenId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.logout(requestBody);
        callable.enqueue(new retrofit2.Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, final retrofit2.Response<UserServerObject> response) {

                refreshShowingDialog.hideRefreshDialog();
                String statusCode = response.body().response;
                String message = response.body().message;
                Log.e("logout", "CALL" + statusCode + "" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        deleteDatabaseData();

                    }
                }

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

                refreshShowingDialog.hideRefreshDialog();
                failurealert();
            }
        });

    }

    public void deleteDatabaseData() {
        /*///////delete social media login details
        SharedPreferences preferences = getActivity().getSharedPreferences("socialMediaLoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
*/
        ContactsDataController.getInstance().selectedContactModel = null;
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getActivity());

        TransactionDataController.getInstance().deleteTransactionData(TransactionDataController.getInstance().allTransactions);
        TransactionDataController.getInstance().allTransactions.removeAll(TransactionDataController.getInstance().allTransactions);
        Log.e("allTransactions", "transactiondata sucessfully" + TransactionDataController.getInstance().allTransactions.size());

        UserDataController.getInstance().deleteUserData(UserDataController.getInstance().allUsers);
        UserDataController.getInstance().currentUser = null;

        startActivity(new Intent(getActivity(), LoginViewController.class));
    }

    public void failurealert() {

        Log.e("responsealert", "call");
        final Dialog failurealert = new Dialog(getActivity());
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
                    loadLogoutServerApi();
                } else {
                    new AlertShowingDialog(getActivity(), "No Internet connection");

                }
            }


        });
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        ///for builtin button open close drawayer.
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();

                Log.e("onDrawerItemSelected", "call");

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
                //below line used to remove shadow of drawer
                mDrawerLayout.setScrimColor(Color.TRANSPARENT);
                Log.e("onDrawerSlide", "call");

                SharedPreferences preferences = getActivity().getSharedPreferences("contactsDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

            }//this method helps you to aside menu drawer

        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }


    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    public Bitmap convertByteArrayTOBitmap(byte[] profilePic) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;

    }


}
