package com.example.rise.carecoin.ServerApis;
import com.example.rise.carecoin.ServerObjects.BalanceServerObjects;
import com.example.rise.carecoin.ServerObjects.ContactsServerObjects;
import com.example.rise.carecoin.ServerObjects.CurrencyServerObject;
import com.example.rise.carecoin.ServerObjects.PersonalInfoServerObjects;
import com.example.rise.carecoin.ServerObjects.SettingsServerObjects;
import com.example.rise.carecoin.ServerObjects.TransferServerObject;
import com.example.rise.carecoin.ServerObjects.UserServerObject;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by WAVE on 10/20/2017.
 */
public interface ServerApisInterface {
    String home_URL = "http://54.234.239.245/";
    String home_URL1 = "http://54.234.239.245/carecoin/";
    String currency_URL="http://54.234.239.245/";
    String currency_InfoUrl="https://gist.githubusercontent.com/";
    String home_Image_Main_URL = "http://54.234.239.245";
     String aboutus_url = "http://www.vedaslabs.com";

    @Multipart
    @POST("carecoin/api/photo")
    Call<PersonalInfoServerObjects> personalinfo(@Part MultipartBody.Part filePart, @Part("username") RequestBody username, @Part("name")RequestBody  name, @Part("mobileno") RequestBody mobileno);

    //for register
    @POST(" operator/register")
    Call<UserServerObject> register(@Body UserServerObject body);

    //for Verify Wallets
    @POST("operator/wallets")
    Call<UserServerObject> verify(@Body UserServerObject body);

    //for login
    @POST("carecoin/login")
    Call<UserServerObject> login(@Body UserServerObject body);

    //for forgot
    @POST("forgot")
    Call<UserServerObject> forgot(@Body UserServerObject body);

    ///for frogot verify
    @POST("carecoin/verify")
    Call<UserServerObject> forgotverify(@Body UserServerObject body);

    //new password api
    @POST("verify")
    Call<UserServerObject> newpassword(@Body UserServerObject body);

    @POST("changepassword")
    Call<UserServerObject> changepassword(@Body UserServerObject body);
    ///  balance enquire api
    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("operator/{addressId}/balance")
    Call<BalanceServerObjects> getBalance(@Path("addressId") String addressId);

    //for transfer money
    @POST("operator/wallets/transactions")
    Call<TransferServerObject> getTransfer(@Body TransferServerObject body);


    //for contacts
    @POST("carecoin/users")
    Call<ContactsServerObjects> contacts(@Body ContactsServerObjects body);

    ///useremailbalance
    @GET("carecoin/user/address/{mailid}")
    Call<UserServerObject> useremail(@Path("mailid") String mailid);


    @GET("brickcap/43681dadc0b6c91ec0b8/raw/5bcb48a60f0547b320079af8b4fa3ae0d19613ef/Common-Currency.json")
    Call<JsonObject> getAllCurrencyInfo();

    @GET("carecoin/currency")
    Call<CurrencyServerObject> getAllCurrency();

    @GET("carecoin/user/mail/{address}")
    Call<UserServerObject> useraddress(@Path("address") String address);

    //logout
    @POST("logout")
    Call<UserServerObject> logout(@Body UserServerObject body);

    @POST("settings")
    Call<SettingsServerObjects> settingsApi(@Body SettingsServerObjects body);
}
