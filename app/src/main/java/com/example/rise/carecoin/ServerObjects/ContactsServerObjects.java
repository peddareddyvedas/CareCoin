package com.example.rise.carecoin.ServerObjects;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rise on 02/03/2018.
 */

public class ContactsServerObjects {


    @SerializedName("mails")
    public String mails;

    @SerializedName("contacts")
    public JsonArray contacts;

    @SerializedName("response")
    public String response;

    @SerializedName("message")
    public String message;
}
