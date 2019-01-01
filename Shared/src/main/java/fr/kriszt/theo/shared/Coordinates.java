package fr.kriszt.theo.shared;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class Coordinates implements Serializable {

    private static final String TAG = "Coordinates";
    private final double MS_TO_KMH = 3.6;

    private transient Location location = null;
    private long timestamp;
    private double speed;
    private double lat;
    private double lng;
    private double altitude;

    public Coordinates(double lat, double lng, double alt, double speed_ms){
        this.lat = lat;
        this.lng = lng;
        this.altitude = alt;
        timestamp = new Date().getTime(); //now
        speed = speed_ms;

    }

    public Coordinates(Location location) {
        this( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed() );
        this.location = location;
    }

    public double getLat() {return lat;}
    public void setLat(double lat) {this.lat = lat;}

    public double getLng() {return lng;}
    public void setLng(double lng) {this.lng = lng;}

    public double getAltitude() {return altitude;}

    public void setAltitude(double altitude) {this.altitude = altitude;}

    public double getSpeedkmh() {
        return MS_TO_KMH * speed;
    }

    public double getSpeedMS() {
        return speed;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public float distanceTo(Coordinates coordinates) {
        if (location == null){
            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(lat);
            location.setLongitude(lng);
            location.setAltitude(getAltitude());
            location.setSpeed((float) getSpeedMS());
        }


        return location.distanceTo(coordinates.location);
    }

    @Override
    public String toString(){
        return "Lat="+lat+", Lon="+lng+", speed="+getSpeedkmh();
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Coordinates && ((Coordinates) o).lat == lat && ((Coordinates) o).lng == lng;
    }
}
