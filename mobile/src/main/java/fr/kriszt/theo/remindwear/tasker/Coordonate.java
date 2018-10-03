package fr.kriszt.theo.remindwear.tasker;

public class Coordonate {

    private double lat;
    private double lng;
    private double h;

    public Coordonate(double lat, double lng, double height){
        this.lat = lat;
        this.lng = lng;
        this.h = height;
    }

    public double getLat() {return lat;}
    public void setLat(double lat) {this.lat = lat;}

    public double getLng() {return lng;}
    public void setLng(double lng) {this.lng = lng;}

    public double getHeight() {return h;}
    public void setHeight(double height) {this.h = height;}
}
