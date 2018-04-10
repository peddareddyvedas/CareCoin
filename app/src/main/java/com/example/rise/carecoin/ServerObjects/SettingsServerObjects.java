package com.example.rise.carecoin.ServerObjects;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rise on 02/03/2018.
 */

public class SettingsServerObjects {

 /*"mailid":"ramanareddy@gmail.com",
         "decimal":"0.00",
         "pin":"4321",
         "language":"Telugu",
         "security":true*/
    @SerializedName("mailid")
    public String mailid;

    @SerializedName("decimal")
    public String decimal;

    @SerializedName("pin")
    public String pin;

    @SerializedName("language")
    public String language;

    @SerializedName("security")
    public String security;

    @SerializedName("response")
    public String response;

    @SerializedName("message")
    public String message;
}
