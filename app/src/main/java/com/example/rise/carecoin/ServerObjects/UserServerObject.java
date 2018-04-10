package com.example.rise.carecoin.ServerObjects;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Created by WAVE on 2/27/2018.
 */

public class UserServerObject {

    @SerializedName("personalinfo")
    public ArrayList <JsonObject>personalinfo;

    @SerializedName("mailid")
    public String mailid;

    @SerializedName("password")
    public String password;

    @SerializedName("register_time")
    public String register_time;
     // for veerification.
    @SerializedName("attempt_time")
    public String attempt_time;

    @SerializedName("otp")
    public String otp;

    @SerializedName("to")
    public String to ;

    @SerializedName("currentpassword")
    public String currentpassword ;

    @SerializedName("newpassword")
    public String newpassword;

    @SerializedName("deviceid")
    public String deviceid;

    @SerializedName("deviceToken")
    public String deviceToken;

    //////2.currentpassword,> 3.newpassword

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("response")
    public String response;

    @SerializedName("message")
    public String message;

    @SerializedName("walletId")
    public String walletid;

    @SerializedName("address")
    public String address;

    @SerializedName("from")
    public String from;

    @SerializedName("TransactionHistory")
    public JsonObject transactionsHistory;

    @SerializedName("transactions")
    public ArrayList<TransactionServerObject> Transaction;
}
