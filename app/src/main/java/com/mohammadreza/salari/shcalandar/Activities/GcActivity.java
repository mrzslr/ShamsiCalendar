package com.mohammadreza.salari.shcalandar.Activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;
import com.mohammadreza.salari.shcalandar.Adapter.EventsAdapter;
import com.mohammadreza.salari.shcalandar.DB.DatabaseHandler;
import com.mohammadreza.salari.shcalandar.Model.MyEvent;
import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Utils.PersianCalendarUtils;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by MohammadReza on 11/22/2016.
 */

public class GcActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Toolbar toolbar;

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_ACCOUNT_PASSWORD = "accountPassword";
    private static final String PREF_ACCOUNT_TYPE = "accountType";
    private static final String PREF_AUTH_TOKEN = "authToken";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    List<MyEvent> myEvents;
    List<MyEvent> dbEvents;
    RecyclerView rvEvents;
    EventsAdapter eventsAdapter;
    DatabaseHandler db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc);
        toolbar = (Toolbar) findViewById(R.id.toolbar_gc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResultsFromApi();
            }
        });

        db = new DatabaseHandler(this);
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents);

        myEvents = new ArrayList<MyEvent>();
        dbEvents = new ArrayList<MyEvent>();
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");
        dbEvents = db.getAllEvents();
        eventsAdapter = new EventsAdapter(GcActivity.this, dbEvents);
        rvEvents.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvEvents.setLayoutManager(llm);

        rvEvents.setAdapter(eventsAdapter);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(GcActivity.this, "No Internet Connection ...", Toast.LENGTH_SHORT).show();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(GcActivity.this,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT);
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {

                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    String accountPassword =
                            data.getStringExtra(AccountManager.KEY_PASSWORD);
                    String accountType =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                    String authToken =
                            data.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.putString(PREF_ACCOUNT_PASSWORD, accountPassword);
                        editor.putString(PREF_ACCOUNT_TYPE, accountType);
                        editor.putString(PREF_AUTH_TOKEN, authToken);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GcActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("ShCalandar")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();

            Events events = mService.events().list("primary")
                    // .setMaxResults(40)
                    //.setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            for (Event event : items) {
                //DateTime start = event.getStart().getDateTime();
              MyEvent myEvent = new MyEvent();
                if (event.getId() != null) {
                    myEvent.setId(event.getId());
                } else {
                    myEvent.setId(String.valueOf(System.currentTimeMillis()));
                }
                if (event.getSummary() != null) {
                    myEvent.setSummary(event.getSummary());
                } else {
                    myEvent.setSummary(" ");
                }
                if (event.getDescription() != null) {
                    myEvent.setDescription(event.getDescription());
                } else {
                    myEvent.setDescription(" ");
                }
                if (event.getLocation() != null) {
                    myEvent.setLocation(event.getLocation());
                } else {
                    myEvent.setLocation(" ");
                }

                try {

                    SimpleDateFormat dtExibicao = new SimpleDateFormat("dd/MM/yyyy");
                    Date dtStart = new Date(event.getStart().getDateTime().getValue());
                    String stStart = dtExibicao.format(dtStart);
                    Date dtEnd = new Date(event.getEnd().getDateTime().getValue());
                    String stEnd = dtExibicao.format(dtEnd);

                    if (stStart == null) {
                        myEvent.setStart("");
                    } else {
                        myEvent.setStart(stStart);
                    }
                    if (stEnd == null) {
                        myEvent.setEnd("");
                    } else {
                        myEvent.setEnd(stEnd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



                db.addEvent(myEvent);
            }
            return eventStrings;
        }

        @Override
        protected void onPreExecute() {

            mProgress.show();

        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            /*
            if (output == null || output.size() == 0) {
                Toast.makeText(GcActivity.this, "No results returned.", Toast.LENGTH_SHORT).show();
            } else {
                output.add(0, "Data retrieved using the Google Calendar API:");
                Toast.makeText(GcActivity.this, TextUtils.join("\n", output), Toast.LENGTH_SHORT).show();

            }
            */

            /*
            for (int i = 0; i < myEvents.size(); i++) {
                Log.i("myevent location ", myEvents.get(i).getLocation() + "");
            }
            */
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GcActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(GcActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(GcActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
