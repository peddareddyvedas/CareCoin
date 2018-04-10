package com.example.rise.carecoin.HomeModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.rise.carecoin.Controllers.ApisController;
import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.Controllers.LoginServerDataController;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.LoginModule.SplashScreenViewController;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.SideMenu.ContactsViewController;
import com.example.rise.carecoin.Transaction.TransactionTabsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class HomeActivityViewController extends AppCompatActivity implements SideMenuViewController.FragmentDrawerListener {
    Toolbar toolbar;
    //this is Fragment which handles the Navigationdrawer items.
    SideMenuViewController drawerFragment;
    DrawerLayout drawer;
    ImageView back, add;
    FrameLayout frameLayout;
    TextView tool_text;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        init();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        initializeDrawer();
        setupTabIcons();

        notification();

        if (ContactsViewController.isFromContacts) {
            Log.e("isconfrim", "call");
            tabLayout.getTabAt(2).select();
            ContactsViewController.isFromContacts=false;
        }
        if (ConfirmSendViewController.isTransactionDone) {
            Log.e("isconfrim", "call");
            tabLayout.getTabAt(1).select();
            ConfirmSendViewController.isTransactionDone=false;
        }

    }

    private void notification() {

        UserDataController.getInstance().fetchUserData();
        CurrencyController.getInstance().fillContext(getApplicationContext());
        TransactionDataController.getInstance().fetchtransactionData();

        Log.e("MoveTodoctor", "" + SplashScreenViewController.isFromNotification);

        if (SplashScreenViewController.isFromNotification) {
            SplashScreenViewController.isFromNotification = false;
            LoginServerDataController.getInstance().executeTransactionsRefreshServerAPI();
            ConfirmSendViewController.isTransactionDone = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResumeHome", "call");
        drawerFragment.loadAdminData();

    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStartHome", "call");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStopHome", "call");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPauseHome", "call");
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.toolbar_icon);//Spectrum
        back.setBackgroundResource(R.drawable.ic_sidemenu);
        tool_text = (TextView) toolbar.findViewById(R.id.toolbar_text);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        add = (ImageView) toolbar.findViewById(R.id.img_share);
        // add.setImageResource(R.drawable.ic_add);
        add.setVisibility(View.VISIBLE);

        tool_text.setText("Home");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    //initializing the navigation drawer
    public void initializeDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment = (SideMenuViewController) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawer, toolbar);

        drawerFragment.setDrawerListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("openDrawer", "call");
                drawer.openDrawer(GravityCompat.START);// for open the side menu
            }
        });
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        Log.e("onDrawerItemSelected","call"+position);
        drawer.closeDrawer(GravityCompat.END);

    }


    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Receive");
        tabOne.setTextColor(Color.parseColor("#ffffff"));
        tabOne.setTextSize(16);
        tabOne.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_receive, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Activity");
        tabTwo.setTextColor(Color.parseColor("#ffffff"));
        tabTwo.setTextSize(16);
        tabTwo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_activity, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Send");
        tabThree.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tabThree.setTextColor(Color.parseColor("#ffffff"));
        tabThree.setTextSize(16);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_send, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ReceiveFragment(), "");
        adapter.addFrag(new ActivityFragment(), "");
        adapter.addFrag(new SendFragment(), "");
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }


    ///////////
    @Override
    public void onBackPressed() {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Log.e("fragment", "position 0");
            }
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
