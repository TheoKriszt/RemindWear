package fr.kriszt.theo.shared.data;

import fr.kriszt.theo.shared.Coordinates;
import java.io.Serializable;

public class DataPoint implements Serializable{

    public Coordinates coords;
    public int steps;
    public int heartRate;
    public float distance;

    public DataPoint(Coordinates c, int stepsCount, int hRate, float dist){
        coords = c;
        steps = stepsCount;
        heartRate = hRate;
        distance = dist;
    }



}
