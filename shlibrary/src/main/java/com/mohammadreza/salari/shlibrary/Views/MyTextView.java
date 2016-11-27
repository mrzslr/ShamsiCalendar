package com.mohammadreza.salari.shlibrary.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {

        if (!isInEditMode()) {

            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/is.ttf");
            setTypeface(tf);
        }
    }


}
