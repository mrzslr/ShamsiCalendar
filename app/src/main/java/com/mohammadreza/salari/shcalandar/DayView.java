package com.mohammadreza.salari.shcalandar;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.text.*;


/**
 * Created by Irshst 1395 .
 */
public class DayView extends LinearLayout {//} TextView {

    private static final int[] STATE_CURRENT = {R.attr.state_current};

    private boolean mCurrent, mVacation;
    private TextView txtUp, txtMid, txtDown;
    private ImageView imgVacation;
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

        txtUp = (TextView) findViewById(R.id.dayTextUp);
        txtMid = (TextView) findViewById(R.id.dayText);
        txtDown = (TextView) findViewById(R.id.dayTextDown);
        imgVacation = (ImageView) findViewById(R.id.imgVacation);
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
        if (vacation == true)

            txtMid.setTextColor(Color.RED);
        else txtMid.setTextColor(Color.WHITE);

    }

    public void clearText() {
        txtUp.setText("");
        txtMid.setText("");
        txtDown.setText("");

    }

    public void setText(String txt, boolean hasEvent, boolean isVacation) {

        if (hasEvent) txtMid.setText(Html.fromHtml("<u>" + txt + "</u>"));
        else txtMid.setText(txt);
        if (isVacation == true) {
            imgVacation.setVisibility(VISIBLE);
            txtMid.setTextColor(Color.RED);
        } else {
            imgVacation.setVisibility(INVISIBLE);
            txtMid.setTextColor(getContext().getResources().getColor(R.color.black));
        }
    }

    public void setTextUp(String txt, boolean hasEvent, boolean isVacation) {
        if (hasEvent) txtUp.setText(Html.fromHtml("<u>" + txt + "</u>"));
        else
            txtUp.setText(txt);
        if (isVacation == true) {
            imgVacation.setVisibility(VISIBLE);
            txtUp.setTextColor(Color.RED);
        } else {
            txtUp.setTextColor(getContext().getResources().getColor(R.color.black));
            imgVacation.setVisibility(INVISIBLE);
        }
    }

    public void setTextDown(String txt, boolean hasEvent, boolean isVacation) {
        if (hasEvent) txtDown.setText(Html.fromHtml("<u>" + txt + "</u>"));
        else
            txtDown.setText(txt);
        if (isVacation == true) {
            imgVacation.setVisibility(VISIBLE);
            txtDown.setTextColor(Color.RED);
        } else {
            txtDown.setTextColor(getContext().getResources().getColor(R.color.black));
            imgVacation.setVisibility(INVISIBLE);
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
