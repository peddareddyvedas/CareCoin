package com.example.rise.carecoin.Alert;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rise.carecoin.R;


/**
 * Created by .
 */
public class RefreshShowingDialog extends Dialog {
    Dialog dialog;
    ImageView imageView;
    Context context;

    public RefreshShowingDialog(Context context1) {
        super(context1);
        context = context1;
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        TextView textView=(TextView)dialog.findViewById(R.id.connecting);
        imageView=(ImageView)dialog.findViewById(R.id.image_rottate) ;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dualspin_selector);

    }

    public  void  showAlert(){

        if(!((Activity) context).isFinishing()) {

            RotateAnimation rotate = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);

            rotate.setDuration(600);
            rotate.setRepeatCount(Animation.INFINITE);
            imageView.setAnimation(rotate);
            dialog.show();

        }

    }

    public void hideRefreshDialog(){
        Log.e("hideRefreshDialog", "call");
        dialog.dismiss();
    }
}
