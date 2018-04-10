package com.example.rise.carecoin.Settings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.Controllers.ApisController;
import com.example.rise.carecoin.R;
import com.example.rise.carecoin.ServerApis.ServerApisInterface;
import com.example.rise.carecoin.Transaction.TransactionsDetailsViewController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rise on 04/04/2018.
 */

public class AboutUsViewController extends AppCompatActivity {
    Toolbar toolbar;
    ImageView btn_back;
    TextView edittwitter, editfacebok, editwebsite, editnotes, link;
    RelativeLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        init();
        setToolbar();
        layout = (RelativeLayout) findViewById(R.id.relative);
        layout.setBackgroundResource(R.drawable.recyclerviewborders);
        GradientDrawable gd = (GradientDrawable) layout.getBackground().getCurrent();
        gd.setColor(Color.parseColor("#f0f0f0"));
    }


    private void init() {
        edittwitter = (TextView) findViewById(R.id.edittwitter);
        editfacebok = (TextView) findViewById(R.id.editfacebook);
        editwebsite = (TextView) findViewById(R.id.editwebsite);
        editnotes = (TextView) findViewById(R.id.editnotes);
        link = (TextView) findViewById(R.id.link);
        edittwitter.setText("www.vedaslabs.com");
        editfacebok.setText("www.vedaslabs.com");
        editwebsite.setText("www.vedaslabs.com");
        editnotes.setText("www.vedaslabs.com");
    }

    @OnClick({R.id.link})
    public void link() {
        movetoaboutus();

    }

    @OnClick({R.id.edittwitter})
    public void edittwitter() {
        movetoaboutus();

    }

    @OnClick({R.id.editfacebook})
    public void editfcebook() {
        movetoaboutus();

    }

    @OnClick({R.id.editwebsite})
    public void editwebsite() {
        movetoaboutus();

    }

    @OnClick({R.id.editnotes})
    public void editnotes() {
        movetoaboutus();

    }


    public void movetoaboutus() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerApisInterface.aboutus_url));
        startActivity(browserIntent);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_back = (ImageView) toolbar.findViewById(R.id.back);
        btn_back.setImageResource(R.drawable.ic_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView toolbartext = (TextView) toolbar.findViewById(R.id.toolbar_text);
        toolbartext.append(getString(R.string.about));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

