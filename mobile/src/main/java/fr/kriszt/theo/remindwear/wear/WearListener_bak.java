package fr.kriszt.theo.remindwear.wear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import fr.kriszt.theo.shared.Constants;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;

/**
 * Created by T.Kriszt on 07/10/2018.
 */
public class WearListener_bak extends WearableListenerService
implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DeleteDataItemsResult> {

    private static final String TAG = "WearListener_bak";
    private GoogleApiClient googleApiClient;

    private boolean pendingTask = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "onCreate: ");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.w(TAG, "DataEvent Received");
        for (DataEvent dataEvent : dataEvents) {
            Log.w(TAG, "Event : " + dataEvent.toString() + "PATH : " + dataEvent.getDataItem().getUri().getPath());

            if (dataEvent.getType() == DataEvent.TYPE_DELETED) {
                if (Constants.WEAR_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    // notification on the phone should be dismissed
                    NotificationManagerCompat.from(this).cancel(Constants.BOTH_ID);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand: ");
        if (null != intent) {
            String action = intent.getAction();
            if (Constants.ACTION_TRACK.equals(action)) {
                int taskId = intent.getIntExtra(Constants.KEY_TASK_ID, -1);

                Log.w(TAG, "onStartCommand: TASK TRACKING identified. Task ID is " + taskId);

//                if (taskId == Constants.BOTH_ID) {
//                    dismissWearableNotification(taskId);
//                }
//                emitMessage();
                if (googleApiClient.isConnected()){
                    Log.w(TAG, "onStartCommand: Emission immediate du message");
                    emitMessage();
                }else {
                    pendingTask = true;
                    googleApiClient.connect();
                }


            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Removes the DataItem that was used to create a notification on the watch. By deleting the
     * data item, a {@link com.google.android.gms.wearable.WearableListenerService} on the watch
     * will be notified and the notification on the watch will be removed. To
     * access the Wearable DataApi, we first need to ensure the GoogleApiClient is ready,
     * which will then run the onConnected callback were the data removal is
     * defined.
     * @deprecated
     * @param id The ID of the notification that should be removed
     */
    private void dismissWearableNotification(final int id) {
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.w(TAG, "onConnected: ");
//        final Uri dataItemUri =
//                new Uri.Builder().scheme(WEAR_URI_SCHEME).path(Constants.WEAR_PATH).build();
        if (pendingTask){
            Log.w(TAG, "onConnected: Emission du message à la reconnexion");
            emitMessage();
            pendingTask = false;
        }

//        Wearable.DataApi.deleteDataItems(
//                googleApiClient, dataItemUri).setResultCallback(this);
    }

    @Override // ConnectionCallbacks
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended: ");
    }

    @Override // OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect to the Google API client");
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Log.e(TAG, "onConnectionFailed: API unavailable");
        }

    }

    @Override // ResultCallback<DataApi.DeleteDataItemsResult>
    public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
        if (!deleteDataItemsResult.getStatus().isSuccess()) {
            Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
        }
        googleApiClient.disconnect();
    }

    /**
     * Builds a DataItem that on the wearable will be interpreted as a request to show a
     * notification. The result will be a notification that only shows up on the wearable.
     */
    private void emitMessage() {
        Log.w(TAG, "emitMessage: ");
        String content = "test_contenu";
        String title = "Ceci est un test";
        if (googleApiClient.isConnected()) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.WEAR_PATH);
            putDataMapRequest.getDataMap().putString(Constants.KEY_CONTENT, content);
            putDataMapRequest.getDataMap().putString(Constants.KEY_TITLE, title);
            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            request.setUrgent(); // sinon le système peut prendre sont temps (jusqu'à 30 minutes) pour remettrre le message <><>"
            PendingResult pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                Log.e(TAG, "buildWatchOnlyNotification(): Failed to set the data, "
                                        + "status: " + dataItemResult.getStatus().getStatusCode());
                            } else {
                                Log.w(TAG, "onResult: Callback de message reçu");
                            }
                        }
                    });
        } else { // end if connected
            Log.e(TAG, "emitMessage(): no Google API Client connection");
        }
    }

}
