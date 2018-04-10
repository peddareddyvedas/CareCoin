package com.example.rise.carecoin.ServerObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rise on 28/02/2018.
 */

public class BalanceServerObjects {

    @SerializedName("addressId")
    public String addressId;

    @SerializedName("response")
    public String response;

    @SerializedName("balance")
    public String balance;
}
