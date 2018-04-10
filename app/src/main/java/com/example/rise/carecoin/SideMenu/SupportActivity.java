package com.example.rise.carecoin.SideMenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.Transaction.TransactionsDetailsViewController;

/**
 * Created by Rise on 12/02/2018.
 */

public class SupportActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView  home,btn_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        setToolbar();
        TransactionsDetailsViewController.isFromNotification=false;
    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_back = (ImageView) toolbar.findViewById(R.id.back);
        home = (ImageView) toolbar.findViewById(R.id.toolbar_icon);

        if (TransactionsDetailsViewController.isFromNotification){
            btn_back.setBackgroundResource(R.drawable.ic_back);
        }else {
            home.setImageResource(R.drawable.ic_home);
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView toolbartext = (TextView) toolbar.findViewById(R.id.toolbar_text);
        toolbartext.append(getString(R.string.support));
    }
    @Override
    public void onBackPressed(){
        finish();
    }
}

