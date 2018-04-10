package com.example.rise.carecoin.SideMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.Model.ContactModel;
import com.example.rise.carecoin.R;
import com.intrusoft.sectionedrecyclerview.Section;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WAVE on 2/14/2018.
 */
public class ContactsViewController extends AppCompatActivity {
    RecyclerView careCoinrecyclerView;
    Toolbar toolbar;
    ImageView back, add, refresh;
    TextView tool_text;
    ArrayList<ContactModel> carecoinContacts;
    ArrayList<ContactModel> phoneContacts;
    int selectedPosition = -1;
    public static boolean isFromContacts = false;
    AdapterSectionRecycler adapterRecycler;
    EditText searchBox;
    ImageView cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setToolbar();
        searchBox = (EditText)findViewById(R.id.searchBox);
        cancel=(ImageView)findViewById(R.id.img_cancel);
        if (ContactsDataController.getInstance().contactsArrary!=null){
            CarecontactsSeperation();

        }

        // from contctsViewController class.

        setCarecoinResultRecyclerViewData(carecoinContacts,phoneContacts);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearchResults(s.toString().toLowerCase());
                cancel.setVisibility(View.VISIBLE);
                if (searchBox.getText().toString().isEmpty()){
                    cancel.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                updateSearchResults(s.toString().toLowerCase());
                cancel.setVisibility(View.VISIBLE);
                if (searchBox.getText().toString().isEmpty()){
                    cancel.setVisibility(View.GONE);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBox.getText().toString().length()>0){
                    searchBox.setText("");
                }
            }
        });
    }
    //filter the array
    private void updateSearchResults(String text){
        ArrayList<ContactModel> careCoin = new ArrayList();
        ArrayList<ContactModel> phone = new ArrayList();

        for(ContactModel d:carecoinContacts){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().startsWith(text)){
                careCoin.add(d);
            }
        }
        Log.e("carefilter","call"+careCoin.size());

        for(ContactModel d:phoneContacts){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().startsWith(text)){
                phone.add(d);
            }
        }
        Log.e("phone","call"+careCoin.size());
        //update recyclerview
        setCarecoinResultRecyclerViewData(careCoin,phone);
        adapterRecycler.notifyDataSetChanged();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "call");
        ContactsDataController.getInstance().loadContactsOnSeparateThread();
        setCarecoinResultRecyclerViewData(carecoinContacts,phoneContacts);
        adapterRecycler.notifyDataSetChanged();
        Log.e("pcontacts", "call" + phoneContacts.size());
        Log.e("coinsize", "call" + carecoinContacts.size());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        Log.e("contactsMessageEvent1", "" + event.message);
        String resultData = event.message.trim();
        if (resultData.equals("refreshContacts")) {
            Log.e("sidemenuMessageevent", "call" + event.message);
            Log.e("arraysize", "call" + phoneContacts.size());
            Log.e("coinarraysize", "call" + carecoinContacts.size());
            if (ContactsDataController.getInstance().contactsArrary!=null){
                CarecontactsSeperation();

            }
            setCarecoinResultRecyclerViewData(carecoinContacts,phoneContacts);
            adapterRecycler.notifyDataSetChanged();
        }

    }

    ///////////
    public static class MessageEvent {
        public final String message;

        public MessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public void CarecontactsSeperation() {
        carecoinContacts = new ArrayList<ContactModel>();
        phoneContacts = new ArrayList<ContactModel>();

        Log.e("contactsMessageEvent1Co", "" + ContactsDataController.getInstance().contactsArrary.size());

        for (ContactModel objContact : ContactsDataController.getInstance().contactsArrary) {

            if (objContact.iscareContact == true) {
                carecoinContacts.add(objContact);
                ContactsDataController.getInstance().removeParentEmailsInCareCoinContacts(carecoinContacts);
            } else {
                phoneContacts.add(objContact);
            }
            Log.e("carecoinContactsarray", "call" + carecoinContacts.size());
            Log.e("phoneContacts", "call" + phoneContacts.size());
        }
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_back);

        add = (ImageView) findViewById(R.id.img_share);
        add.setBackgroundResource(R.drawable.ic_add);
        tool_text = (TextView) toolbar.findViewById(R.id.toolbar_text);
        tool_text.setText("To Contacts");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), AddpayeeActivity.class));

            }
        });
        refresh = (ImageView) toolbar.findViewById(R.id.img_refresh);
        refresh.setImageResource(R.drawable.ic_refresh);
        refresh.setVisibility(View.GONE);

    }

    public void setCarecoinResultRecyclerViewData(ArrayList<ContactModel> carecoinContacts,ArrayList<ContactModel> phoneContacts) {
        careCoinrecyclerView = (RecyclerView) findViewById(R.id.recyclerview_activity);
        //setLayout Manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        careCoinrecyclerView.setLayoutManager(linearLayoutManager);
        careCoinrecyclerView.setHasFixedSize(true);

        //Create a List of Child DataModel
        List<Child> childList = new ArrayList<>();
        for (ContactModel contactModel : carecoinContacts) {
            childList.add(new Child(contactModel.getEmail(), contactModel.getPhoto(), contactModel.getMobileNumber(),contactModel.getName()));
        }

        //Create a List of Section DataModel implements Section
        List<SectionHeader> sections = new ArrayList<>();
        sections.add(new SectionHeader(childList, "People on CareCoin"));


        childList = new ArrayList<>();
        for (ContactModel contactModel : phoneContacts) {
            childList.add(new Child(contactModel.getEmail(), contactModel.getPhoto(), contactModel.getMobileNumber(),contactModel.getName()));
        }
        sections.add(new SectionHeader(childList, "Others"));


        adapterRecycler = new AdapterSectionRecycler(this, sections);
        careCoinrecyclerView.setAdapter(adapterRecycler);
        adapterRecycler.notifyDataSetChanged();
    }

    public void alertDialogueForEmail(final String emailtext) {
        Log.e("alert", "call");
        final Dialog emailDilog = new Dialog(this);
        emailDilog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        emailDilog.setCancelable(false);
        emailDilog.setCanceledOnTouchOutside(false);
        emailDilog.setCancelable(true);
        emailDilog.setContentView(R.layout.email_alert);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(emailDilog.getWindow().getAttributes());
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        emailDilog.getWindow().setAttributes(lp);
        emailDilog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        emailDilog.show();

        final TextView text = (TextView) emailDilog.findViewById(R.id.textview2);
        text.setText(emailtext);

        final Button no = (Button) emailDilog.findViewById(R.id.btn_no);
        Button yes = (Button) emailDilog.findViewById(R.id.btn_yes);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailDilog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailDilog.dismiss();

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailtext});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, "This is an email sent using CareCion wrapper from an Spectrum App.");
                try {
                    startActivity(Intent.createChooser(i, "Choose an Email client :"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ContactsViewController.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class Child {

        String email, phone,name;
        Bitmap image;

        public Child(String email, Bitmap image, String phone,String name) {
            this.email = email;
            this.phone = phone;
            this.image = image;
            this.name=name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public Bitmap getImg() {
            return image;
        }

        public String getName() {
            return name;
        }
    }

    public class SectionHeader implements Section<Child> {

        List<Child> childList;
        String sectionText;

        public SectionHeader(List<Child> childList, String sectionText) {
            this.childList = childList;
            this.sectionText = sectionText;
        }

        @Override
        public List<Child> getChildItems() {
            return childList;
        }

        public String getSectionText() {
            return sectionText;
        }
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public SectionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.section);
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context ctx;
        TextView txt_name, txt_phone, txt_email;
        CircleImageView imageView;
        RelativeLayout invite;

        public ChildViewHolder(View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.name);
            txt_email = (TextView) itemView.findViewById(R.id.email);
            txt_phone = (TextView) itemView.findViewById(R.id.phonenumber);
            invite = (RelativeLayout) itemView.findViewById(R.id.rl_invite);
            imageView = (CircleImageView) itemView.findViewById(R.id.img_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }


    }
    public class AdapterSectionRecycler extends SectionRecyclerViewAdapter<SectionHeader, Child, SectionViewHolder, ChildViewHolder> {

        Context context;

        public AdapterSectionRecycler(Context context, List<SectionHeader> sectionItemList) {
            super(context, sectionItemList);
            this.context = context;

        }

        @Override
        public SectionViewHolder onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.sectionheader, sectionViewGroup, false);
            return new SectionViewHolder(view);
        }

        @Override
        public ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_contacts_items, childViewGroup, false);
            return new ChildViewHolder(view);
        }

        @Override
        public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, int sectionPosition, SectionHeader section) {
            sectionViewHolder.name.setText(section.sectionText);
        }
        @Override
        public void onBindChildViewHolder(ChildViewHolder childViewHolder, final int position, final int childPosition, final Child child) {
            childViewHolder.txt_email.setText(child.getEmail());
            childViewHolder.txt_phone.setText(child.getPhone());
            childViewHolder.imageView.setImageBitmap(child.getImg());
            childViewHolder.txt_name.setText(child.getName());
            ///pos=o means child oneee
            if (position == 0) {
                childViewHolder.invite.setVisibility(View.GONE);
                if (selectedPosition == childPosition) {
                    Log.e("isFromContacts", "call" + selectedPosition);
                    ContactModel contactModel = new ContactModel();
                    contactModel = carecoinContacts.get(selectedPosition);
                    Log.e("contactModel", "call" + contactModel.email);
                    Log.e("contactModel", "call" + contactModel.walletAddress);

                    RelativeLayout relative = (RelativeLayout) childViewHolder.itemView.findViewById(R.id.relativesetting);
                    relative.setBackgroundColor(Color.parseColor("#cccccc"));

                    ContactsDataController.getInstance().selectedContactModel=contactModel;


                    isFromContacts = true;
                    finish();
                    Intent intent = new Intent(getApplicationContext(), HomeActivityViewController.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    RelativeLayout relative = (RelativeLayout) childViewHolder.itemView.findViewById(R.id.relativesetting);
                    relative.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (selectedPosition != childPosition) {
                            selectedPosition = childPosition;
                        } else {
                            selectedPosition = -1;
                        }
                        notifyDataSetChanged();

                    }

                });

            }///pos=o means child two
            else if (position == 1) {
                Log.e("itemViewposition", "call" + childPosition);
                childViewHolder.invite.setVisibility(View.VISIBLE);
                ShapeDrawable shapedrawable = new ShapeDrawable();
                shapedrawable.setShape(new RectShape());
                shapedrawable.getPaint().setColor(Color.parseColor("#38baa6"));
                shapedrawable.getPaint().setStrokeWidth(5f);
                shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                childViewHolder.invite.setBackground(shapedrawable);

                childViewHolder.invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogueForEmail(child.getEmail());
                        adapterRecycler.notifyDataSetChanged();
                    }
                });
            }

        }
    }


}
