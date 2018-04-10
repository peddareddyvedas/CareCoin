package com.example.rise.carecoin.DataBase;

import android.util.Log;

import com.example.rise.carecoin.Model.Payees;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 02-10-2017.
 */

public class PayeeDataController {
    public ArrayList<Payees> allPayees = new ArrayList<>();
    public Payees currentPayee;
    public static PayeeDataController myObj;

    public static PayeeDataController getInstance() {
        if (myObj == null) {
            myObj = new PayeeDataController();
        }
        return myObj;
    }

    //Inserting member data
    public Boolean insertPayeesData(Payees payees) {
        Payees payeesModel = new Payees(UserDataController.getInstance().currentUser, payees.getPayeeId(), payees.getName(), payees.getEmail(), payees.getPhonenumber(), payees.getImage(), payees.isExistAccount());
        Log.e("insertmember", "call" + UserDataController.getInstance().currentUser);
        Log.e("insertmid", "call" + payees.getName());

        try {
            UserDataController.getInstance().helper.getPayeesDao().create(payeesModel);
            fetchPayeesData();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
        //memberdao.create(memberModel);
    }

    public void getCurrentPayee() {
        if (allPayees.size() > 0) {
            for (int i = 0; i < allPayees.size(); i++) {
                Payees payeesObj = allPayees.get(i);
                Log.e("cururMember", "" + payeesObj.getName());
                currentPayee = payeesObj;
            }
        }
    }

    //Fetching all the member data
    public ArrayList<Payees> fetchPayeesData() {
        allPayees = new ArrayList<>();
        ArrayList<Payees> dbPayees = new ArrayList<Payees>(UserDataController.getInstance().currentUser.getPayees());
        if (dbPayees != null) {
            allPayees = dbPayees;
            if (allPayees.size() > 0) {
                Log.e("currentPayee", "call" + allPayees.size());
                getCurrentPayee();
            }
        }
        return allPayees;
    }

    //Deleting all users in database
    public void deletePayeesData(List<Payees> payees_list) {
        try {
            UserDataController.getInstance().helper.getPayeesDao().delete(payees_list);
            Log.e("Delete", "delete all Payees");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean deletePayeesData(Payees member) {
        try {
            UserDataController.getInstance().helper.getPayeesDao().delete(member);
            Log.e("Delete", "delete all payees");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePayeesData(Payees objPayee) {
        try {
            UpdateBuilder<Payees, Integer> updateBuilder = UserDataController.getInstance().helper.getPayeesDao().updateBuilder();
            updateBuilder.updateColumnValue("name", objPayee.getName());
            updateBuilder.updateColumnValue("email", objPayee.getEmail());
            updateBuilder.updateColumnValue("phonenumber", objPayee.getPhonenumber());
            updateBuilder.updateColumnValue("image", objPayee.getImage());
            updateBuilder.updateColumnValue("isExistAccount", objPayee.isExistAccount());

            updateBuilder.where().eq("email", objPayee.getEmail());
            updateBuilder.update();

            Log.e("update", "" + "payee data updated sucessfully");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //updating the member data
    public void updatePayeeData(Payees objPayee) {
        try {
            UpdateBuilder<Payees, Integer> updateBuilder = UserDataController.getInstance().helper.getPayeesDao().updateBuilder();
            updateBuilder.updateColumnValue("name", objPayee.getName());
            updateBuilder.updateColumnValue("email", objPayee.getEmail());
            updateBuilder.updateColumnValue("phonenumber", objPayee.getPhonenumber());
            updateBuilder.updateColumnValue("image", objPayee.getImage());
            updateBuilder.updateColumnValue("isExistAccount", objPayee.isExistAccount());

            updateBuilder.where().eq("email", objPayee.getEmail());
            updateBuilder.update();

            Log.e("update", "payee data updated sucessfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
