package com.example.rise.carecoin.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rise.carecoin.DataBase.UserDataController;
import com.example.rise.carecoin.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by Rise on 09/10/2017.
 */

public class LanguageViewController extends AppCompatActivity {
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> name1 = new ArrayList<>();

    RecyclerView addRecyclerView;
    View view;
    Toolbar toolbar;
    ImageView back, home, add, refresh;
    int selectedPosition = 5;
    int tempSelectedPosition = 5;
    Language device;
    String selectedLanguage, language1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        ButterKnife.bind(this);
        setToolbar();
        init();
    }

    private void init() {

        name.add("Arabic");
        name.add("Chinese(Traditional)");
        name.add("Spanish");
        name.add("Chinese(simplified)");
        name.add("English (India)");
        name.add("English (United States)");
        name.add("Freench");
        name.add("German");
        name.add("Greek");
        name.add("Japanese");
        name.add("Portuguese");
        name.add("Russian");


        name1.add("لعربية");
        name1.add("繁體中文");
        name1.add("Español/Castilian");
        name1.add("简体中文");
        name1.add("");
        name1.add("");
        name1.add("français");
        name1.add("Deutsch");
        name1.add("ελληνικά (ellēniká)");
        name1.add("(日本語 / Nihongo)");
        name1.add("português");
        name1.add("Русский язык");
//["لعربية", " 繁體中文","Español/Castilian","简体中文","","","français","Deutsch","ελληνικά (ellēniká)","(日本語 / Nihongo)","português","Русский язык"]
        addRecyclerView = (RecyclerView) findViewById(R.id.recycler_mydevice);
        device = new Language(name, getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        addRecyclerView.setLayoutManager(horizontalLayoutManager);
        addRecyclerView.setAdapter(device);


    }


    private void setToolbar() {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        home = (ImageView) toolbar.findViewById(R.id.toolbar_icon);
        home.setImageResource(R.drawable.ic_home);
        home.setVisibility(View.INVISIBLE);

        back = (ImageView) toolbar.findViewById(R.id.back);
        back.setImageResource(R.drawable.ic_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        refresh = (ImageView) toolbar.findViewById(R.id.img_refresh);
        refresh.setVisibility(View.INVISIBLE);


        TextView toolbartext = (TextView) toolbar.findViewById(R.id.toolbar_text);
        toolbartext.append(getString(R.string.language));


    }

    // Step 1:-
    public class Language extends RecyclerView.Adapter<Language.ViewHolder> {

        // step 3:-
        ArrayList<String> arrayList = new ArrayList<>();
        Context ctx;
        ImageView button;


        public Language(ArrayList<String> arrayList, Context ctx) {
            this.ctx = ctx;
            this.arrayList = arrayList;
        }

        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.language_customlayout, parent, false);


            ViewHolder myViewHolder = new ViewHolder(view, ctx, arrayList);
            return myViewHolder;


        }

        //step 6:-
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            ImageView image = (ImageView) holder.itemView.findViewById(R.id.tick);

            holder.userName.setText(name.get(position));
            holder.userName1.setText(name1.get(position));

            if (selectedPosition == position) {
                Log.e("if", "called");
                image.setVisibility(View.VISIBLE);
                Log.e("selectedLanguage", "called" + selectedLanguage);
                RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative_language);
                // relative.setBackgroundColor(Color.parseColor("#a6dad2"));
            } else {
                Log.e("else", "called");
                image.setVisibility(View.INVISIBLE);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    Log.e("SelctedPosition", "" + position);
                    if (selectedPosition != position) {
                        tempSelectedPosition = position;
                        selectedLanguage = holder.userName.getText().toString();
                        language1 = holder.userName1.getText().toString();
                        dilog();

                    } else if (selectedPosition == position) {
                        tempSelectedPosition = position;
                        selectedLanguage = holder.userName.getText().toString();
                        language1 = holder.userName1.getText().toString();
                        dilog();

                    }
                }
            });

            if (position == 4 || position == 5) {
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)
                        holder.layout.getLayoutParams();
                params1.height = 110;
                holder.layout.setLayoutParams(params1);
                Log.e("layout", "call");
            }
        }


        // step 4:-
        @Override
        public int getItemCount() {

            Log.e("listarray", "" + arrayList.size());
            return arrayList.size();
        }


        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            TextView userName, userName1;
            ArrayList<String> arrayList = new ArrayList<String>();
            Context ctx;
            RelativeLayout layout;

            public ViewHolder(View itemView, Context ctx, final ArrayList<String> arrayList) {
                super(itemView);

                this.arrayList = arrayList;
                this.ctx = ctx;
                userName = (TextView) itemView.findViewById(R.id.devicename);
                userName1 = (TextView) itemView.findViewById(R.id.deviceid);
                layout = (RelativeLayout) itemView.findViewById(R.id.relative_language);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {


            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dilog() {

        final Dialog dialog = new Dialog(LanguageViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        Button ok = (Button) dialog.findViewById(R.id.btn_ok);
        Button cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        TextView text = (TextView) dialog.findViewById(R.id.text_name);
        TextView text1 = (TextView) dialog.findViewById(R.id.text_name1);

        text.setText("" + selectedLanguage);
        text1.setText("" + language1);
        Log.e("language", "" + selectedLanguage + "" + language1);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempSelectedPosition = selectedPosition;
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = tempSelectedPosition;
                device.notifyDataSetChanged();
                dialog.dismiss();
                UserDataController.getInstance().currentUser.preferdlanguage =selectedLanguage;
                UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);
            }
        });

    }


    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}

