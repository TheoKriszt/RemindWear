package fr.kriszt.theo.remindwear.sensors.steps.StepListenerImpl;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class BuiltinStepCounterListener extends AbstractStepListener {

    private Integer initialCount = null;

    public BuiltinStepCounterListener(SensorManager sm) {
        super(sm);
    }

    @Override
    public int getSteps() {
        return count - initialCount;
    }

    @Override
    public void step(SensorEvent event) {

        int totalSteps = (int) event.values[0];
        if (initialCount == null) {
            initialCount = totalSteps;
        }

        count = totalSteps;
    }
}
