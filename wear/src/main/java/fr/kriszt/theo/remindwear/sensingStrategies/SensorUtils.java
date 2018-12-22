package fr.kriszt.theo.remindwear.sensingStrategies;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;

import java.util.List;

public class SensorUtils {

    private static List<Sensor> getAvailableSensors(SensorManager sm){
        return sm.getSensorList(Sensor.TYPE_ALL);
    }

    public static int[] getAvailableSensorsTypes(SensorManager sm){
        List<Sensor> sensors = getAvailableSensors(sm);
        int[] sensorTypes = new int[sensors.size()];

        for (int i = 0; i < sensors.size(); i++){
            sensorTypes[i] = sensors.get(i).getType();
        }

        return sensorTypes;

    }

    public static boolean checkPermissions(Context context, String permissionTag) {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                permissionTag);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

}
