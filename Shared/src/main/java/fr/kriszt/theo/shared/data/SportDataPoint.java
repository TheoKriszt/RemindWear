package fr.kriszt.theo.shared.data;

import fr.kriszt.theo.shared.Coordinates;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SportDataPoint implements Serializable{

    private final Calendar createdAt;
    public Coordinates coords;
    public int steps;
    public int heartRate;
    public float distance;

    public SportDataPoint(Coordinates c, int stepsCount, int hRate, float dist){
        coords = c;
        steps = stepsCount;
        heartRate = hRate;
        distance = dist;
        createdAt = new GregorianCalendar();
    }

    @Override
    public String toString(){
//        String res = "Data Point : created at " + createdAt.get(Calendar.HOUR_OF_DAY) + ":" + createdAt.get(Calendar.MINUTE) + ", " + steps + " steps; " + distance + "Km; " + heartRate + " BPM; Location: " + coords;
        String res = "Data Point : " + steps + " steps; " + distance + "Km; " + heartRate + " BPM; Location: " + coords;
        res += " Calendar = " + createdAt;
        return res;
    }


    public Calendar getCreatedAt() {
        return createdAt;
    }
}
