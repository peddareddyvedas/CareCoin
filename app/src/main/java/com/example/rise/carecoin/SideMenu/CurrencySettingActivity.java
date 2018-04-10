package com.example.rise.carecoin.SideMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Alert.RefreshShowingDialog;
import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.Model.CurrencyModel;
import com.example.rise.carecoin.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Rise on 13/02/2018.
 */
public class CurrencySettingActivity extends AppCompatActivity {
    RecyclerView settingsRecyclerView;
    Toolbar toolbar;
    ImageView home, refresh;
    CurrencySettings device;
    TextView ccnVal_txt, toolbartext, txt_amount, txt_convertVal;
    RefreshShowingDialog refreshShowingDialog;
    ArrayList<CurrencyModel> currencyList;
    DecimalFormat df;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_currencysetting);
        ButterKnife.bind(this);
        if (UserDataController.getInstance().currentUser.decimalvalue!=null && !UserDataController.getInstance().currentUser.decimalvalue.isEmpty()){
            String decimalval=UserDataController.getInstance().currentUser.decimalvalue;
            Log.e("decimalval1","call"+decimalval);
            df = new DecimalFormat(decimalval);
        }else {
            df = new DecimalFormat("0.0");
        }

        refreshShowingDialog = new RefreshShowingDialog(this);
        UserDataController.getInstance().fetchUserData();
        setToolbar();
        init();
        gettingCurrencyDataFromSharedPreferences();
        refreshSelectedCurrencyInformation();


    }

    public void gettingCurrencyDataFromSharedPreferences() {
        currencyList = new ArrayList<CurrencyModel>();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("MyCurrencyObject", "");
        if (json != null) {
            Type type = new TypeToken<ArrayList<CurrencyModel>>() {
            }.getType();
            ArrayList<CurrencyModel> currencyModelArrayList = gson.fromJson(json, type);
            if (currencyModelArrayList != null) {
                Log.e("currencyModelArrayList", "call" + currencyModelArrayList.size());
                currencyList = currencyModelArrayList;
            }
            if (currencyList != null) {
                final int selectedPosition = currencyList.indexOf(CurrencyController.getInstance().selectedCurrencyModel);
                Log.e("selectedPosition", "call" + selectedPosition);
            /*settingsRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    // Call smooth scroll
                    settingsRecyclerView.smoothScrollToPosition(selectedPosition);
                }
            });*/
            }
        }
    }

    public void refreshSelectedCurrencyInformation() {

        if (UserDataController.getInstance().currentUser != null) {
            txt_amount.setText("" + UserDataController.getInstance().currentUser.avaliablebalance);
            if (CurrencyController.getInstance().selectedCurrencyModel != null) {
                double selectedCurrencyValue = UserDataController.getInstance().currentUser.avaliablebalance * CurrencyController.getInstance().selectedCurrencyModel.getPriceByUSD();
                String selectedValue = df.format(selectedCurrencyValue);
                Log.e("selectedValue", "call" + selectedValue);
                txt_convertVal.setText("" + selectedValue + CurrencyController.getInstance().selectedCurrencyModel.getCurrencySymbol());
                ccnVal_txt.setText("" + df.format(CurrencyController.getInstance().selectedCurrencyModel.getPriceByUSD()) + "" + CurrencyController.getInstance().selectedCurrencyModel.getCurrencySymbol());
            }
        } else {
            txt_amount.setText("568.43");
        }
    }

    private void init() {
        txt_amount = (TextView) findViewById(R.id.textnumber);
        txt_convertVal = (TextView) findViewById(R.id.txt_convertVal);

        settingsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        device = new CurrencySettings(CurrencyController.getInstance().currencyList, getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        settingsRecyclerView.setLayoutManager(horizontalLayoutManager);
        settingsRecyclerView.setAdapter(device);
    }

    private void setToolbar() {

        ccnVal_txt = (TextView) findViewById(R.id.ccnvalue);
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
        toolbartext.append(getString(R.string.currentsettings));
    }

    // Step 1:-
    public class CurrencySettings extends RecyclerView.Adapter<CurrencySettings.ViewHolder> {

        // step 3:-
        List<CurrencyModel> horizontalList = Collections.emptyList();
        Context ctx;


        public CurrencySettings(List<CurrencyModel> horizontalList, Context ctx) {
            this.horizontalList = horizontalList;
            this.ctx = ctx;
        }

        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.currencysettings_layout, parent, false);

            ViewHolder myViewHolder = new ViewHolder(view, ctx, horizontalList);
            return myViewHolder;
        }

        //step 6:-
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ImageView image = (ImageView) holder.itemView.findViewById(R.id.tick);

            CurrencyModel objCurrencyModel = CurrencyController.getInstance().currencyList.get(position);
            holder.currencySymbolTextView.setText(objCurrencyModel.getCurrencySymbol());
            holder.currencyNameTextView.setText(objCurrencyModel.getCurrencyName());
            if (objCurrencyModel.getCurrencyShortName().equals(CurrencyController.getInstance().selectedCurrencyModel.getCurrencyShortName())) {
                Log.e("if", "called");
                image.setVisibility(View.VISIBLE);
                RelativeLayout relative = (RelativeLayout) holder.itemView.findViewById(R.id.relativesetting);
                relative.setBackgroundColor(Color.parseColor("#d7f1ed"));

            } else {
                Log.e("else", "called");
                image.setVisibility(View.GONE);
                RelativeLayout relative = (RelativeLayout) holder.itemView.findViewById(R.id.relativesetting);
                relative.setBackgroundColor(Color.parseColor("#ffffff"));

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    CurrencyController.getInstance().selectedCurrencyModel = CurrencyController.getInstance().currencyList.get(position);
                    refreshSelectedCurrencyInformation();
                    Log.e("currenyVal", "cal" + CurrencyController.getInstance().selectedCurrencyModel.getCurrencyShortName());
                    UserDataController.getInstance().updateSelectedCurrency(CurrencyController.getInstance().selectedCurrencyModel.getCurrencyShortName());
                    notifyDataSetChanged();
                }
            });
        }

        // step 4:-
        @Override
        public int getItemCount() {
            if (horizontalList != null) {
                return horizontalList.size();
            }
            return 0;

        }

        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            TextView currencyNameTextView, currencySymbolTextView;
            ArrayList<String> arrayList = new ArrayList<String>();
            Context ctx;
            RelativeLayout layout;
            ImageView image;

            public ViewHolder(View itemView, Context ctx, final List<CurrencyModel> arrayList) {
                super(itemView);
                this.ctx = ctx;
                currencyNameTextView = (TextView) itemView.findViewById(R.id.devicename);
                layout = (RelativeLayout) itemView.findViewById(R.id.relativesetting);
                currencySymbolTextView = (TextView) itemView.findViewById(R.id.dollerimage);
                image = (ImageView) itemView.findViewById(R.id.tick);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {


            }
        }
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}

