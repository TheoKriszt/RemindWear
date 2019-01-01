package fr.kriszt.theo.remindwear.sensors.steps.StepListenerImpl;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import fr.kriszt.theo.remindwear.sensors.steps.StepDetector;

public class AccelerationStepListener extends AbstractStepListener {

    private final StepDetector stepDetector;

    public AccelerationStepListener(SensorManager sm) {
        super(sm);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
    }

    @Override
    public void step(SensorEvent event) {
        stepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
    }
}
