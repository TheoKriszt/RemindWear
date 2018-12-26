package fr.kriszt.theo.shared.data;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.io.Serializable;

import fr.kriszt.theo.shared.SportType;

public class DataSet implements Serializable {

    private static final String TAG = DataSet.class.getSimpleName();
    private final boolean hasPodometer;
    private final boolean hasCardiometer;
    private final boolean hasGPS;
    private final SportType sportType;
    private Integer taskId = null;

    private HashMap<Long, DataPoint> dataPoints = new HashMap<>();

    public DataSet(SportType sportType, boolean hasPodometer, boolean hasGPS, boolean hasCardiometer){
        this.sportType = sportType;
        this.hasPodometer = hasPodometer;
        this.hasCardiometer = hasCardiometer;
        this.hasGPS = hasGPS;
    }

    public DataSet(SportType sportType, boolean hasPodometer, boolean hasGPS, boolean hasCardiometer, String taskId) {
        this(sportType, hasPodometer, hasGPS, hasCardiometer);
        if (taskId != null){
            this.taskId = Integer.parseInt(taskId);
        }else Log.w(TAG, "DataSet: impossible de parser un ID de tâche nulle !");
    }

    public HashMap<Long, DataPoint> getPoints(){
        return dataPoints;
    }

    public void addPoint( long time, DataPoint data ){
        dataPoints.put(time, data);
    }

    public void addPoint(DataPoint data ){
        long time = new Date().getTime();
        dataPoints.put(time, data);
    }

    public int size(){
        return dataPoints.size();
    }

    public String toJson(){
        return new Gson().toJson(this);

    }

    @Override
    public String toString(){
        String res = "DataSet";
        res+= taskId == null ? "" : " for task ID="+taskId;

        res += " ("+ sportType+") : [" + size() + " dataPoints] podo="+hasPodometer+", cardio="+hasCardiometer+", GPS="+hasGPS;


        return res;
    }

    public static DataSet fromJson(String json){
        return new Gson().fromJson(json, DataSet.class);
    }



}
