package fr.kriszt.theo.remindwear.sensingStrategies.steps.StepListenerImpl;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import fr.kriszt.theo.remindwear.sensingStrategies.steps.StepListener;

public abstract class AbstractStepListener implements StepListener {

    protected int count = 0;
    protected SensorEventListener sensorEventListener = null;
    protected SensorManager sensorManager;

    public AbstractStepListener(SensorManager sm){
        sensorManager = sm;
    }

    @Override
    public int getSteps() {
        return count;
    }

    @Override
    public void step() {
        count++;
    }

    @Override
    public void setSensorEventListener(SensorEventListener sel) {
        sensorEventListener = sel;
    }

    @Override
    public void unregisterSensor() {
        if (sensorEventListener != null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}
