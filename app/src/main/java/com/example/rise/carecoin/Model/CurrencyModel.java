package com.example.rise.carecoin.Model;

/**
 * Created by Rise on 13/02/2018.
 */

public class CurrencyModel {

    private String currencyName;
    private String currencyShortName;
    private double priceByUSD;
    private String currencySymbol;

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }


    public String getCurrencyShortName() {
        return currencyShortName;
    }

    public void setCurrencyShortName(String currencyShortName) {
        this.currencyShortName = currencyShortName;
    }


    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public double getPriceByUSD() {
        return priceByUSD;
    }

    public void setPriceByUSD(double priceByUSD) {
        this.priceByUSD = priceByUSD;
    }


    /*public CurrencyModel( String currencyName, String currencyShortName,String currencySymbol,double priceByUSD) {
        this.currencyName =currencyName;
        this.currencyShortName = currencyShortName;
        this.currencySymbol=currencySymbol;
        this.priceByUSD = priceByUSD;

    }*/
}