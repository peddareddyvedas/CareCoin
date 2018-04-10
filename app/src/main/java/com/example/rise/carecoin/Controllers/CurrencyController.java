package com.example.rise.carecoin.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.Model.CurrencyModel;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.CurrencyServerObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WAVE on 3/6/2018.
 */
public class CurrencyController {
    public static CurrencyController myObj;
    public ArrayList<CurrencyModel> currencyList;
    Context context;
    public CurrencyModel selectedCurrencyModel;
    JsonObject currencyInfoDictionary;


    public static CurrencyController getInstance() {
        if (myObj == null) {
            myObj = new CurrencyController();
        }

        return myObj;
    }

    public void fillContext(Context context1) {
        context = context1;
        Log.e("fillContext", "call");
        executeCurrencyInfoServerAPI();
        gettingCurrencyDataFromSharedPreferences();
    }

    public void executeCurrencyInfoServerAPI() {
        Log.e("executeCurrencyInfo", "call");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.currency_InfoUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<JsonObject> callable = api.getAllCurrencyInfo();
        callable.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("response", "call" + response.isSuccessful());
                        Log.e("jsonObject", "call" + response.body());
                        currencyInfoDictionary = response.body();
                        Log.e("currencyInfoDictionary", "call" + currencyInfoDictionary);
                        executeCurrencyServerAPI();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("onFailure", "call");
            }
        });

    }
    public void executeCurrencyServerAPI() {
        Log.e("executeCurrency", "call");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.currency_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        //Call<CurrencyServerObject> callable = api.getAllCurrency("79183cb11a4cbc1df521b6193aa3ac06", 1, ServerApisInterface.BaseCurrency);
        Call<CurrencyServerObject> callable = api.getAllCurrency();

        callable.enqueue(new Callback<CurrencyServerObject>() {
            @Override
            public void onResponse(Call<CurrencyServerObject> call, retrofit2.Response<CurrencyServerObject> response) {
                HashMap<String, Double> ratesMap = response.body().rates;
                currencyList = new ArrayList<CurrencyModel>();
                if (ratesMap != null) {
                    for (Map.Entry<String, Double> entry : ratesMap.entrySet()) {
                        CurrencyModel objCurrencyModel = new CurrencyModel();
                        String key = entry.getKey();
                        Log.e("value", "call" + key);

                        //String[] parts = key1.split("USD");
                        //String part1 = parts[0];
                       // String key = parts[1];
                        double value = entry.getValue();
                        JsonElement jsonElement = currencyInfoDictionary.get(key);
                        if (jsonElement != null) {
                            JsonObject object = jsonElement.getAsJsonObject();
                            Log.e("value", "call" + value + "" + key);
                            Log.e("name", "call" + object.get("name").getAsString());
                            objCurrencyModel.setCurrencyShortName(key);
                            objCurrencyModel.setCurrencyName(object.get("name").getAsString());
                            objCurrencyModel.setPriceByUSD(value);
                            objCurrencyModel.setCurrencySymbol(object.get("symbol_native").getAsString());
                            Log.e("shortcut", "call" + objCurrencyModel.getCurrencyShortName());

                            if (objCurrencyModel.getCurrencyShortName().equals("USD")) {
                                selectedCurrencyModel = objCurrencyModel;
                                Log.e("selectedCurrencyModel", "call" + selectedCurrencyModel.getPriceByUSD());
                            }
                            currencyList.add(objCurrencyModel);
                            Log.e("currencyList", "call" + currencyList.size());
                            sortCurrencyNames(currencyList);
                            setSelectedCurrency(currencyList);
                        }
                    }
                    saveCurrencyDataInSharedPreferences(currencyList, context);
                }
            }

            @Override
            public void onFailure(Call<CurrencyServerObject> call, Throwable t) {
                Log.e("onFailure", "call");
            }
        });

    }

    public ArrayList<CurrencyModel> sortCurrencyNames(ArrayList<CurrencyModel> urineResults) {
        Collections.sort(urineResults, new Comparator<CurrencyModel>() {
            @Override
            public int compare(CurrencyModel s1, CurrencyModel s2) {
                return s1.getCurrencyName().compareTo(s2.getCurrencyName());
            }
        });
        return urineResults;

    }

    public void saveCurrencyDataInSharedPreferences(ArrayList<CurrencyModel> currencyList, Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currencyList);
        prefsEditor.putString("MyCurrencyObject", json);
        Log.e("json", "call" + json);
        prefsEditor.commit();
    }

    public void gettingCurrencyDataFromSharedPreferences() {
        UserDataController.getInstance().fetchUserData();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("MyCurrencyObject", "");
        if (json != null) {
            Type type = new TypeToken<ArrayList<CurrencyModel>>() {
            }.getType();
            ArrayList<CurrencyModel> currencyModelArrayList = gson.fromJson(json, type);
            if (currencyModelArrayList != null) {
                currencyList = currencyModelArrayList;
                Log.e("currencyModelArrayList", "call" + currencyModelArrayList.size());
            }
            if (currencyList != null) {
                setSelectedCurrency(currencyList);
            }

        }
    }

    public void setSelectedCurrency(ArrayList<CurrencyModel> currencyList) {
        for (CurrencyModel objCurrencyModel : currencyList) {

            if (UserDataController.getInstance().currentUser != null) {

                if (UserDataController.getInstance().currentUser.selectedcurrency == null) {
                    if (objCurrencyModel.getCurrencyShortName().equals("USD")) {
                        selectedCurrencyModel = objCurrencyModel;
                    }
                }
                if (objCurrencyModel.getCurrencyShortName().equals(UserDataController.getInstance().currentUser.selectedcurrency)) {
                    selectedCurrencyModel = objCurrencyModel;

                }

            } else {
                if (objCurrencyModel.getCurrencyShortName().equals("USD")) {
                    selectedCurrencyModel = objCurrencyModel;
                }
            }

        }
    }
}
