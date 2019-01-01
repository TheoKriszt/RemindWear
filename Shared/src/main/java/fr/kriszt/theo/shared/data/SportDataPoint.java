package fr.kriszt.theo.shared.data;

import fr.kriszt.theo.shared.Coordinates;
import java.io.Serializable;

public class SportDataPoint implements Serializable{

    public Coordinates coords;
    public int steps;
    public int heartRate;
    public float distance;

    public SportDataPoint(Coordinates c, int stepsCount, int hRate, float dist){
        coords = c;
        steps = stepsCount;
        heartRate = hRate;
        distance = dist;
    }

    @Override
    public String toString(){
        String res = "Data Point : " + steps + " steps; " + distance + "Km; " + heartRate + " BPM; Location: " + coords;
        return res;
    }



}
