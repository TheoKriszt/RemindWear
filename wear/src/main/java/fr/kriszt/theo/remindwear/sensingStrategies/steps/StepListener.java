package fr.kriszt.theo.remindwear.sensingStrategies.steps;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public interface StepListener {

    int getSteps(); // Nombre de pas depuis le démarrage du listener

    void step(SensorEvent event);
    void step();

    void setSensorEventListener(SensorEventListener sel);

    void unregisterSensor();
}
