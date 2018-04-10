package com.example.rise.carecoin.Model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by WAVE on 2/26/2018.
 */

public class Payees {
    public Payees() {

    }

    public Payees(User user, String payeeId, String name, String email, String phonenumber, byte[] image, boolean isExistedAccount) {
        this.user = user;
        this.payeeId = payeeId;
        this.Name = name;
        this.Email = email;
        this.Phonenumber = phonenumber;
        this.Image = image;
        this.isExistedAccount = isExistedAccount;
    }


    @DatabaseField(columnName = "user_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public User user;

    @DatabaseField(columnName = "name")
    private String Name;

    @DatabaseField(columnName = "payeeid")
    private String payeeId;

    @DatabaseField(columnName = "email")
    private String Email;

    @DatabaseField(columnName = "phonenumber")
    private String Phonenumber;

    @DatabaseField(columnName = "image",dataType=DataType.BYTE_ARRAY)
    private byte[] Image;

    @DatabaseField(columnName = "isExistAccount", dataType = DataType.BOOLEAN)
    private boolean isExistedAccount;

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }


    public boolean isExistAccount() {
        return isExistedAccount;
    }

    public void setExistAccount(boolean existAccount) {
        isExistedAccount = existAccount;
    }


    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


}
