package com.mohammadreza.salari.shcalandar.Activities;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mohammadreza.salari.shcalandar.Adapter.EventsAdapter;
import com.mohammadreza.salari.shcalandar.DB.DatabaseHandler;
import com.mohammadreza.salari.shcalandar.Model.MyEvent;
import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Views.MyTextViewBold;
import com.mohammadreza.salari.shcalandar.Views.ProgressWheel;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MyEventsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Toolbar toolbar;
    GoogleAccountCredential mCredential;
    private static final String TAG = "MyEventsActivity";
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

    CircularImageView imgPersonPhoto;
    MyTextViewBold txtPersonName;
    private GoogleApiClient mGoogleApiClient;
    SharedPreferences mSettings;
    SharedPreferences preferences;
    FloatingActionButton fabUpdateEvents;
    ProgressWheel progressLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        toolbar = (Toolbar) findViewById(R.id.toolbar_gc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressLoading = (ProgressWheel) findViewById(R.id.progress_loading);
        fabUpdateEvents = (FloatingActionButton) findViewById(R.id.fab_update_events);
        fabUpdateEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    db.clearDatabase();
                    myEvents.clear();
                    dbEvents.clear();
                    eventsAdapter.notifyDataSetChanged();
                    getResultsFromApi();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        txtPersonName = (MyTextViewBold) findViewById(R.id.txtPersonName);
        imgPersonPhoto = (CircularImageView) findViewById(R.id.imgPersonPhoto);
        mSettings = getSharedPreferences("googleAccount", 0);
        String personName = mSettings.getString("personName", "missing");
        txtPersonName.setText(personName);
        try {

            Glide.with(MyEventsActivity.this).load(mSettings.getString("personPhotoUrl", "missing")).into(imgPersonPhoto);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db = new DatabaseHandler(this);
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents);

        myEvents = new ArrayList<MyEvent>();
        dbEvents = new ArrayList<MyEvent>();


        eventsAdapter = new EventsAdapter(MyEventsActivity.this, myEvents);
        rvEvents.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvEvents.setLayoutManager(llm);
        rvEvents.setAdapter(eventsAdapter);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(MyEventsActivity.this)
                .enableAutoManage(MyEventsActivity.this /* FragmentActivity */, MyEventsActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        if (!mSettings.contains("loaded")) {
            getResultsFromApi();
        } else {
            myEvents.clear();
            dbEvents = db.getAllEvents();
            myEvents.addAll(dbEvents);
            eventsAdapter.notifyDataSetChanged();
        }
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
            Toast.makeText(MyEventsActivity.this, getResources().getString(R.string.internet_disconnect), Toast.LENGTH_SHORT).show();
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
                    getResources().getString(R.string.google_account_permission),
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
                    Toast.makeText(MyEventsActivity.this, getResources().getString(R.string.google_play_unavailable), Toast.LENGTH_SHORT);
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
                MyEventsActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {
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
        protected Void doInBackground(Void... params) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);

            }
            return null;
        }

        private void getDataFromApi() throws IOException {

            // DateTime now = new DateTime(System.currentTimeMillis());
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
                eventStrings.add(event.getId());


                db.addEvent(myEvent);
            }


        }

        @Override
        protected void onPreExecute() {

            progressLoading.setVisibility(View.VISIBLE);
            fabUpdateEvents.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void output) {

            dbEvents = db.getAllEvents();
            myEvents.addAll(dbEvents);
            eventsAdapter.notifyDataSetChanged();
            if (myEvents.size() == 0) {
                Toast.makeText(MyEventsActivity.this, getResources().getString(R.string.no_events), Toast.LENGTH_SHORT).show();
            }
            if (!mSettings.contains("loaded")) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putBoolean("loaded", true);
                editor.apply();
            }
            progressLoading.setVisibility(View.GONE);
            fabUpdateEvents.setEnabled(true);

        }

        @Override
        protected void onCancelled() {
            progressLoading.setVisibility(View.GONE);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MyEventsActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(MyEventsActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MyEventsActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        try {
                            DatabaseHandler db = new DatabaseHandler(MyEventsActivity.this);
                            db.clearDatabase();
                            SharedPreferences mea = getSharedPreferences("Activities.MyEventsActivity", 0);
                            mea.edit().remove("").commit();
                            SharedPreferences gms = getSharedPreferences("com.google.android.gms.signin", 0);
                            gms.edit().clear().commit();
                            SharedPreferences gmsm = getSharedPreferences("com.google.android.gms.measurement.pref", 0);
                            gmsm.edit().clear().commit();
                            SharedPreferences ga = getSharedPreferences("googleAccount", 0);
                            ga.edit().clear().commit();


                            MyEventsActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_google_calendar, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                new AlertDialog.Builder(MyEventsActivity.this)
                        .setTitle(getResources().getString(R.string.account_signout_title))
                        .setMessage(getResources().getString(R.string.account_signout_content))
                        .setPositiveButton(getResources().getString(R.string.yes
                        ), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.ic_signout_red)
                        .show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
