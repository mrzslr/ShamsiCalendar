package com.mohammadreza.salari.shlibrary.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by MohammadReza on 24/06/2016.
 */
public class MyTextViewBold extends TextView {
    public MyTextViewBold(Context context) {
        super(context);
        init();
    }

    public MyTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {

        if (!isInEditMode()) {

            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/is_bold");
            setTypeface(tf);
        }
    }


}
