package com.example.rise.carecoin.ServerObjects;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by WAVE on 3/5/2018.
 */

public class CurrencyServerObject {
    @SerializedName("base")
    public String base;

    @SerializedName("date")
    public String date;

   /* @SerializedName("rates")
    public HashMap<String,Double> rates;
*/

    @SerializedName("result")
    public HashMap<String,String> result;

    @SerializedName("quotes")
    public HashMap<String,Double> rates;
}
