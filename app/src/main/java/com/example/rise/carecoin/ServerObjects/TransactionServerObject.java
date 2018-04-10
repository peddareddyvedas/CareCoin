package com.example.rise.carecoin.ServerObjects;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by WAVE on 2/27/2018.
 */

public class TransactionServerObject {

    @SerializedName("id")
    public String walletId;

    @SerializedName("hash")
    public String Hash;

    @SerializedName("type")
    public String Type;

    @SerializedName("data")
    public JsonObject Data;


}
