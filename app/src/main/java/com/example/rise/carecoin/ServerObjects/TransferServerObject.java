package com.example.rise.carecoin.ServerObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WAVE on 2/28/2018.
 */

public class TransferServerObject {

    @SerializedName("mailid")
    public String mailid;

    @SerializedName("walletId")
    public String walletId;

    @SerializedName("password")
    public String password;

    @SerializedName("fromAddress")
    public String fromAddress;

    @SerializedName("toAddress")
    public String toAddress;

    @SerializedName("changeAddress")
    public String changeAddress;

    @SerializedName("amount")
    public int amount;

    @SerializedName("response")
    public String response;

    @SerializedName("notes")
    public String notes;

    @SerializedName("id")
    public String id;

    @SerializedName("message")
    public String message;


}
