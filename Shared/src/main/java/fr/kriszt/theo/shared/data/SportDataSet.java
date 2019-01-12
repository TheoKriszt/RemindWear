package fr.kriszt.theo.shared.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
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

    public SportDataSet(SportType sportType, boolean hasPodometer, boolean hasGPS, boolean hasCardiometer){
        this.sportType = sportType;
        this.hasPodometer = hasPodometer;
        this.hasCardiometer = hasCardiometer;
        this.hasGPS = hasGPS;
    }

    public SportDataSet(SportType sportType, boolean hasPodometer, boolean hasGPS, boolean hasCardiometer, String taskId) {
        this(sportType, hasPodometer, hasGPS, hasCardiometer);
        if (taskId != null){
            this.taskId = Integer.parseInt(taskId);
        }else {
            Log.w(TAG, "SportDataSet: impossible de parser un ID de tâche nulle !");
        }
    }

    public Map<Long, SportDataPoint> getPoints(){
        return dataPoints;
    }

    public void addPoint(SportDataPoint data ){
        long time = new Date().getTime();
        dataPoints.put(time, data);
    }

    public int size(){
        return dataPoints.size();
    }



    @NonNull
    @Override
    public String toString(){
        String res = "SportDataSet created at " + creationDate.get(Calendar.HOUR_OF_DAY)+"h"+creationDate.get(Calendar.MINUTE) + "";
        res+= taskId == null ? "" : " for task ID="+taskId;
        res += " ("+ sportType+") : [" + size() + " dataPoints] podo="+hasPodometer+", cardio="+hasCardiometer+", GPS="+hasGPS;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SportDataSet && ((SportDataSet) o).toJson().equals(this.toJson());
    }

    public static SportDataSet fromJson(String json){
        return new Gson().fromJson(json, SportDataSet.class);
    }
    public String toJson(){
        return new Gson().toJson(this);
    }

    private List<SportDataPoint> getDataPoints(){
        ArrayList<SportDataPoint> sportDataPointArrayList = new ArrayList<>();
        sportDataPointArrayList.addAll(dataPoints.values());
        return sportDataPointArrayList;
    }

    private SportDataPoint lastPoint(){
        return dataPoints.lastEntry().getValue();
    }

    private SportDataPoint firstPoint(){
        return dataPoints.firstEntry().getValue();
    }

    public int minHeartRate(){
        int value = 255;
        for (SportDataPoint dp : getDataPoints()){
            value = Math.min(value, dp.heartRate);
        }
        return value;
    }

    public int maxHeartRate(){
        int value = 0;
        for (SportDataPoint dp : getDataPoints()){
            value = Math.max(value, dp.heartRate);
        }
        return value;
    }

    public float avgHeartRate(){
        float value = 0;
        float count = 0;
        for (SportDataPoint dp : getDataPoints()){
            value += dp.heartRate;
            count++;
        }
        return value / count;
    }

    public int stepsCount(){
        return lastPoint().steps;
    }

    public SportType getSportType(){
        return sportType;
    }

    public float getAvgSpeed(){
        return lastPoint().distance / size();
    }

    public float maxAvgSpeed(){
        float max = 0f;
        for (SportDataPoint dp : getDataPoints()){
            max = (float) Math.max(max, dp.coords.getSpeedkmh());
        }
        return lastPoint().distance / size();
    }

    public long getDuration(){
        long first = 0, last = 0;
        List<Coordinates> coords = getCoordinates();
        for (int i = 0; i < coords.size(); i++){
            if (coords.get(i) != null){
                first = coords.get(i).getTimestamp();
                break;
            }
        }

        for (int i = coords.size()-1 ; i >= 0; i++){
            try {
                if (coords.get(i) != null){
                    last = coords.get(i).getTimestamp();
                    break;
                }
            } catch (IndexOutOfBoundsException ignored){}

        }


        return (last - first) / 1000;
//        return lastPoint().coords.getTimestamp() - firstPoint().coords.getTimestamp();
    }

    public float getDistance(){
        return lastPoint().distance;
    }

    public List<Coordinates> getCoordinates(){
        List<Coordinates> coords = new ArrayList<>();
        for (SportDataPoint sportDataPoint : getDataPoints()){
            coords.add(sportDataPoint.coords);
        }

        return coords;
    }


    public Integer getTaskId() {
        return taskId;
    }

//    public Calendar getCreationDate(){
//        GregorianCalendar gc = new GregorianCalendar();
//        Coordinates coords = firstPoint().coords;
//        Log.w(TAG, "getCreationDate: firstPoint" + firstPoint());
//        Log.w(TAG, "getCreationDate: firstPoint coords" + firstPoint().coords);
//        long timestamp = coords.getTimestamp();
//
//        gc.setTime(new Date(coords.getTimestamp()));
//        return gc;
//    }

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
