package com.mohammadreza.salari.shcalandar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.RemoteViews;

import com.mohammadreza.salari.shcalandar.Activities.MainActivity;


public class MyWidgetProvider extends AppWidgetProvider {
    //	PersianCalendar pCalendar;
    MyApplication app;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        app = (MyApplication) context.getApplicationContext();
        // initializing widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        app.pCalendar.goToCurrentDate();
        //pCalendar=new PersianCalendar(context);
        // register for button event
        Intent configIntent = new Intent(context, MainActivity.class);

        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.title, configPendingIntent);

        // updating view with initial data
        remoteViews.setTextViewText(R.id.title, getTitle());
        remoteViews.setTextViewText(R.id.desc, getDesc());


        // request for widget update
        pushWidgetUpdate(context, remoteViews);
    }


    private String getDesc() {

        return app.pCalendar.getTodayEvent();
    }

    private String getTitle() {
        return app.pCalendar.getPersianLongDate();
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context,
                MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    public Bitmap buildUpdate(String time, Context context) {
        Bitmap myBitmap = Bitmap.createBitmap(160, 84, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(), "fonts/is.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(65);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(time, 80, 60, paint);
        return myBitmap;
    }
}
