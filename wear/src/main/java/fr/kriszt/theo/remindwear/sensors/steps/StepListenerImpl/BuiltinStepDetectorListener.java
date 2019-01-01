package fr.kriszt.theo.remindwear.sensors.steps.StepListenerImpl;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class BuiltinStepDetectorListener extends AbstractStepListener {

    public BuiltinStepDetectorListener(SensorManager sm) {
        super(sm);
    }

    @Override
    public void step(SensorEvent event) {
        count++;
    }

}
