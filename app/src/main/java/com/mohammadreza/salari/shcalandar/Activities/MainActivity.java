package com.mohammadreza.salari.shcalandar.Activities;


import android.app.ProgressDialog;

import android.net.Uri;
import android.os.*;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.text.*;

import android.content.*;


import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mohammadreza.salari.shcalandar.Application.MyApplication;
import com.mohammadreza.salari.shcalandar.Calendar.PersianDatePicker;
import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Utils.PersianCalendar;
import com.mohammadreza.salari.shcalandar.Views.MyTextView;
import com.mohammadreza.salari.shcalandar.Views.MyTextViewBold;


public class MainActivity extends AppCompatActivity implements PersianDatePicker.OnDateChangedListener,
        GoogleApiClient.OnConnectionFailedListener {
    PersianDatePicker persianDatePicker;
    BroadcastReceiver tickReceiver;
    MyApplication app;
    ImageView header;
    Toolbar toolbar;
    MyTextView txtToolbarTitle;
    MyTextViewBold txtShamsiDate;
    MyTextView txtMiladiDate, txtHejriDate, txtTodayEvent;
    FloatingActionButton fab;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplicationContext();
        // setStatusBarTranslucent(true);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        txtToolbarTitle = (MyTextView) toolbar.findViewById(R.id.toolbar_title);
        header = (ImageView) findViewById(R.id.header);

        persianDatePicker = (PersianDatePicker) findViewById(R.id.PersianDatePicker);
        persianDatePicker.setPersianCalendar(app.pCalendar);
        persianDatePicker.setOnDateChangedListener(this);
        final PersianCalendar pCalendar = persianDatePicker.getPersianCalendar();
        int day = pCalendar.getPersianDay();
        int month = pCalendar.getPersianMonth();

        txtToolbarTitle.setText(pCalendar.getPersianWeekDayName() + "  -  " + pCalendar.getPersianDay()
                + " / " + pCalendar.getPersianMonth() + " / " + pCalendar.getPersianYear());
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this /* FragmentActivity */, MainActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettings = getSharedPreferences("googleAccount", 0);
                if (mSettings.contains("personName")) {
                    startActivity(new Intent(MainActivity.this, MyEventsActivity.class));
                } else {

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });

        txtShamsiDate = (MyTextViewBold) findViewById(R.id.txtShamsiDate);
        txtMiladiDate = (MyTextView) findViewById(R.id.txtMiladiDate);
        txtHejriDate = (MyTextView) findViewById(R.id.txtHejriDate);
        txtTodayEvent = (MyTextView) findViewById(R.id.txtTodayEvent);


        //showCalendar(pCalendar);

        if (month <= 3) {
            //spring
            Glide.with(MainActivity.this).load(R.drawable.spring).into(header);
        } else if (month > 3 && month <= 6) {
            //summer
            Glide.with(MainActivity.this).load(R.drawable.su).into(header);
        } else if (month > 6 && month <= 9) {
            //auntum
            Glide.with(MainActivity.this).load(R.drawable.au).into(header);
        } else {
            //winter
            Glide.with(MainActivity.this).load(R.drawable.wi).into(header);
        }

        txtShamsiDate.setText(pCalendar.getPersianLongDate());
        SimpleDateFormat df = new SimpleDateFormat("EEEE yyyy-MMMM(MM)-dd");
        String formattedDate = df.format(pCalendar.getTime());
        txtMiladiDate.setText(formattedDate);
        txtHejriDate.setText(pCalendar.writeIslamicDate());
        txtTodayEvent.setText(pCalendar.getGEvent(day));

    }


    private void showCalendar(PersianCalendar pCalendar) {

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


    //---------- --------------//

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            Uri personPhotoUrl = acct.getPhotoUrl();
            String email = acct.getEmail();
            //Glide.with(GoogleCalendarActivity.this).load(personPhotoUrl).into(imgProfilePic);
            try {
                mSettings = getSharedPreferences("googleAccount", 0);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("personName", personName);
                editor.putString("email", email);
                editor.putString("personPhotoUrl", personPhotoUrl.toString());
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(MainActivity.this, MyEventsActivity.class));
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        // updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        //updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    /*
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    */
    //------------- -------------//
/*
    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
*/
}
