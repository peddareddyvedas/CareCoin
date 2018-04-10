package com.example.rise.carecoin.Controllers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.Model.ContactModel;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.ServerObjects.ContactsServerObjects;
import com.example.rise.carecoin.SideMenu.ContactsViewController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WAVE on 2/15/2018.
 */

public class ContactsDataController extends Activity {

    public static ContactsDataController myObj;
    public ArrayList<ContactModel> contactsArrary;
    Context context;
    public  ContactModel selectedContactModel;


    public static ContactsDataController getInstance() {
        if (myObj == null) {
            myObj = new ContactsDataController();
        }

        return myObj;
    }

    public void fillContext(Context context1) {
        context = context1;
    }

    public void loadContactsOnSeparateThread() {
        // run on separate thread
        HandlerThread handlerThread = new HandlerThread("fetchContacts");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
                try {
                    Runnable backgroundTask = new Runnable() {
                        @Override
                        public void run() {
                            ContactsDataController.getInstance().getPhoneDetailsFromDeviceContacts();
                        }
                    };
                    taskExecutor.submit(backgroundTask);
                    taskExecutor.shutdown();
                    taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {

                }
            }
        });

    }

    public void getPhoneDetailsFromDeviceContacts() {

        //run on saparat thrad.
        contactsArrary = new ArrayList<ContactModel>();
        ///
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                while (cur1.moveToNext()) {
                    //to get the contact names
                    String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.e("Name:", "" + name);
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    Log.e("Email", "" + email);
                    String image_uri = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                    Integer hasPhone = cur1.getInt(cur1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (email != null) {
                        ContactModel contactModel = new ContactModel();
                        contactModel.setEmail(email);
                        contactModel.setName(name);

                        // get the user's phone number
                        String phone = null;
                        if (hasPhone > 0) {
                            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (cp != null && cp.moveToFirst()) {
                                phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                contactModel.setMobileNumber(phone);
                                cp.close();
                            }
                        }
                        // get the user's phone image

                        if (image_uri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image_uri));
                                Log.e("Image in Bitmap:", "call" + bitmap);
                                contactModel.setPhoto(bitmap);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("isEmpty", "call" + image_uri);
                            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.ic_profile);
                            contactModel.setPhoto(icon);
                        }
                        contactsArrary = removeParentEmailsInCareCoinContacts(contactsArrary);

                        if (isEmailAvaliableInCOntacts(contactModel.email)) {
                            contactsArrary.add(contactModel);
                        }

                        Log.e("contactsArrary", "call" + contactsArrary.size());
                    }
                }
                cur1.close();
            }

            //call api
            contactApiExecution();
        }
    }

    public ArrayList<ContactModel> removeParentEmailsInCareCoinContacts(ArrayList<ContactModel> contactsarrayList) {
        for (ContactModel contactModel : contactsarrayList) {
            if (contactModel.email.contains(UserDataController.getInstance().currentUser.userid)) {
                contactsarrayList.remove(contactModel);
                Log.e("parenrmail", "call" + contactModel.email);
            }
        }
        return contactsarrayList;
    }

    public boolean isEmailAvaliableInCOntacts(String email) {
        for (ContactModel contactModel : contactsArrary) {
            if (email.equals(contactModel.email)) {
                Log.e("dupemail", "call" + contactModel.email);
                return false;
            }
        }
        return true;

    }

    public String getEmailsArrayFromContacts() {
        StringBuilder sb = new StringBuilder();
        for (ContactModel item : contactsArrary) {
            if (item.email.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(item.email);
            }
        }
        String result = sb.toString();
        Log.e("result", "cal" + result);
        return result;
    }

    //contactAps////
    public void contactApiExecution() {
        final ContactsServerObjects requestBody = new ContactsServerObjects();

        Log.e("contacts", "" + getEmailsArrayFromContacts());
        requestBody.mails = getEmailsArrayFromContacts();
        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<ContactsServerObjects> callable = api.contacts(requestBody);
        callable.enqueue(new Callback<ContactsServerObjects>() {
            @Override
            public void onResponse(Call<ContactsServerObjects> call, retrofit2.Response<ContactsServerObjects> response) {
                if (response.body() != null) {
                    String statusCode = response.body().response;
                    String message = response.body().message;
                    JsonArray contacts = response.body().contacts;
                    Log.e("contactcode", "call" + contacts);
                    Log.e("message", "call" + message);

                    if (!statusCode.equals(null)) {
                        if (statusCode.equals("3")) {
                            processCareCoinContacts(response.body().contacts);

                        } else if (statusCode.equals("0")) {
                            //new AlertShowingDialog(getApplicationContext(), message);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ContactsServerObjects> call, Throwable t) {
                Log.e("onFailure", "call");
            }
        });
    }

    public void processCareCoinContacts(JsonArray careCoinContactsArray) {
        int index = 0;
        if (careCoinContactsArray.size() > 0) {
            for (int i = 0; i < careCoinContactsArray.size(); i++) {
                JsonObject contactObj = careCoinContactsArray.get(i).getAsJsonObject();
                String email = contactObj.get("email").getAsString();
                String walletaddress = contactObj.get("address").getAsString();
                Log.e("careCoinContacts", "call" + email + "---" + walletaddress);
                for (ContactModel contactsModel : contactsArrary) {
                    if (contactsModel.email.equals(email)) {
                        contactsModel.iscareContact = true;
                        contactsModel.walletAddress = walletaddress;
                        index = contactsArrary.indexOf(contactsModel);
                        Log.e("index", "call" + index);
                        contactsArrary.set(index, contactsModel);
                    }
                }
            }
            EventBus.getDefault().post(new ContactsViewController.MessageEvent("refreshContacts"));
        }
    }
}
