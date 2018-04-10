package com.example.rise.carecoin.FONTS;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vedas on 3/27/2015.
 */
public class BoldTextViewFontStyle extends TextView {


    public BoldTextViewFontStyle(Context context) {
        super(context);
        init();
    }

    public BoldTextViewFontStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldTextViewFontStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Lato-Bold.ttf");
        setTypeface(tf);
    }
}
