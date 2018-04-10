package com.example.rise.carecoin.DataBase;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.rise.carecoin.Model.Transaction;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rise on 26/02/2018.
 */

public class TransactionDataController  {

    public ArrayList<Transaction> allTransactions=new ArrayList<Transaction>();
     static TransactionDataController myObj;

    public static TransactionDataController getInstance() {
        if (myObj == null) {
            myObj = new TransactionDataController();
        }

        return myObj;
    }

    //insert the userdata into user table
    public void insertTransactionData(Transaction transactiondata) {
        Log.e("insertTransactionData","call");
        try {
            UserDataController.getInstance().helper.getTransactionDao().create(transactiondata);
            Log.e("insertTransactionData","call"+transactiondata.getNotes());
            fetchtransactionData();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Fetching all the user data
    public ArrayList<Transaction> fetchtransactionData() {
            allTransactions = null;
            allTransactions = new ArrayList<Transaction>();
            ArrayList<Transaction> transactionModels = new ArrayList<Transaction>(UserDataController.getInstance().currentUser.getTransactions());
            Log.e("call", "" + transactionModels);
            if (transactionModels != null) {
                allTransactions = transactionModels;
            }

        Log.e("fetching", "successfully" + allTransactions.size());
        return allTransactions;
    }

    //updating the userdata
    public void updateTransactionData(Transaction transaction) {
        try {
            UpdateBuilder<Transaction, Integer> updateBuilder = UserDataController.getInstance().helper.getTransactionDao().updateBuilder();
            updateBuilder.updateColumnValue("transactionID", transaction.getTransactionID());
            updateBuilder.updateColumnValue("transactionName", transaction.getTransactionName());
            updateBuilder.updateColumnValue("timeStamp", transaction.getDateTimeStamp());
            updateBuilder.updateColumnValue("transactiontype", transaction.getTransactiontype());
            updateBuilder.updateColumnValue("amount", transaction.getAmount());
            updateBuilder.updateColumnValue("careCoinAddress", transaction.getCareCoinAddress());
            updateBuilder.updateColumnValue("notes", transaction.getNotes());
            updateBuilder.updateColumnValue("toAddress", transaction.getToAddress());
            updateBuilder.updateColumnValue("fromAddress", transaction.getFromAddress());

            updateBuilder.where().eq("transactionID", transaction.getTransactionID());
            updateBuilder.update();
            Log.e("update data", "updated the data sucessfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Deleting all users in database
    public void deleteTransactionData(ArrayList<Transaction> transactions) {
        try {
            UserDataController.getInstance().helper.getTransactionDao().deleteBuilder().delete();
            Log.e("deletedata", "transactiondata sucessfully"+allTransactions.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public  ArrayList<Transaction> getTransactionArrayFor(String transactionType)
    {
        ArrayList<Transaction> filteredArray=new ArrayList<Transaction>();
        Log.e("testingdata", "transactiondata sucessfully"+allTransactions.size());

        for (Transaction objType:allTransactions)
        {

            if(objType.getTransactiontype().equals(transactionType))
            {
                filteredArray.add(objType);
            }
        }
        return filteredArray;
    }
    public ArrayList<Transaction> sortTransactionsBasedOnTime(ArrayList<Transaction> urineResults){
        Collections.sort(urineResults, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction s1, Transaction s2) {
                return s1.getDateTimeStamp().compareTo(s2.getDateTimeStamp());
            }
        });
        return urineResults;

    }
    public CharSequence convertTimestampToAgoFromate(String stringData) throws ParseException {


        long yourmilliseconds = (long) Double.parseDouble(stringData);
        Date past = new Date(yourmilliseconds * 1000);
        Log.e("sec", "" + yourmilliseconds * 1000);
        Log.e("sec", "" + past);

        Date now = new Date();

        long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
        long minutes= TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
        long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
        long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

        Log.e("TimeCheckSeconds",""+seconds);
        Log.e("TimeCheckMinutes",""+minutes);
        Log.e("TimeCheckHours",""+hours);
        Log.e("TimeCheckdays",""+days);

        if (seconds <0 || minutes <0 || hours < 0 || days <0)
        {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return dateformat.format(past);
        }

        if(seconds<60)
        {
            return seconds+ " Sec ago";
        }
        else if(minutes<60)
        {
            if ( minutes ==1) {
                return minutes + " Min ago";
            }
            return minutes+ " Mins ago";
        }
        else if(hours<24)
        {
            if ( hours ==1) {
                return hours + " Hour ago";
            }
            return hours + " Hours ago";

        }
        else
        {
            return days + " Days ago";
        }

    }

}
