package com.mohammadreza.salari.shcalandar.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Views.MyTextView;


public class DayView extends LinearLayout {//} TextView {

    private static final int[] STATE_CURRENT = {R.attr.state_current};

    private boolean mCurrent, mVacation;
    private MyTextView txtMid;
    LayoutInflater mInflater;

    public DayView(Context context) {
        super(context);
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.day_layout, this);

    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        txtMid = (MyTextView) findViewById(R.id.dayText);

    }

    public void setCurrent(boolean current) {
        mCurrent = current;
        if (current == true)
             setBackgroundResource(R.drawable.b_current);

        else

        setBackgroundResource(R.drawable.b_black);
    }

    public void setSelected(boolean select) {

        if (select == true) {
            if (mCurrent == true)

                setBackgroundResource(R.drawable.b_current_selected);
            else
                setBackgroundResource(R.drawable.b_selected);

        } else
            setCurrent(mCurrent);
    }

    public void hasEvent(boolean event) {
        if (event == true)

            txtMid.setTextColor(Color.GREEN);
    }

    public void setVacation(boolean vacation) {
        mVacation = vacation;
        if (vacation == true) {

            //  txtMid.setTextColor(Color.RED);
        } else {
            txtMid.setTextColor(Color.WHITE);
        }

    }

    public void clearText() {

        txtMid.setText("");


    }

    public void setText(String txt, boolean hasEvent, boolean isVacation) {

        if (hasEvent) txtMid.setText(Html.fromHtml("<u>" + txt + "</u>"));
        else txtMid.setText(txt);
        if (isVacation == true) {
            //imgVacation.setVisibility(VISIBLE);
//            txtMid.setTextColor(getContext().getResources().getColor(R.color.m_red));
            txtMid.setTextColor(getContext().getResources().getColor(R.color.m_red));

            // txtMid.setTextColor(getContext().getResources().getColor(R.color.black));
        } else {
            //imgVacation.setVisibility(INVISIBLE);
            txtMid.setTextColor(getContext().getResources().getColor(R.color.black));

        }
    }

    public boolean isCurrent() {
        return mCurrent;
    }
/*
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] state = super.onCreateDrawableState(extraSpace + 1);
       
        if (mCurrent) {
            mergeDrawableStates(state, STATE_CURRENT);
        }
		

        return state;
    }*/
}
