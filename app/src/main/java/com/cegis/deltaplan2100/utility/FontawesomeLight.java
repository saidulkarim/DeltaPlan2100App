package com.cegis.deltaplan2100.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class FontawesomeLight extends AppCompatTextView {
    public FontawesomeLight(Context context) {
        super(context);
        init();
    }

    public FontawesomeLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontawesomeLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/fa-solid-900.ttf");
        setTypeface(tf);
    }
}