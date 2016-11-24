package com.mohammadreza.salari.shcalandar.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.mohammadreza.salari.shcalandar.Model.MyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MohammadReza on 11/23/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "shcalandar";

    // Contacts table name
    private static final String TABLE_EVENTS = "events";

    // Contacts Table Columns names
    private static final

    String KEY_ID = "id";
    private static final

    String KEY_SUMMARY = "summary";
    private static final

    String KEY_DESCRIPTION = "description";
    private static final

    String KEY_LOCATION = "location";
    private static final

    String KEY_START = "start";
    private static final

    String KEY_END = "end";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " TEXT," + KEY_SUMMARY + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_START + " TEXT,"
                + KEY_END + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

        // Create tables again
        onCreate(db);
    }

    public void addEvent(MyEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, event.getId());
        values.put(KEY_SUMMARY, event.getSummary());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_LOCATION, event.getLocation());
        values.put(KEY_START, event.getStart());
        values.put(KEY_END, event.getEnd());
        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
    }

    public MyEvent getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY_ID,
                        KEY_SUMMARY, KEY_DESCRIPTION, KEY_LOCATION, KEY_START, KEY_END}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MyEvent contact = new MyEvent(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        // return contact
        return contact;
    }

    public void clearDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM events";
        db.execSQL(clearDBQuery);
    }

    public List<MyEvent> getAllEvents() {
        List<MyEvent> eventList = new ArrayList<MyEvent>();

        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                MyEvent event = new MyEvent();
                event.setId(cursor.getString(0));
                event.setSummary(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setLocation(cursor.getString(3));
                event.setStart(cursor.getString(4));
                event.setEnd(cursor.getString(5));
                eventList.add(event);
            } while (cursor.moveToNext());
        }


        return eventList;
    }
}