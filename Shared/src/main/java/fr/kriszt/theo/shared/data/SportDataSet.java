package fr.kriszt.theo.shared.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import fr.kriszt.theo.shared.Coordinates;
import fr.kriszt.theo.shared.SportType;

public class SportDataSet implements Serializable {

    private static final String TAG = SportDataSet.class.getSimpleName();
    private final boolean hasPodometer;
    private final boolean hasCardiometer;
    private final boolean hasGPS;
    private final SportType sportType;
    private Integer taskId = null;

    private final Calendar creationDate = new GregorianCalendar();

    private NavigableMap<Long, SportDataPoint> dataPoints = new TreeMap<>();

    private SportDataSet(SportType sportType, boolean hasPodometer, boolean hasGPS, boolean hasCardiometer) {
        this.sportType = sportType;
        this.hasPodometer = hasPodometer;
        this.hasCardiometer = hasCardiometer;
        this.hasGPS = hasGPS;
    }

    public SportDataSet(SportType sportType, boolean hasPodometer, boolean hasGPS, boolean hasCardiometer, String taskId) {
        this(sportType, hasPodometer, hasGPS, hasCardiometer);
        if (taskId != null) {
            this.taskId = Integer.parseInt(taskId);
        }
    }

    private Map<Long, SportDataPoint> getPoints() {
        return dataPoints;
    }

    public void addPoint(SportDataPoint data) {
        long time = new Date().getTime();
        dataPoints.put(time, data);
    }

    public int size() {
        return dataPoints.size();
    }


    @NonNull
    @Override
    public String toString() {
        String res = "SportDataSet created at " + creationDate.get(Calendar.HOUR_OF_DAY) + "h" + creationDate.get(Calendar.MINUTE) + "";
        res += taskId == null ? "" : " for task ID=" + taskId;
        res += " (" + sportType + ") : [" + size() + " dataPoints] podo=" + hasPodometer + ", cardio=" + hasCardiometer + ", GPS=" + hasGPS;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SportDataSet && ((SportDataSet) o).toJson().equals(this.toJson());
    }

    public static SportDataSet fromJson(String json) {
        return new Gson().fromJson(json, SportDataSet.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    private List<SportDataPoint> getDataPoints() {
        return new ArrayList<>(dataPoints.values());
    }

    private SportDataPoint lastPoint() {
        return dataPoints.lastEntry().getValue();
    }

    private SportDataPoint firstPoint() {
        return dataPoints.firstEntry().getValue();
    }


    public float avgHeartRate() {
        float value = 0;
        float count = 0;
        for (SportDataPoint dp : getDataPoints()) {
            value += dp.heartRate;
            count++;
        }
        return value / count;
    }

    public int stepsCount() {
        return lastPoint().steps;
    }

    public SportType getSportType() {
        return sportType;
    }

    public long getDuration() {
        long diff = lastPoint().getCreatedAt().getTimeInMillis() - firstPoint().getCreatedAt().getTimeInMillis();
        return diff / 1000;
    }

    public float getDistance() {
        return lastPoint().distance;
    }

    public List<Coordinates> getCoordinates() {
        List<Coordinates> coords = new ArrayList<>();
        for (SportDataPoint sportDataPoint : getDataPoints()) {
            coords.add(sportDataPoint.coords);
        }

        return coords;
    }


    public Integer getTaskId() {
        return taskId;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public boolean hasPodometer() {
        return hasPodometer;
    }

    public boolean hasCardiometer() {
        return hasCardiometer;
    }

    public boolean hasGPS() {
        return hasGPS;
    }
}
