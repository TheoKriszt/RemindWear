package fr.kriszt.theo.remindwear;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import fr.kriszt.theo.shared.SportType;

class SportTypeItem {

    public String name;
    public Drawable icon;

    public SportTypeItem(String n, Drawable i){
        name = n;
        icon = i;
    }

    public static List<SportTypeItem> getAvailableSportTypes(Context c, boolean hasGPS, boolean hasPodometer, boolean hasCardiometer){
        ArrayList<SportTypeItem> items = new ArrayList<>();


        if (hasPodometer){
            items.add(new SportTypeItem("Marche", c.getDrawable(R.drawable.ic_directions_walk)));
        }

        if (hasGPS){
            items.add(new SportTypeItem("Course", c.getDrawable(R.drawable.ic_directions_run)));
            items.add(new SportTypeItem(SportType.SPORT_BIKE.getName(), c.getDrawable(R.drawable.ic_directions_bike)));
        }

        return items;
    }


}
