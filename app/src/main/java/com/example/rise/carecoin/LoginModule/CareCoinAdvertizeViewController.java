package com.example.rise.carecoin.LoginModule;

/**
 * Created by WAVE on 5/29/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.example.rise.carecoin.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class CareCoinAdvertizeViewController extends AppCompatActivity {
    ViewPager mPager;
    // int currentPage = 0;
    Handler activityHandler = new Handler();
    Dialog activityDialog;
    ImageView imageView;
    RelativeLayout splashScreenLayout, viewPagerLayout;
    SharedPreferences sharedPreferences;
    SlidingImage_Adapter slideAdapter;
    TextView textView;

    SharedPreferences.Editor sharedPreferencesEditor;
    TextView gotit;

    int currentposition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator);
        ButterKnife.bind(this);
        init();
        Log.e("currentposition", "call"+currentposition);
        gotit.setText("Skip");


    }


    @OnClick(R.id.gotit)
    public void movetoLogin() {
        SharedPreferences.Editor editor = getSharedPreferences("AdvertiseInfo", MODE_PRIVATE).edit();
        editor.putString("isStored", "true");
        editor.apply();

        startActivity(new Intent(getApplicationContext(), LoginViewController.class));
    }


    private void init() {


        gotit = (TextView) findViewById(R.id.gotit);
        viewPagerLayout = (RelativeLayout) findViewById(R.id.viewPagerId);

        mPager = (ViewPager) findViewById(R.id.pager);

        slideAdapter = (new SlidingImage_Adapter(CareCoinAdvertizeViewController.this));
        mPager.setAdapter(slideAdapter);
        // mPager.setPageTransformer(true, new DepthPageTransformer());


        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(4 * density);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentposition=position;
                Log.e("currentposition", "call"+position);

                if (currentposition==0){

                    gotit.setText("SKIP");

                }else if (currentposition==1){
                    gotit.setText("SKIP");

                } else if (currentposition==2){
                    gotit.setText("Got it");

                }

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    public class SlidingImage_Adapter extends PagerAdapter {
        private Context context;

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        SlidingImage_Adapter(Context context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return CustomPagerEnum.values().length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];

            LayoutInflater inflater = LayoutInflater.from(context);

            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), container, false);
            if (customPagerEnum.getTitleResId() == CustomPagerEnum.URINE.getTitleResId())
            {

                Log.e("resourceID", "" + customPagerEnum.getTitleResId());
            } else if (customPagerEnum.getTitleResId() == CustomPagerEnum.BLOOD.getTitleResId())
            {
                Log.e("resourceID", "" + customPagerEnum.getTitleResId());

            } else if (customPagerEnum.getTitleResId() == CustomPagerEnum.TEAR.getTitleResId())
            {
                Log.e("resourceID", "" + customPagerEnum.getTitleResId());

            }

            container.addView(layout);
            return layout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    public enum CustomPagerEnum {

        URINE(R.layout.activity_carecoinwallet, 0),
        BLOOD(R.layout.activity_buysellcarecoin, 1),
        TEAR(R.layout.activity_tellafriend, 2);


        private int mTitleResId;
        private int mLayoutResId;


        CustomPagerEnum(int layoutResId, int id) {

            mLayoutResId = layoutResId;
            mTitleResId = id;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }

    }


}
