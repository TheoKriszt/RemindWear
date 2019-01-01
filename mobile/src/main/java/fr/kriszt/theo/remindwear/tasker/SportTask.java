package fr.kriszt.theo.remindwear.tasker;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.kriszt.theo.shared.Coordinates;
import fr.kriszt.theo.shared.data.SportDataSet;

public class SportTask implements Serializable{
    private static final String TAG = SportTask.class.getSimpleName();

//    private ArrayList<Coordinates> listCoord = new ArrayList<>();
//    private int steps;
//    private int heart;
//    private int distance;
//    private long duration;

    private SportDataSet sportDataSet;
    private Integer taskId = null;
    private Task referer = null;

//    public SportTask(SportDataSet d){
//        this.sportDataSet = d;
//    }

//    public SportTask(SportDataSet d, Integer tId){
//        this(d);
//        taskId = tId;
//    }

    public SportTask(Task referer) {
        this.referer = referer;
        this.taskId = referer.getID();
    }

    public static void bindTasks(List<SportTask> taskSportList, Context context) {
//        Log.w(TAG, "bindTasks: ");
        Tasker tasker = Tasker.getInstance(context);

        for (SportTask st : taskSportList){
            if (st.taskId != null) {
                Task referer = tasker.getTaskByID(st.taskId);
                st.setTask(referer);
            }

        }
    }

    public List<Coordinates> getListCoord() {
        if (sportDataSet != null) {
            return sportDataSet.getCoordinates();
        }else return new ArrayList<>();
    }


    public Calendar getFirstDate(){
        if (sportDataSet != null) {
            return sportDataSet.getCreationDate();
        }else return null;
    }
    public int getSteps() {return sportDataSet.stepsCount();}
//    public void setSteps(int steps) {this.steps = steps;}

    public float getHeart() {return sportDataSet.avgHeartRate();}
//    public void setHeart(int heart) {this.heart = heart;}

    public float getDistance() {return sportDataSet.getDistance();}
//    public void setDistance(int distance) {this.distance = distance;}

    public long getDuration() {return sportDataSet.getDuration();}

    public Integer getID() {
        if (referer != null){
            return referer.getID();
        }
        return taskId;//sportDataSet.getTaskId();
    }

    public Category getCategory() {
        if (this.referer != null){
            return referer.getCategory();
        }else return null;
    }

    public String getName() {
        if (this.referer != null){
            return referer.getName();
        }else return null;

    }

    public String getDescription() {
        if (this.referer != null){
            return referer.getDescription();
        }else return null;

    }

    public void setTask(Task referer) {
        this.referer = referer;
    }

    public void setDataset(SportDataSet sportDataSet) {
        this.sportDataSet = sportDataSet;
    }

    public SportDataSet getDataset() {
        return  sportDataSet;
    }


    @Override
    public String toString(){
        String res = "SportTask, ID="+taskId+", DataSet="+sportDataSet;
        return res;
    }

//    public void setDurationSecondes(long duration) {
//        this.duration = duration;
//    }

}
