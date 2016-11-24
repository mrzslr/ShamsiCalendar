package com.mohammadreza.salari.shcalandar.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Views.MyTextView;
import com.mohammadreza.salari.shcalandar.Views.MyTextViewBold;

/**
 * Created by MohammadReza on 11/20/2016.
 */

public class DayDetailsActivity extends AppCompatActivity {

    MyTextViewBold shamsiDate;
    MyTextView miladiDate, hejriDate, shamsiEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_day_details);

        shamsiDate = (MyTextViewBold) findViewById(R.id.dialog_shamsi_date);
        miladiDate = (MyTextView) findViewById(R.id.dialog_miladi_date);
        hejriDate = (MyTextView) findViewById(R.id.dialog_hejri_date);
        shamsiEvent = (MyTextView) findViewById(R.id.dialog_shamsi_event);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            shamsiDate.setText(b.getString("shamsi_date"));
            miladiDate.setText(b.getString("miladi_date"));
            hejriDate.setText(b.getString("hejri_date"));
            shamsiEvent.setText(b.getString("shamsi_event"));
        }
    }

    public void closeDialog(View view) {
        DayDetailsActivity.this.finish();
    }
}
