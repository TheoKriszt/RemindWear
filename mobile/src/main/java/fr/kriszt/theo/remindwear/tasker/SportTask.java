package fr.kriszt.theo.remindwear.tasker;

import java.util.ArrayList;
import java.util.Calendar;

import fr.kriszt.theo.shared.Coordinates;

public class SportTask extends Task {

    private ArrayList<Coordinates> listCoord = new ArrayList<>();
    private int steps;
    private int heart;
    private int distance;
    private long duration;

    public SportTask(String name, String description, Category category, Calendar dateDeb,
                     int warningBefore, int timeHour, int timeMinutes, Boolean[] repete,
                     int steps, int heart, int distance, long duration) {
        super(name, description, category, dateDeb, warningBefore, timeHour, timeMinutes, repete);
        this.distance = distance;
        this.steps = steps;
        this.heart = heart;
        this.duration = duration;
    }

    public SportTask(Task base, int steps, int heart, int distance, long duration){
        this(base.getName(), base.getDescription(), base.getCategory(), base.getDateDeb(), base.getWarningBefore(), base.getTimeHour(), base.getTimeMinutes(), base.getRepete(),
        steps, heart, distance, duration);
    }

    public SportTask(Task base){
        this(base, 0, 0, 0, 0);
    }

    public void addCoord(Coordinates c){listCoord.add(c);}
    public ArrayList<Coordinates> getListCoord() {return listCoord;}

    public int getSteps() {return steps;}
    public void setSteps(int steps) {this.steps = steps;}

    public int getHeart() {return heart;}
    public void setHeart(int heart) {this.heart = heart;}

    public int getDistance() {return distance;}
    public void setDistance(int distance) {this.distance = distance;}

    //TODO a tester
    public void caculateDistance(){
        int res = 0;
        for(int i=0;i<listCoord.size();i++){
            if(i!=0){
                res += distance(listCoord.get(i-1),listCoord.get(i));
            }
        }
        setDistance(res);
    }

    private double distance(Coordinates c1, Coordinates c2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(c2.getLat() - c1.getLat());
        double lonDistance = Math.toRadians(c2.getLng() - c1.getLng());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(c1.getLat())) * Math.cos(Math.toRadians(c2.getLat()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = c1.getAltitude() - c2.getAltitude();

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public long getDuration() {return duration;}
    public void setDurationSecondes(long duration) {
        this.duration = duration;
    }

}
