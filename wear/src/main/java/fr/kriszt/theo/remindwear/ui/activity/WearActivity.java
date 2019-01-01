package fr.kriszt.theo.remindwear.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.WearDataService;
import fr.kriszt.theo.remindwear.sensors.SensorUtils;
import fr.kriszt.theo.remindwear.sensors.UnavailableSensorException;
import fr.kriszt.theo.remindwear.sensors.steps.StepListener;
import fr.kriszt.theo.remindwear.sensors.steps.StepListenerFactory;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.Coordinates;
import fr.kriszt.theo.shared.SportType;
import fr.kriszt.theo.shared.data.SportDataPoint;
import fr.kriszt.theo.shared.data.SportDataSet;


public class WearActivity extends WearableActivity
    implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    public static WearActivity lastInstance;
    private boolean hasGPS = false;
    private boolean hasPodometer = false;
    private boolean hasCardiometer = false;

    @BindView(R.id.stepTextValue)
    public TextView stepValue;

    @BindView(R.id.heartRateTextValue)
    public TextView heartRateValue;

    @BindView(R.id.timeTextValue)
     public TextView timeValue;

    @BindView(R.id.speedTextValue)
     public TextView speedValue;

    @BindView(R.id.distanceTextValue)
     public TextView distanceValue;

    @BindView(R.id.stepIcon)
    public ImageView stepIcon;

    @BindView(R.id.speedIcon)
    public ImageView speedIcon;

    @BindView(R.id.distanceIcon)
    public ImageView distanceIcon;

    @BindView(R.id.heartRateIcon)
    public ImageView heartRateIcon;

    @BindView(R.id.button)
    public Button button;


    private StepListener stepListener;

    // infos a afficher
    private int stepsCount = 0;
    private int heartRate = 0;
    private long startTime = System.currentTimeMillis();
    private Coordinates coordinates = null;
    private Coordinates lastCoordinates = null;
    private float currentSpeed = 0;
    private float totalDistance = 0;

    private static final String speedUnit = "km/h";
    private static final String distanceUnit = "km";
    private static final String heartRateUnit = "BPM";

    private static final String TAG = "wear_activity";

    private SensorManager sensorManager;

    // Constantes parametres GPS
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    private GoogleApiClient mGoogleApiClient;
    private LocationCallback locationCallback;


    // Thread parallele : met à jour l'affichage
    private Handler layoutUpdater;
    private static final long REFRESH_RATE = 500;
    Runnable timeStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateValues();
                updateDisplay();
            } finally {
                layoutUpdater.postDelayed(timeStatusChecker, REFRESH_RATE);
            }
        }
    };

    private SportType sportType = SportType.SPORT_WALK;
    private SportDataSet sportDataSet;
    private boolean GPShasFix = false;
    private Sensor  heartRateSensor;
    private SensorEventListener heartRateSensorListener;
    private String taskId = null;

    private void updateValues() {
        if (coordinates == null) {
            currentSpeed = 0;
        } else {
            currentSpeed = (float) coordinates.getSpeedkmh();
        }

        if (hasGPS/* && GPShasFix*/){
            if (lastCoordinates != null && coordinates != null && !coordinates.equals(lastCoordinates)){
                totalDistance += lastCoordinates.distanceTo(coordinates) / 1000; // en km
                lastCoordinates = null;
            }
        } else coordinates = null;

        if (hasPodometer){
            stepsCount = stepListener.getSteps();
        }

        SportDataPoint sportDataPoint = new SportDataPoint(coordinates, stepsCount, heartRate, totalDistance);
        sportDataSet.addPoint(sportDataPoint);


    }

    @SuppressLint("DefaultLocale")
    private void updateDisplay(){
        long timeDiff = (System.currentTimeMillis() - startTime) / 1000; // secondes
        long hours = timeDiff / 3600;
        long minutes = timeDiff % 3600 / 60;
        long seconds = timeDiff % 60;

        timeValue.setText(String.format("%02dh : %02dm : %02ds", hours, minutes, seconds));

        if (hasPodometer) {
            stepValue.setText(String.format("%d", stepsCount));
        }

        if (hasGPS){
            distanceValue.setText(String.format("%.2f %s", totalDistance, distanceUnit));
            speedValue.setText(String.format("%.2f %s", currentSpeed, speedUnit));
        }

        if (hasCardiometer){

            heartRateValue.setText(String.format("%d %s", heartRate, heartRateUnit));

        } else {
            heartRateValue.setText(String.format("??? %s", heartRateUnit));
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastInstance = this;


        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Integer tID = extras.getInt(Constants.KEY_TASK_ID);
            if ( tID != null){
                this.taskId = tID+"";
            }
        }


        getSportType();
        setContentView(R.layout.activity_wear);
        ButterKnife.bind(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createSensors();

        hideMissingSensors();
        hideUnusedSensors();

        sportDataSet = new SportDataSet(sportType, hasPodometer, hasGPS, hasCardiometer, taskId);

        Log.w(TAG, "onCreate: Creating dataset for taskId =" + taskId);
        Log.w(TAG, sportDataSet.toString());
        layoutUpdater = new Handler();
        timeStatusChecker.run(); // démarrer le màj du layout

        askForGPSPermission();
        setupGPSSensor();

//        boolean hasGPSPermission = SensorUtils.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (!hasGPSPermission){
//            Log.w(TAG, "onCreate: Asking for GPS access");
//            askForGPSPermission();
//        }
    }

    private void getSportType() {
        Intent createIntent = getIntent();
        Bundle extras = createIntent.getExtras();

        if (extras != null){
            String intendedSport = extras.getString(Constants.KEY_SPORT_TYPE);
            if (intendedSport != null){
                for (SportType st : SportType.values()){
                    if (st.getName().equals(intendedSport)){
                        sportType = st;
                        return;
                    }
                }
            }
        }
    }

    private void hideMissingSensors() {
        if (!hasGPS){
            speedValue.setVisibility(View.INVISIBLE);
            distanceValue.setVisibility(View.INVISIBLE);
            distanceIcon.setVisibility(View.INVISIBLE);
            speedIcon.setVisibility(View.INVISIBLE);
        }

        if (!hasPodometer){
            stepValue.setVisibility(View.INVISIBLE);
            stepIcon.setVisibility(View.INVISIBLE);


        }
        if (!hasCardiometer){
            heartRateValue.setVisibility(View.GONE);
            heartRateIcon.setVisibility(View.GONE);
        }
    }

    private void hideUnusedSensors(){
        switch (sportType){
            case SPORT_BIKE: // Vélo : Pas de compteur de pas
                stepValue.setVisibility(View.INVISIBLE);
                stepIcon.setVisibility(View.INVISIBLE);
                break;
            case SPORT_RUN: // Course : la totale
                break;
            case SPORT_WALK: // Marche : Compteur de pas uniquement, virer la vitesse et la distance
                speedValue.setVisibility(View.INVISIBLE);
                distanceValue.setVisibility(View.INVISIBLE);
                distanceIcon.setVisibility(View.INVISIBLE);
                speedIcon.setVisibility(View.INVISIBLE);
                break;
        }
    }


    /**
     * Arrête le tracking sportif
     * Envoie les données au Service Data Wear
     * Coupe les capteurs
     */
    public void stopTracking(View view){

        disableDisplayUpdate();
        disableSensors();

        // Envoyer l'info d'arrêt vers le telephone
        Intent stopIntent = new Intent(getApplicationContext(), WearDataService.class);
        stopIntent.setAction(Constants.ACTION_END_TRACK);
        stopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        String json = sportDataSet.toJson();
        stopIntent.putExtra(Constants.KEY_DATASET, json);
        stopIntent.putExtra(Constants.KEY_TASK_ID, taskId);

//        WearDataService.setObserver(this);
//        if (!getButtonText().startsWith("Sending")) {
//            setButton("Send", R.color.blue);
//        }

//        if (button.getText().equals("Send")){
//            setButton("Send", R.color.grey);
//        }else {
//            setButton("Send", R.color.green);
//        }


        startService(stopIntent);

    }

    public void setButton(String msg, int color) {
//        Log.w(TAG, "setButton: " + msg + "; " + getResources().getResourceEntryName(color));

        button.setText(msg);
        button.setBackgroundTintList(getResources().getColorStateList(color, null));
    }

    public String getButtonText(){
        return button.getText().toString();
    }

    private void createSensors() {
        // Essayer de trouver un moyen d'obtenir un capteur de pas
        sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        try {
            stepListener = StepListenerFactory.getStepListener(sensorManager);
            hasPodometer = true;
        } catch (UnavailableSensorException e) {
            hasPodometer = false;
        }

        hasGPS = getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        hasCardiometer = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE);

        if (hasCardiometer){
            registerCardiometer();
        }

    }



    public void askForGPSPermission() {

        boolean hasGPSPermission = SensorUtils.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasGPSPermission){
            Log.w(TAG, "GPS permission already granted");
            return;
        }
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.i(TAG, "onPermissionGranted: GPS access OK");
//                        mRequestingLocationUpdates = true;
//                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Toast.makeText(WearActivity.this, "Acces to GPS is denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "onConnected: ");

    }

    private void setupGPSSensor() {
        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location lastLocation = locationResult.getLastLocation();
                lastCoordinates = coordinates;
                coordinates = new Coordinates(lastLocation);
                Log.w(TAG, "onLocationResult: " + coordinates.getLat() + "; "+coordinates.getLng());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.w(TAG, "onLocationAvailability: " + locationAvailability);
                GPShasFix = locationAvailability.isLocationAvailable();
            }
        };

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void registerCardiometer() {
        this.heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        this.heartRateSensorListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.accuracy > SensorManager.SENSOR_STATUS_UNRELIABLE){
                    heartRate = (int) sensorEvent.values[0];
                }else heartRate = -1;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(this.heartRateSensorListener, this.heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Log.i(TAG, "onConnectionSuspended: ");
    }


    @Override
    public void onLocationChanged(Location location) {
//        Log.i(TAG, "onLocationChanged: " + location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.i(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Log.i(TAG, "onProviderEnabled: ");

    }

    @Override
    public void onProviderDisabled(String provider) {
//        Log.i(TAG, "onProviderDisabled: ");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.i(TAG, "onConnectionFailed: ");

    }

    /**
     * Arrête les capteurs
     */
    private void disableSensors(){
        stepListener.unregisterSensor(); // Capteur de pas

        sensorManager.unregisterListener(heartRateSensorListener);

        if (mGoogleApiClient.isConnected()) { // GPS
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        }
        mGoogleApiClient.disconnect();
    }

    private void disableDisplayUpdate(){
        layoutUpdater.removeCallbacks(timeStatusChecker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disableDisplayUpdate();
        disableSensors();
        lastInstance = null;
    }
}
