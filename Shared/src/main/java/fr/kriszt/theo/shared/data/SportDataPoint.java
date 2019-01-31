package fr.kriszt.theo.shared.data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import fr.kriszt.theo.shared.Coordinates;

public class SportDataPoint implements Serializable {

    private final Calendar createdAt;
    Coordinates coords;
    public int steps;
    int heartRate;
    float distance;

    public SportDataPoint(Coordinates c, int stepsCount, int hRate, float dist) {
        coords = c;
        steps = stepsCount;
        heartRate = hRate;
        distance = dist;
        createdAt = new GregorianCalendar();
    }

    @NonNull
    @Override
    public String toString() {
        String res = "Data Point : " + steps + " steps; " + distance + "Km; " + heartRate + " BPM; Location: " + coords;
        res += " Calendar = " + createdAt;
        return res;
    }


    Calendar getCreatedAt() {
        return createdAt;
    }
}
