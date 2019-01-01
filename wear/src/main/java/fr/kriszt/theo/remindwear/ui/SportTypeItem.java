package fr.kriszt.theo.remindwear.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import fr.kriszt.theo.shared.SportType;

public class SportTypeItem implements Serializable{

    public String name;
    public Drawable icon;

    public SportTypeItem(String n, Drawable i){
        name = n;
        icon = i;
    }

    public static List<SportTypeItem> getAvailableSportTypes(Context c, boolean hasGPS, boolean hasPodometer, boolean hasCardiometer){
        ArrayList<SportTypeItem> items = new ArrayList<>();


        if (hasPodometer){
            items.add(new SportTypeItem(SportType.SPORT_WALK.getName(), c.getDrawable(SportType.SPORT_WALK.getIcon())));
        }

        if (hasGPS){
            items.add(new SportTypeItem(SportType.SPORT_RUN.getName(), c.getDrawable(SportType.SPORT_RUN.getIcon())));
            items.add(new SportTypeItem(SportType.SPORT_BIKE.getName(), c.getDrawable(SportType.SPORT_BIKE.getIcon())));
        }

        return items;
    }


}
