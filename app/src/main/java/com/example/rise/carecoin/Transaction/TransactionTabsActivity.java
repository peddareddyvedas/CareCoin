package com.example.rise.carecoin.Transaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TransactionTabsActivity extends AppCompatActivity {
    ViewPagerAdapter adapter;
    TabLayout tabLayout;
    String key = null;
    ImageView img_share,back;
    RelativeLayout sharepage;
    private static final int REQUEST_WRITE_PERMISSION = 56;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintransaction);
        //
        setTollBar();
        requestPermissions();
        try {
            Bundle bundle = getIntent().getExtras();
            key = bundle.getString("key");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (savedInstanceState == null) {
            if (key == null) {
                // Select this one
                Fragment fragment = new AllTransactionViewController();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            } else if (key.equals("receive")) {

                Fragment fragment = new ReceivedTransactionViewController();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            } else if (key.equals("send")) {
                Fragment fragment = new SendTransactionViewController();

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            }
        }
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllTransactionViewController());
        adapter.addFragment(new ReceivedTransactionViewController());
        adapter.addFragment(new SendTransactionViewController());
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.getTabAt(0).select();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (i == 0) {
                TextView imageView = new TextView(getApplicationContext());
                tabLayout.getTabAt(i).setCustomView(imageView);
                imageView.setText("All");
                imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                imageView.setTextColor(Color.parseColor("#ffffff"));
                imageView.setTextSize(17);


            } else if (i == 1) {
                TextView imageView = new TextView(getApplicationContext());
                tabLayout.getTabAt(i).setCustomView(imageView);
                imageView.setText("Received");
                imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                imageView.setTextColor(Color.parseColor("#ffffff"));
                imageView.setTextSize(17);
            } else if (i == 2) {
                TextView imageView = new TextView(getApplicationContext());
                tabLayout.getTabAt(i).setCustomView(imageView);
                imageView.setText("Sent");
                imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                imageView.setTextColor(Color.parseColor("#ffffff"));
                imageView.setTextSize(17);
            }

        }
        // Select this one
        AllTransactionViewController fragment = (AllTransactionViewController) adapter.getItem(0);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "All");
        fragmentTransaction.commitAllowingStateLoss();
        tabLayout.getTabAt(0).select();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("Position", String.valueOf(tab.getPosition()));

                if (tab.getPosition() == 0) {
                    Log.e("PositionInside", String.valueOf(tab.getPosition()));
                    AllTransactionViewController fragment = (AllTransactionViewController) adapter.getItem(0);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "All");
                    fragmentTransaction.commitAllowingStateLoss();

                } else if (tab.getPosition() == 1)
                {
                    Log.e("PositionInside", String.valueOf(tab.getPosition()));
                    ReceivedTransactionViewController fragment = (ReceivedTransactionViewController) adapter.getItem(1);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "Receive");
                    fragmentTransaction.commitAllowingStateLoss();

                }else if (tab.getPosition() == 2) {
                    Log.e("PositionInside", String.valueOf(tab.getPosition()));
                    SendTransactionViewController fragment = (SendTransactionViewController) adapter.getItem(2);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "Send");
                    fragmentTransaction.commitAllowingStateLoss();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        //private final List<String> mFragmentTitleList = new ArrayList<>();
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

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);

        }

    }
    public void setTollBar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharepage = (RelativeLayout) findViewById(R.id.rl_home);

        back = (ImageView) findViewById(R.id.toolbar_icon);
        back.setBackgroundResource(R.drawable.ic_home);
        TextView textView=(TextView)findViewById(R.id.toolbar_text);
        textView.setText("Transactions");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();

            }
        });
        img_share=(ImageView)findViewById(R.id.img_share);
        img_share.setBackgroundResource(R.drawable.ic_share);
        img_share.setVisibility(View.GONE);
    }
    private void requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }

    }
    @Override
    public void onBackPressed()
    {
        finish();
    }
}
