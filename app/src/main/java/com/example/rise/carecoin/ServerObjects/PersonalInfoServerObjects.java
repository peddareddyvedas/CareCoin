package com.example.rise.carecoin.ServerObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rise on 30/03/2018.
 */

public class PersonalInfoServerObjects {


    @SerializedName("username")
    public String username;

    @SerializedName("name")
    public String name;

    @SerializedName("mobileno")
    public String mobileno;


    @SerializedName("image")
    public String imageURl;


    @SerializedName("response")
    public String response;

    @SerializedName("message")
    public String message;

}
