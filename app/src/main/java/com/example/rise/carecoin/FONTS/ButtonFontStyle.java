package com.example.rise.carecoin.FONTS;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by vedas on 3/27/2015.
 */
public class ButtonFontStyle extends Button {
    public ButtonFontStyle(Context context) {
        super(context);
        init();
    }

    public ButtonFontStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonFontStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Lato-Regular.ttf");
        setTypeface(tf);
    }
}
