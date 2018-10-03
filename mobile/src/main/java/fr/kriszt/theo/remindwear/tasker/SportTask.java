package fr.kriszt.theo.remindwear.tasker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class SportTask extends Task {

    private ArrayList<Coordonate> listCoord = new ArrayList<>();

    public SportTask(String name, String description, Category category, Calendar dateDeb, int warningBefore, int timeHour, int timeMinutes, Boolean[] repete) {
        super(name, description, category, dateDeb, warningBefore, timeHour, timeMinutes, repete);
    }

    public void addCoord(Coordonate c){listCoord.add(c);}
    public ArrayList<Coordonate> getListCoord() {return listCoord;}
}
