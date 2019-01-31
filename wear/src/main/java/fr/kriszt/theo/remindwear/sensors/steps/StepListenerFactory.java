package fr.kriszt.theo.remindwear.sensors.steps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import fr.kriszt.theo.remindwear.sensors.SensorUtils;
import fr.kriszt.theo.remindwear.sensors.UnavailableSensorException;
import fr.kriszt.theo.remindwear.sensors.steps.StepListenerImpl.AccelerationStepListener;
import fr.kriszt.theo.remindwear.sensors.steps.StepListenerImpl.BuiltinStepCounterListener;
import fr.kriszt.theo.remindwear.sensors.steps.StepListenerImpl.BuiltinStepDetectorListener;

public class StepListenerFactory {

    // Types de capteurs utilisables en pédomètre, par ordre de priorité
    private static int[] stepSensorTypes = new int[]{
            Sensor.TYPE_STEP_DETECTOR,
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_LINEAR_ACCELERATION
    };

    public static StepListener getStepListener(SensorManager sensorManager) throws UnavailableSensorException {

        Sensor stepSensor = null;

        int[] availableSensors = SensorUtils.getAvailableSensorsTypes(sensorManager);

        for (int sensorType : stepSensorTypes){
            for (int availableSensor : availableSensors){
                if (sensorType == availableSensor){
                    stepSensor = sensorManager.getDefaultSensor(sensorType);
                }
            }
        }

        if (stepSensor == null){
            throw new UnavailableSensorException("STEP SENSOR");
        }

        final StepListener stepListener = initSensor(stepSensor, sensorManager);

        SensorEventListener sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                stepListener.step(event);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(sel, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        stepListener.setSensorEventListener(sel);
        return stepListener;
    }

    private static StepListener initSensor(Sensor sensor, SensorManager sensorManager) {
        StepListener sl = null;
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            sl = new AccelerationStepListener(sensorManager);
        } else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            sl = new BuiltinStepCounterListener(sensorManager);

        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            return new BuiltinStepDetectorListener(sensorManager);
        }

        return sl;
    }

}
