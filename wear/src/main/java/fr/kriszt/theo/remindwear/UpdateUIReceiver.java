package fr.kriszt.theo.remindwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fr.kriszt.theo.shared.Constants;

public class UpdateUIReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdateUIReceiver";

    // https://medium.com/@anitaa_1990/how-to-update-an-activity-from-background-service-or-a-broadcastreceiver-6dabdb5cef74
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "onReceive: ");

        if (intent.getExtras() != null) {

                Intent in = new Intent(Constants.ACTION_UPDATE_UI);
                Bundle extras = new Bundle();
                extras.putString(Constants.KEY_TITLE, "some title");
                extras.putString(Constants.KEY_CONTENT, "some name");
                in.putExtras(extras);
                context.sendBroadcast(in);
        }
    }

}
