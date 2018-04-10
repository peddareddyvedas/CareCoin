package com.example.rise.carecoin.Model;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by WAVE on 2/14/2018.
 */

public class ContactModel {
    public String id;
    public String name;
    public String mobileNumber;
    public Bitmap photo;
    public String walletAddress;
    public String email;
    public Uri photoURI;
    public boolean iscareContact=false;


    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    public boolean iscareContact() {
        return iscareContact;
    }

    public void setIscareContact(boolean iscareContact) {
        this.iscareContact = iscareContact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
