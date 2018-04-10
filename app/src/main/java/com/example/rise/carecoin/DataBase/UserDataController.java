package com.example.rise.carecoin.DataBase;

import android.content.Context;
import android.util.Log;

import com.example.rise.carecoin.Model.User;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by dell on 20-09-2017.
 */

public class UserDataController extends OrmLiteBaseActivity {
    public DataBaseHelper helper;
    public ArrayList<User> allUsers = new ArrayList<>();
    public User currentUser;
    //public  Context context;
    public static UserDataController myObj;

    public static UserDataController getInstance() {
        if (myObj == null) {
            myObj = new UserDataController();
        }

        return myObj;
    }

    public void fillContext(Context context1) {

        Log.e("DBStatus", "Fill Context Called");
        helper = new DataBaseHelper(context1);

    }

    //insert the userdata into user table
    public void insertUserData(User userdata) {
        try {
            helper.getUserDao().create(userdata);
            fetchUserData();
            Log.e("fetc", "" + allUsers + "id" + "call" + userdata.userid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Fetching all the user data
    public ArrayList<User> fetchUserData() {
        allUsers = null;
        allUsers = new ArrayList<>();

        try {
            allUsers = (ArrayList<User>) helper.getUserDao().queryForAll();
            if (allUsers.size() > 0) {
                currentUser = allUsers.get(0);
                Log.e("currentUser", "call" + currentUser.userid);
                Log.e("currentcount", "call" + allUsers.size());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.e("fetching", "user data fectched successfully" + allUsers.size());

        return allUsers;
    }

    public boolean updateSelectedCurrency(String selectedCurrency) {

        currentUser.selectedcurrency = selectedCurrency;
        updateUserData(currentUser);
        return true;
    }

    public boolean updatePinPasscode(String passcode, boolean issetpin) {
        Log.e("passcodesuser", "call" + passcode + "boolean" + issetpin);
        currentUser.pinpasscode = passcode;
        currentUser.isSetPin = issetpin;
        updateUserData(currentUser);
        return true;
    }

    //updating the userdata
    public void updateUserData(User user) {
        try {
            UpdateBuilder<User, Integer> updateBuilder = helper.getUserDao().updateBuilder();
            updateBuilder.updateColumnValue("userid", user.userid);
            updateBuilder.updateColumnValue("password", user.password);
            updateBuilder.updateColumnValue("registertime", user.registerTime);
            updateBuilder.updateColumnValue("registertype", user.registerType);
            updateBuilder.updateColumnValue("latitude", user.latitude);
            updateBuilder.updateColumnValue("longitude", user.longitude);
            updateBuilder.updateColumnValue("walletid", user.walletId);
            updateBuilder.updateColumnValue("walletaddress", user.walletAddress);
            updateBuilder.updateColumnValue("avaliablebalance", user.avaliablebalance);
            updateBuilder.updateColumnValue("profilePicture", user.mprofilepicturepath);
            updateBuilder.updateColumnValue("username", user.username);
            updateBuilder.updateColumnValue("phonenumber", user.phonenumber);
            updateBuilder.updateColumnValue("selectedcurrency", user.selectedcurrency);
            updateBuilder.updateColumnValue("pinpasscode", user.pinpasscode);
            updateBuilder.updateColumnValue("issetpin", user.isSetPin);
            updateBuilder.updateColumnValue("preferdlanguage", user.preferdlanguage);
            updateBuilder.updateColumnValue("decimalvalue", user.decimalvalue);

            updateBuilder.where().eq("userid", user.userid);
            updateBuilder.update();

            fetchUserData();
            Log.e("updatedata", "updated the mobilenumber" + "" + user.preferdlanguage);
            Log.e("profile", "" + user.mprofilepicturepath);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Deleting all users in database
    public void deleteUserData(ArrayList<User> user) {
        try {
            helper.getUserDao().delete(user);
            Log.e("userdatadeleted", "call" + allUsers.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
