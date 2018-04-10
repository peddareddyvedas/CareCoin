package com.example.rise.carecoin.Alert;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.rise.carecoin.R;

/**
 * Created by .
 */
public class AlertShowingDialog extends Dialog {
    Dialog dialog;
    Context context;

    public AlertShowingDialog(Context context1, String message)
    {
        super(context1);
        context = context1;
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        //
        TextView txtView = (TextView) dialog.findViewById(R.id.textView);
        TextView txtView1 = (TextView) dialog.findViewById(R.id.text_info);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        txtView.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });

        if(!((Activity) context).isFinishing()) {
            dialog.show();

        }
    }
    public AlertShowingDialog(Context context1, String message,String title)
    {
        super(context1);
        context = context1;
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        //
        TextView txtView = (TextView) dialog.findViewById(R.id.textView);
        TextView txtView1 = (TextView) dialog.findViewById(R.id.text_info);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        txtView1.setText(title);

        txtView.setText(message);
        txtView.setGravity(View.TEXT_ALIGNMENT_GRAVITY);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });

        if(!((Activity) context).isFinishing()) {
            dialog.show();

        }
    }
}
