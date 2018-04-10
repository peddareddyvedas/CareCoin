package com.example.rise.carecoin.Model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by WAVE on 2/13/2018.
 */

public class Transaction {
    public Transaction() {
    }
    public Transaction(User user,String transactionID, String transactionName, String dateTimeStamp, String transactiontype, double amount, String careCoinAddress, String notes, String toAddress, String fromAddress) {

        this.user=user;
        this.transactionID = transactionID;
        this.transactionName = transactionName;
        this.timeStamp = dateTimeStamp;
        this.transactiontype = transactiontype;
        this.amount = amount;
        this.careCoinAddress = careCoinAddress;
        this.notes = notes;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
    }
    @DatabaseField(id = true, columnName = "userid")
    private String id;

    @DatabaseField(columnName = "transactionID")
    private String transactionID;

    @DatabaseField(columnName = "transactionName")
    private String transactionName;

    @DatabaseField(columnName = "timeStamp")
    private String timeStamp;

    @DatabaseField(columnName = "transactiontype")
    private String transactiontype;

    @DatabaseField(columnName = "amount")
    private double amount;

    @DatabaseField(columnName = "careCoinAddress")
    private String careCoinAddress;

    @DatabaseField(columnName = "notes")
    private String notes;

    @DatabaseField(columnName = "toAddress")
    private String toAddress;

    @DatabaseField(columnName = "fromAddress")
    private String fromAddress;

    @DatabaseField(columnName = "user_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public User user;


    private User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDateTimeStamp(String dateTimeStamp) {this.timeStamp = dateTimeStamp;}

    public String getDateTimeStamp() {
        return timeStamp;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getTransactionName() {return transactionName;}

    public void setTransactionName(String transactionName) {this.transactionName = transactionName;}

    public String getTransactiontype() {return transactiontype;}

    public void setTransactiontype(String transactiontype) {this.transactiontype = transactiontype;}

    public String getCareCoinAddress() {return careCoinAddress;}

    public void setCareCoinAddress(String careCoinAddress) {this.careCoinAddress = careCoinAddress;}

    public String getNotes() {return notes;}

    public void setNotes(String notes) {this.notes = notes;}

    public String getToAddress() {return toAddress;}

    public void setToAddress(String toAddress) {this.toAddress = toAddress;}

    public String getFromAddress() {return fromAddress;}

    public void setFromAddress(String fromAddress) {this.fromAddress = fromAddress;}
}

