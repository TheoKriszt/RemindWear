package fr.kriszt.theo.remindwear.tasker;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.kriszt.theo.shared.Coordinates;
import fr.kriszt.theo.shared.data.SportDataSet;

public class SportTask implements Serializable {

    private SportDataSet sportDataSet;
    private Integer taskId = null;
    private Task referer;

    public SportTask(Task referer) {
        this.referer = referer;
        if (referer != null) {
            this.taskId = referer.getID();
        }
    }

    /**
     * Lie les tâches de sport à des tâches classiques par comparaison
     * @param taskSportList la liste de SPortTask à binder
     * @param context pour retrouver le Tasker
     */
    public static void bindTasks(List<SportTask> taskSportList, Context context) {
        Tasker tasker = Tasker.getInstance(context);

        for (SportTask st : taskSportList) {
            if (st.taskId != null) {
                Task referer = tasker.getTaskByID(st.taskId);
                st.setTask(referer);
            }

        }
    }

    public List<Coordinates> getListCoord() {
        if (sportDataSet != null) {
            return sportDataSet.getCoordinates();
        } else return new ArrayList<>();
    }


    /**
     *
     * @return la date de creation du jeu de données du tracking sportif
     */
    public Calendar getFirstDate() {
        if (sportDataSet != null) {
            return sportDataSet.getCreationDate();
        } else return null;
    }

    public int getSteps() {
        return sportDataSet.stepsCount();
    }

    /**
     * @return le rythme cardiaque moyen de l'activité sportive
     */
    public float getHeart() {
        return sportDataSet.avgHeartRate();
    }

    public float getDistance() {
        return sportDataSet.getDistance();
    }

    public long getDuration() {
        return sportDataSet.getDuration();
    }

    /**
     * @return Integer car id peut être null
     */
    public Integer getID() {
        if (referer != null) {
            return referer.getID();
        }
        return taskId;
    }

    public Category getCategory() {
        if (this.referer != null) {
            return referer.getCategory();
        } else return null;
    }

    public String getName() {
        if (this.referer != null) {
            return referer.getName();
        } else return null;

    }

    public String getDescription() {
        if (this.referer != null) {
            return referer.getDescription();
        } else return null;

    }

    public void setTask(Task referer) {
        this.referer = referer;
    }

    public void setDataset(SportDataSet sportDataSet) {
        this.sportDataSet = sportDataSet;
    }

    public SportDataSet getDataset() {
        return sportDataSet;
    }


    @NonNull
    @Override
    public String toString() {
        return "SportTask, ID=" + taskId + ", DataSet=" + sportDataSet;
    }

}
