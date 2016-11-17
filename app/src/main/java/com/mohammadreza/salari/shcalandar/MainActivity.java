package com.mohammadreza.salari.shcalandar;

import android.app.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;

import android.util.Log;
import android.widget.*;

import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.*;

import com.mohammadreza.salari.shcalandar.Utils.PersianCalendar;

public class MainActivity extends Activity implements PersianDatePicker.OnDateChangedListener {
    PersianDatePicker persianDatePicker;
    TextView mText1, mText2, mText3;
    BroadcastReceiver tickReceiver;
    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplicationContext();
        setContentView(R.layout.main);
        mText1 = (TextView) findViewById(R.id.shamsi);
        mText2 = (TextView) findViewById(R.id.hejri);

        mText3 = (TextView) findViewById(R.id.miladi);
        persianDatePicker = (PersianDatePicker) findViewById(R.id.PersianDatePicker);
        persianDatePicker.setPersianCalendar(app.pCalendar);
        persianDatePicker.setOnDateChangedListener(this);

        PersianCalendar pCalendar = persianDatePicker.getPersianCalendar();
        showCalendar(pCalendar);


        Utility utility = new Utility();
        List<String> events = new ArrayList<String>();
        events = utility.readCalendarEvent(MainActivity.this);
        utility.    
        for (int i = 0; i < events.size(); i++) {
            Log.i("#" + i, events.get(i) + "");
        }


    }


    private void showCalendar(PersianCalendar pCalendar) {
        int day = pCalendar.getPersianDay();
        mText1.setText(pCalendar.getPersianLongDateAndTime() + " " + pCalendar.getPEvent(day));
        mText2.setText(pCalendar.writeIslamicDate() + " " + pCalendar.getHEvent(day));


        SimpleDateFormat df = new SimpleDateFormat("EEEE yyyy-MMMM(MM)-dd");
        String formattedDate = df.format(pCalendar.getTime());
        mText3.setText(formattedDate + " " + pCalendar.getGEvent(day));

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
}
