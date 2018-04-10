package com.example.rise.carecoin.FONTS;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rise on 08/02/2018.
 */

public class TextViewHeavyFontStyle extends TextView {


    public TextViewHeavyFontStyle(Context context) {
        super(context);
        init();
    }

    public TextViewHeavyFontStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewHeavyFontStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Lato-Heavy.ttf");
        setTypeface(tf);
    }
}
