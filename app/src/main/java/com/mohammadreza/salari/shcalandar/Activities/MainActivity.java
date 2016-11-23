package com.mohammadreza.salari.shcalandar.Activities;


import android.database.Cursor;

import android.net.Uri;
import android.os.*;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.*;


import com.bumptech.glide.Glide;
import com.mohammadreza.salari.shcalandar.MyApplication;
import com.mohammadreza.salari.shcalandar.PersianDatePicker;
import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Utils.PersianCalendar;
import com.mohammadreza.salari.shcalandar.Views.MyTextView;
import com.mohammadreza.salari.shcalandar.Views.MyTextViewBold;


public class MainActivity extends AppCompatActivity implements PersianDatePicker.OnDateChangedListener {
    PersianDatePicker persianDatePicker;
    BroadcastReceiver tickReceiver;
    MyApplication app;
    ImageView header;
    Toolbar toolbar;
    MyTextView txtToolbarTitle;
    MyTextViewBold txtShamsiDate;
    MyTextView txtMiladiDate, txtHejriDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplicationContext();
        setStatusBarTranslucent(true);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        txtToolbarTitle = (MyTextView) toolbar.findViewById(R.id.toolbar_title);
        header = (ImageView) findViewById(R.id.header);
        Glide.with(MainActivity.this).load(R.drawable.su).into(header);
        final CollapsingToolbarLayout clpJobDetails = (CollapsingToolbarLayout) findViewById(R.id.collapseToolbarJobDetails);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //  clpJobDetails.setTitle("عنوان");
                    txtToolbarTitle.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        txtToolbarTitle.animate()
                                .alpha(1.0f)
                                .setDuration(500);
                    }


                    isShow = true;
                } else if (isShow) {
                    // clpJobDetails.setTitle("");
                    txtToolbarTitle.setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        txtToolbarTitle.animate()
                                .alpha(0.0f)
                                .setDuration(500);
                    }
                    isShow = false;
                }
            }
        });

        txtShamsiDate = (MyTextViewBold) findViewById(R.id.txtShamsiDate);
        txtMiladiDate = (MyTextView) findViewById(R.id.txtMiladiDate);
        txtHejriDate = (MyTextView) findViewById(R.id.txtHejriDate);


        persianDatePicker = (PersianDatePicker) findViewById(R.id.PersianDatePicker);
        persianDatePicker.setPersianCalendar(app.pCalendar);
        persianDatePicker.setOnDateChangedListener(this);

        final PersianCalendar pCalendar = persianDatePicker.getPersianCalendar();
        showCalendar(pCalendar);

        txtShamsiDate.setText(pCalendar.getPersianLongDate());
        SimpleDateFormat df = new SimpleDateFormat("EEEE yyyy-MMMM(MM)-dd");
        String formattedDate = df.format(pCalendar.getTime());
        txtMiladiDate.setText(formattedDate);
        txtHejriDate.setText(pCalendar.writeIslamicDate());

/*
        Utility utility = new Utility();
        List<String> events = new ArrayList<String>();
        events = utility.readCalendarEvent(MainActivity.this);
        //
        for (int i = 0; i < events.size(); i++) {
            Log.i("#" + i, events.get(i) + "");
        }

*/
    }


    private void showCalendar(PersianCalendar pCalendar) {
        int day = pCalendar.getPersianDay();
        /*
        mText1.setText(pCalendar.getPersianLongDateAndTime() + " " + pCalendar.getPEvent(day));
        mText2.setText(pCalendar.writeIslamicDate() + " " + pCalendar.getHEvent(day));


        SimpleDateFormat df = new SimpleDateFormat("EEEE yyyy-MMMM(MM)-dd");
        String formattedDate = df.format(pCalendar.getTime());
        mText3.setText(formattedDate + " " + pCalendar.getGEvent(day));

        txtPersianMonth.setText(pCalendar.getPersianMonthName());
        txtPersianYear.setText(pCalendar.getPersianYear() + "");
*/
    }

    @Override
    public void onDateChanged(PersianCalendar pCalendar) {
        // TODO: Implement this method
        app.pCalendar = pCalendar;
        showCalendar(pCalendar);

    }

    @Override
    public void onHijriAdjust(PersianCalendar persianCalendar, int hijriAdjust) {
        // TODO: Implement this method
        app.pCalendar = persianCalendar;
        showCalendar(persianCalendar);
        app.writeConfig(getCacheDir().getParent(), app);
    }


    @Override
    public void onAddClicked(PersianCalendar persianCalendar) {
        // TODO: Implement this method
    }

    public class Utility {

        public ArrayList<String> nameOfEvent = new ArrayList<String>();
        public ArrayList<String> startDates = new ArrayList<String>();
        public ArrayList<String> endDates = new ArrayList<String>();
        public ArrayList<String> descriptions = new ArrayList<String>();

        public ArrayList<String> readCalendarEvent(Context context) {
            Cursor cursor = context.getContentResolver()
                    .query(
                            Uri.parse("content://com.android.calendar/events"),
                            new String[]{"calendar_id", "title", "description",
                                    "dtstart", "dtend", "eventLocation"}, null,
                            null, null);
            cursor.moveToFirst();
            // fetching calendars name
            String CNames[] = new String[cursor.getCount()];

            // fetching calendars id
            nameOfEvent.clear();
            startDates.clear();
            endDates.clear();
            descriptions.clear();
            for (int i = 0; i < CNames.length; i++) {

                nameOfEvent.add(cursor.getString(1));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));
                CNames[i] = cursor.getString(1);
                cursor.moveToNext();

            }
            return nameOfEvent;
        }

        public String getDate(long milliSeconds) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd/MM/yyyy hh:mm:ss a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        }


    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calendar:
                startActivity(new Intent(MainActivity.this, GoogleCalendarActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
