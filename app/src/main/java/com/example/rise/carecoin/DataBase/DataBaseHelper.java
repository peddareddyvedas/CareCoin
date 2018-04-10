package com.example.rise.carecoin.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.rise.carecoin.Model.Payees;
import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.Model.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application --
    private static final String DATABASE_NAME = "carecoin.db";

    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    private Dao<User, Integer> userDao = null;
    private Dao<Payees, Integer> payeesDao = null;
    private Dao<Transaction, Integer> transactionDao = null;


    ConnectionSource objConnectionSource;

    public DataBaseHelper(Context contex) {
        super(contex, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.e("DBStatus", "OnCreate" + connectionSource);

        try {
            //creating the user table
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Payees.class);
            TableUtils.createTable(connectionSource, Transaction.class);


            userDao = DaoManager.createDao(connectionSource, User.class);
            payeesDao = DaoManager.createDao(connectionSource, Payees.class);
            transactionDao = DaoManager.createDao(connectionSource, Transaction.class);

            Log.e("user", "user table is created");

            objConnectionSource = connectionSource;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.e("DBStatus", "OnUpgrade" + connectionSource);
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Payees.class, true);
            TableUtils.dropTable(connectionSource, Transaction.class, true);

            onCreate(database, connectionSource);
            objConnectionSource = connectionSource;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //user DAO
    public Dao<User, Integer> getUserDao() {
        if (userDao == null) {
            try {
                //  userDao = DaoManager.createDao(objConnectionSource,User.class);
                userDao = getDao(User.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userDao;
    }

    //payeees DAO
    public Dao<Payees, Integer> getPayeesDao() {
        if (payeesDao == null) {
            try {
                payeesDao = getDao(Payees.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return payeesDao;
    }
    //transaction DAO

    public Dao<Transaction, Integer> getTransactionDao() {
        if (transactionDao == null) {
            try {
                transactionDao = getDao(Transaction.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return transactionDao;

    }
}
