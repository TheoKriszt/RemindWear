package fr.kriszt.theo.shared;

import android.location.Location;

import java.io.Serializable;
import java.util.Date;

public class Coordinates implements Serializable {

    private final double MS_TO_KMH = 3.6;

    private Location location = null;
    private long timestamp;
    private double speed;
    private double lat;
    private double lng;
    private double h;

    private Coordinates(double lat, double lng, double alt, double speed_ms){
        this.lat = lat;
        this.lng = lng;
        this.h = alt;
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

    public double getAltitude() {return h;}

    public void setAltitude(double altitude) {this.h = altitude;}

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
        return location.distanceTo(coordinates.location);
    }
}
