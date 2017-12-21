package es.academy.solidgear.surveyx.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import es.academy.solidgear.surveyx.R;
import es.academy.solidgear.surveyx.managers.NetworkManager;
import es.academy.solidgear.surveyx.managers.SharedPrefsManager;
import es.academy.solidgear.surveyx.model.SurveyModel;
import es.academy.solidgear.surveyx.model.SurveysModel;
import es.academy.solidgear.surveyx.services.requests.GetAllSurveysRequest;
import es.academy.solidgear.surveyx.ui.adapter.SurveyListAdapter;
import es.academy.solidgear.surveyx.ui.fragments.ErrorDialogFragment;
import es.academy.solidgear.surveyx.ui.fragments.InformationDialogFragment;

public class SurveyListActivity extends BaseActivity {

    private static final String TAG = "SurveyListActivity";
    public static final String EXTRA_TOKEN = "token";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    private RecyclerView mQuestionnaireList;
    private InformationDialogFragment mDialog;
    private AlertDialog mGpsEnabledDialog;

    private SurveysModel mRequestResponse = null;

    private Boolean mGpsEnabled;
    private Boolean mAskedEnableGps = false;
    private Boolean mRequestedRuntimePermissions = false;

    private boolean mSurveyListAlreadyShown = false;

    private String mToken;

    private FusedLocationProviderClient mFusedLocationClient = null;
    protected Location mLastLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);
        initToolbar();

        Bundle extras = getIntent().getExtras();
        mToken = extras.getString(EXTRA_TOKEN, null);

        mQuestionnaireList = (RecyclerView) findViewById(R.id.questionnaireList);

        mQuestionnaireList.setLayoutManager(new LinearLayoutManager(this));
        mQuestionnaireList.setAdapter(new SurveyListAdapter(new ArrayList<SurveyModel>(), this));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGpsEnabled = checkGpsEnabled();
        mAskedEnableGps = SharedPrefsManager.getInstance(this).getBoolean("AskedEnableGps");
        mLastLocation = null;

        if (!mGpsEnabled && !mAskedEnableGps) {
            showEnableGpsDialog();
        } else {
            boolean requestingRuntimePermissions = false;

            if (mGpsEnabled && mFusedLocationClient == null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!hasLocationPermission() && !mRequestedRuntimePermissions) {
                        requestingRuntimePermissions = true;
                        requestLocationPermission();
                    }
                }
                // If the location permission has been requested the set-up will be done in the
                // async callback for the permission, otherwise it is done here.
                if (hasLocationPermission() && !requestingRuntimePermissions) {
                    setUpLocationSettings();
                }

            }

            // If the location permission has been requested the initial fetcg will be done in the
            // async callback for the permission, otherwise it is done here.
            if (!requestingRuntimePermissions) {
                triggerFetchingSurveys();
            }

        }
    }

    private void triggerFetchingSurveys() {
        if (mDialog == null) {
            mDialog = InformationDialogFragment.newInstance(R.string.dialog_getting_surveys);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            mDialog.show(fragmentManager, "dialog");
        } else {
            mDialog.onResume();
        }
        fetchAllSurveys();
    }

    @Override
    protected void onPause() {
        mRequestResponse = null;
        mSurveyListAlreadyShown = false;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            doLogout();
        } else if (id == R.id.action_refresh) {
            doRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void setUpLocationSettings() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (mDialog != null) {
            mDialog.setMessage(getString(R.string.dialog_waiting_location));
        }
        mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mLastLocation = location;
                                triggerFetchingSurveys();
                            }
                        }
                    });
    }

    private void doLogout() {
        //mAuthManager.setAuthCredentials("", null);
        SharedPrefsManager.getInstance(this).remove("AskedEnableGps");
        SharedPrefsManager.getInstance(this).remove("rememberMeToken");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void doRefresh() {
        mSurveyListAlreadyShown = false;
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mDialog.show(fragmentManager, "dialog");
        fetchAllSurveys();
    }

    private void fetchAllSurveys() {
        Response.Listener<SurveysModel> onGetAllSurveys = new Response.Listener<SurveysModel>() {
            @Override
            public void onResponse(SurveysModel response) {
                mRequestResponse = response;
                handleResponse();
            }
        };

        Response.ErrorListener onGetAllSurveysFail = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();

                ErrorDialogFragment.OnClickClose onClickClose = new ErrorDialogFragment.OnClickClose() {
                    @Override
                    public void onClickClose() {

                    }
                };
                showGenericError(error.toString(), onClickClose);
            }
        };

        GetAllSurveysRequest request = new GetAllSurveysRequest(mToken,
                                                                onGetAllSurveys,
                                                                onGetAllSurveysFail);
        NetworkManager.getInstance(this).makeRequest(request);
    }

    private void handleResponse() {
        if (mRequestResponse != null && mLastLocation != null && !mSurveyListAlreadyShown) {
            showSurveys(mRequestResponse.getSurveys());
            mSurveyListAlreadyShown = true;
            return;
        }

        if (mRequestResponse == null) {
            mDialog.setMessage(getString(R.string.dialog_waiting_data));
        }

        if (mLastLocation == null) {
            showSurveys(mRequestResponse.getSurveys());
        }
    }

    private void showSurveys(List<SurveyModel> surveyModelList) {
        mDialog.dismiss();

        for (int i=0; i<surveyModelList.size(); i++) {
            SurveyModel currentSurvey = surveyModelList.get(i);
            if (currentSurvey.getAlreadyDone()) {
            /* Remove done survey */
                surveyModelList.remove(i);
                i--;
                continue;
            }
            if ((!mGpsEnabled) ||
                 (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ! hasLocationPermission())) {
                /* Remove located survey */
                if (surveyModelList.get(i).hasCoordinates()){
                    surveyModelList.remove(i);
                    i--;
                }
            }
        }

        mQuestionnaireList.setAdapter(new SurveyListAdapter(surveyModelList, this));

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mQuestionnaireList.startAnimation(fadeInAnimation);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mQuestionnaireList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public boolean checkGpsEnabled() {
        LocationManager mlocManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showEnableGpsDialog() {
        final SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance(this);
        if (mGpsEnabledDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_gps_title);
            builder.setMessage(R.string.dialog_gps_message);
            builder.setPositiveButton(R.string.dialog_gps_settings, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    sharedPrefsManager.putBoolean("AskedEnableGps", true);

                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.dialog_gps_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    sharedPrefsManager.putBoolean("AskedEnableGps", true);
                    enableGpsRejected();
                    dialog.dismiss();
                }
            });
            mGpsEnabledDialog = builder.create();
        }
        mGpsEnabledDialog.show();
    }

    private void enableGpsRejected() {
        this.onResume();
    }

    public void showSurveyPage(SurveyModel survey) {
        Intent intent = new Intent(this, SurveyActivity.class);
        intent.putExtra(SurveyActivity.EXTRA_TOKEN, mToken);
        intent.putExtra(SurveyActivity.SURVEY_ID, survey.getId());
        startActivity(intent);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                                          new String[]{
                                                  Manifest.permission.ACCESS_FINE_LOCATION
                                          },
                                          MY_PERMISSIONS_REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                mRequestedRuntimePermissions = true;
                if (hasLocationPermission()) {
                    setUpLocationSettings();
                } else {
                    triggerFetchingSurveys();
                }
            }
        }
    }
}
