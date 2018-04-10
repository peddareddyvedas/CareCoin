package com.example.rise.carecoin.HomeModule;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Controllers.ApisController;
import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.LoginModule.SplashScreenViewController;
import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.Transaction.TransactionTabsActivity;
import com.example.rise.carecoin.Transaction.TransactionsDetailsViewController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.grantland.widget.AutofitTextView;

import static com.example.rise.carecoin.R.id.txt_notes;

/**
 * Created by Vedas on 11/10/2016.
 */

public class ActivityFragment extends Fragment {

    View view;
    RecyclerView resultRecyclerView;
    ActivityTableViewCell activityTableViewCell;
    ArrayList<Transaction> transactionArrayList;
    RelativeLayout recyclerlayout, rl_receive, rl_send;
    TextView line_receive, line_send, txt_amount, txt_currencyVal;
    IntentFilter timeChangeIntentFilter;
    TextView btn_showmore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.active_fragment, container, false);
        ButterKnife.bind(this, view);
        setResultRecyclerViewData(view);
        setActions(view);
        refreshSelectedCurrencyInformation();

        timeChangeIntentFilter = new IntentFilter();
        timeChangeIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeChangeIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeChangeIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);

        getActivity().registerReceiver(m_timeChangedReceiver, timeChangeIntentFilter);

        if (SplashScreenViewController.isFromNotification) {
            TransactionDataController.getInstance().fetchtransactionData();
            activityTableViewCell.notifyDataSetChanged();
            //SplashScreenViewController.isFromNotification=false;
        }
        btn_showmore = (TextView) view.findViewById(R.id.btn_showmore);


        return view;
    }

    @OnClick(R.id.btn_showmore)
    public void loadTransactions() {
        startActivity(new Intent(getActivity(), TransactionTabsActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "call");
        refreshSelectedCurrencyInformation();
        EventBus.getDefault().register(this);
        refreshTransactions();

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

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ReceiveFragment.MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);
        String resultData = event.message.trim();
        if (resultData.equals("refreshMoney")) {
            Log.e("sidemenuMessageevent", "call" + event.message);
            refreshSelectedCurrencyInformation();

        } else if (resultData.equals("refreshCurrencyValue")) {

        } else if (resultData.equals("refreshNotification")) {
            Log.e("LanguageupdateInfo", "Called");
            refreshTransactions();

        }
    }

    private void refreshTransactions() {
        ApisController.getInstance().currentbalanceApiExecution();
        processTransactionData("recieved");
        line_send.setVisibility(View.GONE);
        line_receive.setVisibility(View.VISIBLE);
        activityTableViewCell.notifyDataSetChanged();
    }

    public void refreshSelectedCurrencyInformation() {
        DecimalFormat df ;
        if (UserDataController.getInstance().currentUser.decimalvalue!=null && !UserDataController.getInstance().currentUser.decimalvalue.isEmpty()){
            String decimalval=UserDataController.getInstance().currentUser.decimalvalue;
            Log.e("decimalval1","call"+decimalval);
            df = new DecimalFormat(decimalval);
        }else {
            df = new DecimalFormat("0.0");
        }
        if (UserDataController.getInstance().currentUser != null) {

            txt_amount.setText("" + UserDataController.getInstance().currentUser.avaliablebalance);
            if (CurrencyController.getInstance().selectedCurrencyModel != null) {
                double selectedCurrencyValue = UserDataController.getInstance().currentUser.avaliablebalance * CurrencyController.getInstance().selectedCurrencyModel.getPriceByUSD();
                String selectedValue = df.format(selectedCurrencyValue);
                Log.e("selectedValue", "call" + selectedValue);
                txt_currencyVal.setText("" + selectedValue + CurrencyController.getInstance().selectedCurrencyModel.getCurrencySymbol());
            }
        } else {
            txt_amount.setText("568.43");
        }
    }

    public void setActions(final View view) {
        txt_amount = (TextView) view.findViewById(R.id.textnumber);
        txt_currencyVal = (TextView) view.findViewById(R.id.txt_currencyVal);
        //
        rl_receive = (RelativeLayout) view.findViewById(R.id.rl_receive);
        rl_send = (RelativeLayout) view.findViewById(R.id.rl_send);
        line_receive = (TextView) view.findViewById(R.id.line_receive);
        line_send = (TextView) view.findViewById(R.id.line_send);

        processTransactionData("recieved");
        line_receive.setVisibility(View.VISIBLE);
        line_send.setVisibility(View.GONE);
        setResultRecyclerViewData(view);
        activityTableViewCell.notifyDataSetChanged();

        rl_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                processTransactionData("recieved");
                line_send.setVisibility(View.GONE);
                line_receive.setVisibility(View.VISIBLE);
                //   setResultRecyclerViewData(view);
                activityTableViewCell.notifyDataSetChanged();

            }
        });
        rl_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                processTransactionData("sent");
                // setResultRecyclerViewData(view);
                line_receive.setVisibility(View.GONE);
                line_send.setVisibility(View.VISIBLE);
                activityTableViewCell.notifyDataSetChanged();

            }
        });
    }

    public void processTransactionData(String transactionType) {
        transactionArrayList = new ArrayList<Transaction>();
        TransactionDataController.getInstance().fetchtransactionData();

        transactionArrayList = TransactionDataController.getInstance().getTransactionArrayFor(transactionType);
        transactionArrayList = TransactionDataController.getInstance().sortTransactionsBasedOnTime(transactionArrayList);
        Collections.reverse(transactionArrayList);

        if (transactionType.equals("recieved")) {
            ArrayList<Transaction> rewardArray = TransactionDataController.getInstance().getTransactionArrayFor("reward");
            transactionArrayList.addAll(rewardArray);
            transactionArrayList = TransactionDataController.getInstance().sortTransactionsBasedOnTime(transactionArrayList);
            Collections.reverse(transactionArrayList);

        }
    }

    public void setResultRecyclerViewData(View view) {
        recyclerlayout = (RelativeLayout) view.findViewById(R.id.recyclerlayout);
        recyclerlayout.setBackgroundResource(R.drawable.recyclerviewborders);
        GradientDrawable gd = (GradientDrawable) recyclerlayout.getBackground().getCurrent();
        gd.setColor(Color.parseColor("#d7f1ed"));

        resultRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_activity);
        activityTableViewCell = new ActivityTableViewCell(getActivity());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity());
        resultRecyclerView.setLayoutManager(horizontalLayoutManager);
        resultRecyclerView.setAdapter(activityTableViewCell);
        resultRecyclerView.setHasFixedSize(true);
        activityTableViewCell.notifyDataSetChanged();
    }

    public class ActivityTableViewCell extends RecyclerView.Adapter<ActivityTableViewCell.ViewHolder> {

        Context ctx;

        public ActivityTableViewCell(Context ctx) {
            this.ctx = ctx;


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Context ctx;
            TextView txt_name, txt_date, txt_val, txt_notes;
            ImageView imageView;

            public ViewHolder(View itemView, Context ctx) {
                super(itemView);
                this.ctx = ctx;

                txt_name = (TextView) itemView.findViewById(R.id.coinname);
                txt_date = (TextView) itemView.findViewById(R.id.coindate);
                txt_val = (TextView) itemView.findViewById(R.id.textVal);
                txt_notes = (TextView) itemView.findViewById(R.id.addressnotes);
                imageView = (ImageView) itemView.findViewById(R.id.img_icon);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_all_items, parent, false);
            ViewHolder contactViewHolder = new ViewHolder(itemView, ctx);

            return contactViewHolder;
        }

        @Override
        public int getItemCount() {
            return transactionArrayList.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Transaction objTransactionModel = transactionArrayList.get(position);

            try {

                if (objTransactionModel.getTransactiontype().equals("sent")) {
                    holder.imageView.setImageResource(R.drawable.ic_sent);
                    holder.txt_name.setText(objTransactionModel.getToAddress());
                    if (objTransactionModel.getNotes().isEmpty()) {
                        holder.txt_notes.setText("No Message");
                    } else {
                        holder.txt_notes.setText(objTransactionModel.getNotes());

                    }
                } else if (objTransactionModel.getTransactiontype().equals("recieved")) {
                    holder.imageView.setImageResource(R.drawable.ic_received);
                    holder.txt_name.setText(objTransactionModel.getFromAddress());
                    if (objTransactionModel.getNotes().isEmpty()) {
                        holder.txt_notes.setText("No Message");
                    } else {
                        holder.txt_notes.setText(objTransactionModel.getNotes());
                    }

                } else if (objTransactionModel.getTransactiontype().equals("reward")) {
                    holder.imageView.setImageResource(R.drawable.ic_reward);
                    holder.txt_name.setText("Care Coin Reward");
                    if (objTransactionModel.getNotes().isEmpty()) {
                        holder.txt_notes.setText("Care Coin Reward");
                    } else {
                        holder.txt_notes.setText(objTransactionModel.getNotes());

                    }
                }
                holder.txt_val.setText("" + objTransactionModel.getAmount() + " " + "CCN");
                holder.txt_date.setText("" + TransactionDataController.getInstance().convertTimestampToAgoFromate(objTransactionModel.getDateTimeStamp()));
                Log.e("ActivityTimeStamp", objTransactionModel.getDateTimeStamp());

            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TransactionsDetailsViewController.objTransaction = transactionArrayList.get(position);
                    Intent intent = new Intent(getActivity(), TransactionsDetailsViewController.class);
                    startActivity(intent);

                }
            });


        }

    }

    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                Log.e("timechange", "call" + m_timeChangedReceiver);
                activityTableViewCell.notifyDataSetChanged();

            }
        }
    };

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(m_timeChangedReceiver);
    }
}