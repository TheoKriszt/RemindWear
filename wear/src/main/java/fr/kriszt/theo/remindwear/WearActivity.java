package fr.kriszt.theo.remindwear;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import fr.kriszt.theo.remindwear.sensingStrategies.SensorUtils;
import fr.kriszt.theo.remindwear.sensingStrategies.UnavailableSensorException;
import fr.kriszt.theo.remindwear.sensingStrategies.steps.StepListener;
import fr.kriszt.theo.remindwear.sensingStrategies.steps.StepListenerFactory;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.Coordinates;

/**
 * Simple activité wearable
 * L'intent peut diriger vers certaines vues ou fragemnts
 *
 * Si ouvert depuis les applis mobiles :
 *     propose de commencer un exercice
 */
public class WearActivity extends WearableActivity
    implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private boolean hasGPS = false;
    private boolean hasPodometer = false;
    private boolean hasCardiometer = false;

    @BindView(R.id.stepTextValue)
    TextView stepValue;

    @BindView(R.id.heartRateTextValue)
    TextView heartRateValue;

    @BindView(R.id.timeTextValue)
     TextView timeValue;

    @BindView(R.id.speedTextValue)
     TextView speedValue;

    @BindView(R.id.distanceTextValue)
     TextView distanceValue;

    @BindView(R.id.stepIcon)
    ImageView stepIcon;

    @BindView(R.id.speedIcon)
    ImageView speedIcon;

    @BindView(R.id.distanceIcon)
    ImageView distanceIcon;

    @BindView(R.id.heartRateIcon)
    ImageView heartRateIcon;

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

    private static final String TAG = "wear_activity";

    private SensorManager sensorManager;

//    // bunch of location related apis
//    private FusedLocationProviderClient mFusedLocationClient;
//    private SettingsClient mSettingsClient;
//    private LocationRequest mLocationRequest;
//    private LocationSettingsRequest mLocationSettingsRequest;
//    private LocationCallback mLocationCallback;
//    private Location mCurrentLocation;
//    // boolean flag to toggle the ui
//    private Boolean mRequestingLocationUpdates;

    // Constantes parametres GPS
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10;
    private static final int REQUEST_CHECK_SETTINGS = 100;

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


    private void updateValues() {
//        currentSpeed = (float) (new Random().nextFloat()%10.0) * 10;
        if (coordinates == null) {
            currentSpeed = 0;
        } else {
            currentSpeed = (float) coordinates.getSpeedkmh();
        }

        if (hasGPS){
            if (lastCoordinates != null && coordinates != null){
                totalDistance += lastCoordinates.distanceTo(coordinates) / 1000; // en km
            }
        }

        if (hasPodometer){
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
        ButterKnife.bind(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createSensors();

        hideMissingSensors();

        layoutUpdater = new Handler();
        timeStatusChecker.run(); // démarrer le màj du layout

//        Todo : check permisisons
        boolean hasGPSPermission = SensorUtils.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (!hasGPSPermission){
            Log.w(TAG, "onCreate: Asking for GPS access");
            askForGPSPermission();
        }
//        else {
//            Log.i(TAG, "onCreate: GPS access granted");
//            startLocationUpdates();
//        }
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

        // Envoyer l'info d'arrêt vers le telephone
        Intent stopIntent = new Intent(this, WearDataService.class);
        stopIntent.setAction(Constants.ACTION_END_TRACK);
        this.startService(stopIntent);
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

    }

//    private void initGPS() {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mSettingsClient = LocationServices.getSettingsClient(this);
//
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                // location is received
//                mCurrentLocation = locationResult.getLastLocation();
//
//                Log.w(TAG, "onLocationResult: " + mCurrentLocation);
//                Log.w(TAG, "onLocationResult: GPS acquis ??");
////                updateLocationUI();
//            }
//        };
//
//        mRequestingLocationUpdates = true;
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        mLocationSettingsRequest = builder.build();
//    }


    public void askForGPSPermission() {
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

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
//    private void startLocationUpdates() {
//        Toast.makeText(this, "Start locating device", Toast.LENGTH_SHORT).show();
//        Log.i(TAG, "startLocationUpdates: ");
//        mSettingsClient
//                .checkLocationSettings(mLocationSettingsRequest)
//                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
//                    @SuppressLint("MissingPermission")
//                    @Override
//                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                        Log.i(TAG, "All location settings are satisfied.");
//
//                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
//
//                        //noinspection MissingPermission
//                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
//                                mLocationCallback, Looper.myLooper());
//
//
////                        updateLocationUI();
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "onFailure: ", e);
//                        int statusCode = ((ApiException) e).getStatusCode();
//                        switch (statusCode) {
//                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
//                                        "location settings ");
//                                try {
//                                    // Show the dialog by calling startResolutionForResult(), and check the
//                                    // result in onActivityResult().
//                                    ResolvableApiException rae = (ResolvableApiException) e;
//                                    rae.startResolutionForResult(WearActivity.this, REQUEST_CHECK_SETTINGS);
//                                } catch (IntentSender.SendIntentException sie) {
//                                    Log.i(TAG, "PendingIntent unable to execute request.");
//                                }
//                                break;
//                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                String errorMessage = "Location settings are inadequate, and cannot be " +
//                                        "fixed here. Fix in Settings.";
//                                Log.e(TAG, errorMessage);
//
//                                Toast.makeText(WearActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//
//                        }
//
////                        updateLocationUI();
//                    }
//                });
//    }

//    public void stopLocationUpdates() {
//        // Removing location updates
//        mFusedLocationClient
//                .removeLocationUpdates(mLocationCallback)
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
////                        toggleButtons();
//                    }
//                });
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            // Check for the integer request code originally supplied to startResolutionForResult().
//            case REQUEST_CHECK_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        Log.e(TAG, "User agreed to make required location settings changes.");
//                        // Nothing to do. startLocationupdates() gets called in onResume again.
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Log.e(TAG, "User chose not to make required location settings changes.");
//                        mRequestingLocationUpdates = false;
//                        break;
//                }
//                break;
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
//        if (mRequestingLocationUpdates && SensorUtils.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            startLocationUpdates();
//        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location lastLocation = locationResult.getLastLocation();
                lastCoordinates = coordinates;
                coordinates = new Coordinates(lastLocation);
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.w(TAG, "onLocationAvailability: " + locationAvailability);
            }
        };

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended: ");
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: " + location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled: ");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: ");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: ");

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        layoutUpdater.removeCallbacks(timeStatusChecker);
        // TODO :  unregister listeners
        stepListener.unregisterSensor(); // Capteur de pas
//        stopLocationUpdates(); // Position GPS
        if (mGoogleApiClient.isConnected()) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        }
        mGoogleApiClient.disconnect();
    }
}
