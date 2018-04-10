package com.example.rise.carecoin.Model;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "User")
public class User {
    
    public User() {
    }
    @DatabaseField(id = true, columnName = "userid")
    public String userid;

    @DatabaseField(columnName = "password")
    public String password;

    @DatabaseField(columnName = "registertype")
    public String registerType;

    @DatabaseField(columnName = "registertime")
    public String registerTime;

    @DatabaseField(columnName = "longitude")
    public String longitude;

    @DatabaseField(columnName = "latitude")
    public String latitude;

    @DatabaseField(columnName = "walletid")
    public String walletId;

    @DatabaseField(columnName = "walletaddress")
    public String walletAddress;

    @DatabaseField(columnName = "avaliablebalance")
    public double avaliablebalance;

    @DatabaseField(columnName = "profilePicture",dataType = DataType.BYTE_ARRAY)
    public byte[] mprofilepicturepath;

    @DatabaseField(columnName = "username")
    public String username;

    @DatabaseField(columnName = "selectedcurrency")
    public String selectedcurrency;

    @DatabaseField(columnName = "device")
    public String device;

    @DatabaseField(columnName = "token")
    public String token;

    @DatabaseField(columnName = "phonenumber")
    public String phonenumber;

    @DatabaseField(columnName = "pinpasscode")
    public String pinpasscode;

    @DatabaseField(columnName = "issetpin", dataType = DataType.BOOLEAN)
    public boolean isSetPin;

    @DatabaseField(columnName = "preferdlanguage")
    public String preferdlanguage;

    @DatabaseField(columnName = "decimalvalue")
    public String decimalvalue;

    //foreign collection fields
   @ForeignCollectionField
   public ForeignCollection<Payees> payees;

    @ForeignCollectionField
    public ForeignCollection<Transaction> transactions;

    public ForeignCollection<Payees> getPayees() {
        return payees;
    }

    public ForeignCollection<Transaction> getTransactions() {
        return transactions;
    }

}
