package com.mohammadreza.salari.shcalandar.Calendar;


import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.*;
import com.mohammadreza.salari.shcalandar.Activities.DayDetailsActivity;
import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Utils.PersianCalendar;
import com.mohammadreza.salari.shcalandar.Utils.PersianCalendarConstants;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class PersianDatePicker extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    private OnDateChangedListener mListener;

    private TextView mTitleView, mTitleUp, mTitleDown;
    private ImageButton mPrev;
    private ImageButton mNext;
    private LinearLayout mWeeksView;
    private boolean externalPersianCalendar = false, disableAddKey = false;
    private final LayoutInflater mInflater;
    private TextView mSelectionText;
    private LinearLayout mHeader;
    private PersianCalendar pCalendar;
    private DayView lastDaySelected = null;
    private Context context;
    int selectedYear = 0, selectedMonth, selectedDay;
    int currentYear, currentMonth, currentDay;

    @Override

    protected void onFinishInflate() {
        super.onFinishInflate();

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleUp = (TextView) findViewById(R.id.titleUp);
        mTitleDown = (TextView) findViewById(R.id.titleDown);
        mPrev = (ImageButton) findViewById(R.id.prev);
        mNext = (ImageButton) findViewById(R.id.next);
        mWeeksView = (LinearLayout) findViewById(R.id.weeks);

        mHeader = (LinearLayout) findViewById(R.id.header);
        mSelectionText = (TextView) findViewById(R.id.selection_title);

        mPrev.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrev.setOnLongClickListener(this);
        mNext.setOnLongClickListener(this);
        if (!externalPersianCalendar)
            refresh();

    }

    public void refresh() {
        if (pCalendar == null) return;

        pCalendar.refresh();
        //populateDays();

        populateMonthLayout();
    }

    @Override
    public void onClick(View v) {

        if (pCalendar != null) {
            int id = v.getId();
            if (id == R.id.prev) {
                pCalendar.prev();
                populateMonthLayout();

            } else if (id == R.id.next) {

                pCalendar.next();

                populateMonthLayout();

            }

        }


    }

    @Override
    public boolean onLongClick(View v) {

        if (pCalendar != null) {
            int id = v.getId();
            if (id == R.id.prev) {
                pCalendar.prevYear();
                populateMonthLayout();

            } else if (id == R.id.next) {

                pCalendar.nextYear();

                populateMonthLayout();

            }

        }
        return true;

    }

    private final static int rtl7(int i) {
        return 6 - i;
    }

    /*
    private void populateDays() {

        LinearLayout layout = (LinearLayout) findViewById(R.id.days);

    for (int i = 0; i < 7; i++) {
            TextView textView = (TextView) layout.getChildAt(rtl7(i));
            textView.setText(PersianCalendarConstants.getShamsiWeekDay(i));


        }


    }
*/
    public PersianCalendar getPersianCalendar() {

        return pCalendar;
    }

    public void setPersianCalendar(PersianCalendar persianCalendar) {

        pCalendar = persianCalendar;
        //pCalendar.calculateMonthLastDay();
        currentDay = pCalendar.getPersianDay();
        currentMonth = pCalendar.getPersianMonth();
        currentYear = pCalendar.getPersianYear();
        selectedYear = pCalendar.selectedYear;
        selectedMonth = pCalendar.selectedMonth;
        selectedDay = pCalendar.selectedDay;
        refresh();

    }

    private WeekView getWeekView(int index) {
        return (WeekView) mWeeksView.getChildAt(index);
    }

    private final DayView getDayView(int week, int day) {
        WeekView weekView = getWeekView(week);
        return (DayView) weekView.getChildAt(rtl7(day));

    }

    private void resetDayView(int week, int day) {
        DayView d = getDayView(week, day);
        d.setSelected(false);
        d.clearText();
        d.setEnabled(false);
        d.setOnClickListener(null);
    }

    private void resetWeekView(int week) {
        for (int i = 0; i < 7; i++)

            resetDayView(week, i);
    }

    private void populateMonthLayout() {

        int wDay = pCalendar.persianMonthFirstDayWeekDay;
        int wCnt = 0;
        int cnt = pCalendar.persianWeekCount;
        String gMonth1 = pCalendar.getMonthForInt(pCalendar.gMonth1);
        String gMonth2 = pCalendar.getMonthForInt(pCalendar.gMonth2);
        String gYear = pCalendar.gYear1 == pCalendar.gYear2 ? Integer.toString(pCalendar.gYear1) : pCalendar.gYear1 + "-" + pCalendar.gYear2;

        String hMonth1 = PersianCalendarConstants.iMonthNames[pCalendar.hMonth1];
        String hMonth2 = PersianCalendarConstants.iMonthNames[pCalendar.hMonth2];
        String hYear = pCalendar.hYear1 == pCalendar.hYear2 ? PersianCalendarConstants.toArabicNumbers(pCalendar.hYear1) : PersianCalendarConstants.toArabicNumbers(pCalendar.hYear1) + "-" + PersianCalendarConstants.toArabicNumbers(pCalendar.hYear2);

        mTitleView.setText(pCalendar.getPersianMonthName() + "-" + PersianCalendarConstants.toArabicNumbers(pCalendar.getPersianYear()));
        mTitleUp.setText(gMonth1 + "/" + gMonth2 + "-" + gYear);
        mTitleDown.setText(hMonth1 + "/" + hMonth2 + "-" + hYear);

        for (int i = 0; i < wDay; i++)

            resetDayView(wCnt, i);


        for (int i = 1; ; i++) {
            final int day = i;
            String hDay = PersianCalendarConstants.toArabicNumbers(pCalendar.persianHDays[i]);
            String gDay = "" + pCalendar.persianGDays[i];


            final DayView dayView = getDayView(wCnt, wDay);


            dayView.setTextUp(gDay, !pCalendar.getGEvent(day).equals(""), pCalendar.getGVacation(day));
            dayView.setText(PersianCalendarConstants.toArabicNumbers(day), !pCalendar.getPEvent(day).equals(""), pCalendar.getPVacation(day) || wDay == 6);
            dayView.setTextDown(hDay, !pCalendar.getHEvent(day).equals(""), pCalendar.getHVacation(day));
            boolean isCurrent = pCalendar.isCurrent(i),
                    isSelected =
                            pCalendar.isSelected(i);
            dayView.setCurrent(isCurrent);

            if (isSelected || (isCurrent && selectedYear == 0)) {
                dayView.setSelected(true);
                if (!isSelected)
                    pCalendar.setSelectedDate(pCalendar.getPersianYear(), pCalendar.getPersianMonth(), i);
                lastDaySelected = dayView;
            }


            dayView.setEnabled(true);


            dayView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null && ((DayView) v).isEnabled()) {
                        if (lastDaySelected != null)

                            lastDaySelected.setSelected(false);


                        ((DayView) v).setSelected(true);

                        lastDaySelected = (DayView) v;

                        int year = pCalendar.getPersianYear();
                        int month = pCalendar.getPersianMonth();
                        pCalendar.setPersianDate(year, month, day);
                        pCalendar.setSelectedDate(year, month, day);
                        mListener.onDateChanged(pCalendar);

                        int day = pCalendar.getPersianDay();
                        SimpleDateFormat df = new SimpleDateFormat("EEEE yyyy-MMMM(MM)-dd");
                        String formattedDate = df.format(pCalendar.getTime());


                        Intent intent = new Intent(context, DayDetailsActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("shamsi_date", pCalendar.getPersianLongDate());
                        intent.putExtra("miladi_date", formattedDate);
                        intent.putExtra("hejri_date", pCalendar.writeIslamicDate());
                        intent.putExtra("shamsi_event", pCalendar.getGEvent(day)+" - "+pCalendar.getPEvent(day) + " - "+pCalendar.getHEvent(day));
                        context.startActivity(intent);


                    }

                }
            });


            if (i == pCalendar.persianMonthDays) break;
            if (wDay < 6) wDay++;
            else {
                wDay = 0;
                wCnt++;
            }

        }

        for (int i = wDay + 1; i < 7; i++)
            resetDayView(wCnt, i);

        int childCnt = mWeeksView.getChildCount() - 1;
        if (cnt < childCnt) {
            for (int i = cnt; i < childCnt; i++) {
                resetWeekView(i);
            }
        }
        if (!disableAddKey) {
            final DayView addBtn = getDayView(5, 6);
            addBtn.setEnabled(true);
            addBtn.setBackgroundResource(android.R.drawable.ic_menu_add);

            addBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null) {


                        mListener.onAddClicked(pCalendar);
                    }

                }
            });
        }
        /*
        final DayView rightBtn = getDayView(5, 5);
        rightBtn.setEnabled(false);
        rightBtn.setBackgroundResource(android.R.drawable.ic_media_previous);

        rightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                pCalendar.hAdjust--;
                pCalendar.calculateMonthLastDay();
                refresh();
                mListener.onHijriAdjust(pCalendar, pCalendar.hAdjust);

            }
        });
        */
/*
        final DayView leftBtn = getDayView(5, 4);
        leftBtn.setEnabled(false);
        leftBtn.setBackgroundResource(android.R.drawable.ic_media_next);

        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                pCalendar.hAdjust++;
                pCalendar.calculateMonthLastDay();
                refresh();
                mListener.onHijriAdjust(pCalendar, pCalendar.hAdjust);

            }
        });
        */
    }


    public PersianDatePicker(Context context) {
        this(context, null, -1);
    }

    public PersianDatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PersianDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context.getApplicationContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PersianDatePicker, 0, 0);

        externalPersianCalendar = a.getBoolean(R.styleable.PersianDatePicker_externalPersianCalendar, false);
        disableAddKey = a.getBoolean(R.styleable.PersianDatePicker_disableAddKey, false);

        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.calendar_layout, this);
        if (!externalPersianCalendar) {
            pCalendar = new PersianCalendar(context);
            currentDay = pCalendar.getPersianDay();
            currentMonth = pCalendar.getPersianMonth();
            currentYear = pCalendar.getPersianYear();
        }
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        mListener = onDateChangedListener;
    }

    /**
     * The callback used to indicate the user changed the date.
     * A class that wants to be notified when the date of PersianDatePicker
     * changes should implement this interface and register itself as the
     * listener of date change events using the PersianDataPicker's
     * setOnDateChangedListener method.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         * <p>
         * param newYear  The year that was set.
         * param newMonth The month that was set (1-12)
         * param newDay   The day of the month that was set.
         */
        void onHijriAdjust(PersianCalendar persianCalendar, int hijriAdjust);

        void onAddClicked(PersianCalendar persianCalendar);

        void onDateChanged(PersianCalendar persianCalendar);
    }

}
