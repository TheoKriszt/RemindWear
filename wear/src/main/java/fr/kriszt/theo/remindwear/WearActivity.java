package fr.kriszt.theo.remindwear;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.wearable.Wearable;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import fr.kriszt.theo.remindwear.sensingStrategies.UnavailableSensorException;
import fr.kriszt.theo.remindwear.sensingStrategies.steps.StepListener;
import fr.kriszt.theo.remindwear.sensingStrategies.steps.StepListenerFactory;
import fr.kriszt.theo.shared.Constants;

/**
 * Simple activité wearable
 * L'intent peut diriger vers certaines vues ou fragemnts
 *
 * Si ouvert depuis les applis mobiles :
 *     propose de commencer un exercice
 */
public class WearActivity extends WearableActivity {

    private boolean hasGPS = false;
    private boolean hasPedometer = false;
    private boolean hasCardiometer = false;

    private TextView stepLabel;
    private TextView speedLabel;
    private TextView distanceLabel;

    private TextView stepValue;
    private TextView timeValue;
    private TextView speedValue;
    private TextView distanceValue;

    private StepListener stepListener;

    public void setStepsCount(int stepsCount) {
        this.stepsCount = stepsCount;
    }

    private int stepsCount = 0;
    private long startTime = System.currentTimeMillis();
    private float currentSpeed = 0;
    private float totalDistance = 0;

    private static final String speedUnit = "km/h";
    private static final String distanceUnit = "km";

    private static final String TAG = "wear_activity";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    private SensorManager sensorManager;

    private Handler layoutUpdater;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // Thread parallele : met à jour l'affichage
    Runnable timeStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateValues();
                updateDisplay();
            } finally {
                layoutUpdater.postDelayed(timeStatusChecker, 500);
            }
        }
    };


    private void updateValues() {

        // TODO : remove tests

        totalDistance += 0.01;
        currentSpeed = (float) (new Random().nextFloat()%10.0) * 10;

        if (hasPedometer){
            stepsCount = stepListener.getSteps();
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateDisplay(){
        distanceValue.setText(String.format("%.2f %s", totalDistance, distanceUnit));
        speedValue.setText(String.format("%.2f %s", currentSpeed, speedUnit));
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000; // secondes
        long hours = timeDiff / 3600;
        long minutes = timeDiff % 3600 / 60;
        long seconds = timeDiff % 60;

        timeValue.setText(String.format("%02dh : %02dm : %02ds", hours, minutes, seconds));
        stepValue.setText(String.format("%d", stepsCount));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wear);

        stepLabel = findViewById(R.id.stepTextLabel);
//        timeLabel = findViewById(R.id.timeTextLabel);
        speedLabel = findViewById(R.id.speedTextLabel);
        distanceLabel = findViewById(R.id.distanceTextLabel);

        stepValue = findViewById(R.id.stepTextValue);
        timeValue = findViewById(R.id.timeTextValue);
        speedValue = findViewById(R.id.speedTextValue);
        distanceValue = findViewById(R.id.distanceTextValue);

        createSensors();

        // TODO : check capabilities
        layoutUpdater = new Handler();
        timeStatusChecker.run(); // démarrer le màj du layout
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
////        if (resultCode == Activity.RESULT_OK) {
////            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
////                Log.w(TAG, "onActivityResult: requestCode OK");
////                subscribe();
////            }else Log.w(TAG, "onActivityResult: Received code " + requestCode);
////        }
//    }



    public void stopTracking(View view){
        Log.w(TAG, "stopTracking: ");
        Intent stopIntent = new Intent(this, WearDataService.class);
        stopIntent.setAction(Constants.ACTION_END_TRACK);
        this.startService(stopIntent);
    }


    private void createSensors() {
        // Essayer de trouver un moyen d'obtenir un capteur de pas
        sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        try {
            stepListener = StepListenerFactory.getStepListener(sensorManager);
            hasPedometer = true;
        } catch (UnavailableSensorException e) {
            hasPedometer = false;
        }

        hasGPS = getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        hasCardiometer = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE);

        Log.w(TAG, "createSensors: Has a GPS ? " + hasGPS);

        if (hasGPS){
            initGPS();
        }

    }

    private void initGPS() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();

                Log.w(TAG, "onLocationResult: " + mCurrentLocation);
//                updateLocationUI();
            }
        };
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        layoutUpdater.removeCallbacks(timeStatusChecker);
        // TODO :  unregister listeners
        stepListener.unregisterSensor();
    }
}
