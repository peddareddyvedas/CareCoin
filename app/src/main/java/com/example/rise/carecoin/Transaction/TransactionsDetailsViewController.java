package com.example.rise.carecoin.Transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rise.carecoin.Model.Transaction;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.SideMenu.SupportActivity;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.rise.carecoin.R.id.textView;

/**
 * Created by Rise on 15/03/2018.
 */

public class TransactionsDetailsViewController extends AppCompatActivity {

    public static Transaction objTransaction;
    ImageView img_share;
    Button back_img;
    ImageView imageView, imageselect;
    LinearLayout sharePage;
    private static final int REQUEST_WRITE_PERMISSION = 56;
    RelativeLayout rl_issue;
    public static boolean isFromNotification=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionviewlayout);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        sharePage=(LinearLayout)findViewById(R.id.rl_home);
        final TextView transacctionidtext = (TextView) findViewById(R.id.transactionid);
        TextView timedate = (TextView) findViewById(R.id.timedate);
        TextView email = (TextView) findViewById(R.id.email);
        TextView message = (TextView) findViewById(R.id.message);
        TextView coin = (TextView) findViewById(R.id.coins);
        TextView transaction_type = (TextView) findViewById(R.id.transaction_type);
        back_img=(Button)findViewById(R.id.back) ;
        rl_issue=(RelativeLayout)findViewById(R.id.rl_issue) ;

        img_share=(ImageView)findViewById(R.id.img_share);
        img_share.setOnClickListener(mShareListener);

        imageView = (ImageView) findViewById(R.id.imageview);
        imageselect = (ImageView) findViewById(R.id.imageselect);

        imageselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(transacctionidtext.getText().toString());   // Assuming that you are copying the text from a TextView
                Toast.makeText(getApplicationContext(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        transacctionidtext.setText(objTransaction.getTransactionID());

        try {
            timedate.setText("" + convertTimestampToAgoFromate(objTransaction.getDateTimeStamp()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String transactionType = objTransaction.getTransactiontype();
        coin.setText("" + objTransaction.getAmount()+" "+"CCN");

        if (transactionType.equals("sent")) {
            imageView.setImageResource(R.drawable.ic_sent);
            transaction_type.setText("Paid to");
            email.setText(objTransaction.getToAddress());
            if (objTransaction.getNotes().isEmpty()) {
                message.setText("No Message");
            } else {
                message.setText(objTransaction.getNotes());
            }

        } else if (transactionType.equals("recieved")) {
            imageView.setImageResource(R.drawable.ic_received);
            transaction_type.setText("Received from");
            email.setText(objTransaction.getFromAddress());
            if (objTransaction.getNotes().isEmpty()) {
                message.setText("No Message");
            } else {
                message.setText(objTransaction.getNotes());
            }

        } else if (transactionType.equals("reward")) {
            imageView.setImageResource(R.drawable.ic_reward);
            transaction_type.setText("Reward");
            email.setText("CareCoin Reward");
            if (objTransaction.getNotes().isEmpty()) {
                message.setText("No Message");
            } else {
                message.setText(objTransaction.getNotes());
            }

        }

    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }
    @OnClick(R.id.rl_issue)
    public void issue() {
        finish();
        isFromNotification=true;
        startActivity(new Intent(getApplicationContext(), SupportActivity.class));
    }
    public String convertTimestampToAgoFromate(String stringData)
            throws ParseException {
        String weekString = "", timeString = "",totalString = "";
        long yourmilliseconds = (long) Double.parseDouble(stringData);
        Date resultdate = new Date(yourmilliseconds * 1000);
        Log.e("resultdate", "call" + resultdate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMMM.yyyy", Locale.ENGLISH);
        SimpleDateFormat timeFormate = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        weekString = dateFormat.format(resultdate);
        timeString = timeFormate.format(resultdate);

        totalString = timeString + " " + " on " + weekString;
        Log.e("totalString", "call" + totalString);

        return totalString;
    }

    View.OnClickListener mShareListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            img_share.setVisibility(View.GONE);
            back_img.setVisibility(View.GONE);
            imageselect.setVisibility(View.GONE);
            rl_issue.setVisibility(View.GONE);
            View viewScreen = sharePage.getRootView();
            viewScreen.buildDrawingCache();
            viewScreen.setDrawingCacheEnabled(true);
            viewScreen.destroyDrawingCache();
            Bitmap screenshot1 = Bitmap.createBitmap(viewScreen.getWidth(), viewScreen.getHeight(), Bitmap.Config.RGB_565);
            viewScreen.draw(new Canvas(screenshot1));
            File mfile2 = savebitmap2(screenshot1);
            final Uri screenshotUri = Uri.fromFile(mfile2);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Care Coin");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "" + "Transaction details");
            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share the status :"));
            ///
            img_share.setVisibility(View.VISIBLE);
            back_img.setVisibility(View.VISIBLE);
            imageselect.setVisibility(View.VISIBLE);
            rl_issue.setVisibility(View.VISIBLE);
        }
    };
    /**
     * Called when take the screen shot
     */

    private File savebitmap2(Bitmap bmp) {
        String temp = "TransactionHistory";

        OutputStream outStream = null;
        String path = Environment.getExternalStorageDirectory()
                .toString();
        new File(path + "/SplashItTemp2").mkdirs();
        File file = new File(path + "/SplashItTemp2", temp + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(path + "/SplashItTemp2", temp + ".png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
    /*private void requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }

    }*/
}
