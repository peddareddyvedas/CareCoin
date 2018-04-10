package com.example.rise.carecoin.Transaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import butterknife.ButterKnife;

/**
 * Created by WAVE on 2/13/2018.
 */

public class ReceivedTransactionViewController extends Fragment {
    View view;
    RecyclerView recyclerView;
    ActivityTableViewCell activityTableViewCell;
    ArrayList<Transaction> allTransactionArray;
    SimpleDateFormat dateFormat;
    IntentFilter timeChangeIntentFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_receive_transaction, container, false);
        ButterKnife.bind(view);
        processTransactionData();
        setResultRecyclerViewData(view);

        timeChangeIntentFilter=new IntentFilter();
        timeChangeIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeChangeIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeChangeIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        getActivity().registerReceiver(m_timeChangedReceiver,timeChangeIntentFilter);

        return view;
    }
    public void processTransactionData(){
        dateFormat = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss a");
        allTransactionArray =new ArrayList<Transaction>();
        allTransactionArray = TransactionDataController.getInstance().getTransactionArrayFor("recieved");
        ArrayList<Transaction> rewardArray = TransactionDataController.getInstance().getTransactionArrayFor("reward");
        allTransactionArray.addAll(rewardArray);
        allTransactionArray = TransactionDataController.getInstance().sortTransactionsBasedOnTime(allTransactionArray);
        Collections.reverse(allTransactionArray);

    }
    public void setResultRecyclerViewData(View view){
        recyclerView =(RecyclerView)view.findViewById(R.id.recyclerview_activity);
        activityTableViewCell = new ActivityTableViewCell(getActivity(), allTransactionArray);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(activityTableViewCell);
        recyclerView.setHasFixedSize(true);
        activityTableViewCell.notifyDataSetChanged();

    }
    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED))
            {
                Log.e("timechange","call"+m_timeChangedReceiver);
                activityTableViewCell.notifyDataSetChanged();

            }
        }
    };

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(m_timeChangedReceiver);
    }

    public class ActivityTableViewCell extends RecyclerView.Adapter<ActivityTableViewCell.ViewHolder> {

        Context ctx;
        ArrayList<Transaction> arrayList=new ArrayList<>();
        String nowTestDateString="";


        public ActivityTableViewCell(Context ctx,ArrayList<Transaction> receiveArrayList) {
            this.ctx = ctx;
            this.arrayList=receiveArrayList;

        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            Context ctx;
            TextView txt_name,txt_date,txt_val,txt_notes;
            ImageView imageView;

            public ViewHolder(View itemView, Context ctx) {
                super(itemView);
                this.ctx = ctx;

                txt_name=(TextView)itemView.findViewById(R.id.coinname);
                txt_date=(TextView)itemView.findViewById(R.id.coindate);
                txt_val=(TextView)itemView.findViewById(R.id.textVal);
                txt_notes = (TextView) itemView.findViewById(R.id.addressnotes);

                imageView=(ImageView)itemView.findViewById(R.id.img_icon);
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
            return arrayList.size();
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            Transaction objTransactionModel = arrayList.get(position);
           // checkTransactionType(objTransactionModel.getTransactiontype(),holder);


          /*  holder.imageView.setBackgroundResource(R.drawable.ic_received);
*/
            try {
                 if (objTransactionModel.getTransactiontype().equals("recieved")) {
                    holder.imageView.setImageResource(R.drawable.ic_received);
                    holder.txt_name.setText(objTransactionModel.getFromAddress());
                     if (objTransactionModel.getNotes().isEmpty()) {
                         holder.txt_notes.setText("No Message");
                     } else {
                         holder.txt_notes.setText(objTransactionModel.getNotes());

                     }

                } else if (objTransactionModel.getTransactiontype().equals("reward")) {
                    holder.imageView.setImageResource(R.drawable.ic_reward);
                    holder.txt_name.setText("CareCoin Reward");
                     if (objTransactionModel.getNotes().isEmpty()) {
                         holder.txt_notes.setText("No Message");
                     } else {
                         holder.txt_notes.setText(objTransactionModel.getNotes());

                     }
                }

                holder.txt_val.setText(""+objTransactionModel.getAmount()+" "+"CCN");
                holder.txt_date.setText(""+TransactionDataController.getInstance().convertTimestampToAgoFromate(objTransactionModel.getDateTimeStamp()));


            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TransactionsDetailsViewController.objTransaction= allTransactionArray.get(position);
                    Intent intent =new Intent(getActivity(),TransactionsDetailsViewController.class);
                    startActivity(intent);

                }
            });


        }
    }

    public void checkTransactionType(String type,ActivityTableViewCell.ViewHolder viewHolder){

        if (type.equals("sent")){
            viewHolder.imageView.setImageResource(R.drawable.ic_sent);
        }else if (type.equals("recieved")){
            viewHolder.imageView.setImageResource(R.drawable.ic_received);

        }else if (type.equals("reward")){
            viewHolder.imageView.setImageResource(R.drawable.ic_reward);

        }

    }
}
