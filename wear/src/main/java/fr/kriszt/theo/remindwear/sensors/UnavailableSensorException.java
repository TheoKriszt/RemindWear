package fr.kriszt.theo.remindwear.sensors;

public class UnavailableSensorException extends Exception {

    public UnavailableSensorException(String sensorType){
        super("Impossible de trouver un capteur pour " + sensorType);
    }
}
