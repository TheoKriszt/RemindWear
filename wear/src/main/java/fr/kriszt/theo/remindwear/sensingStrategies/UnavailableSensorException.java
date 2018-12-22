package fr.kriszt.theo.remindwear.sensingStrategies;

public class UnavailableSensorException extends Exception {

    public UnavailableSensorException(String sensorType){
        super("Impossible de trouver un capteur pour " + sensorType);
    }
}
