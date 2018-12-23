package fr.kriszt.theo.shared;

public enum SportType {
    SPORT_WALK("Marche"),
    SPORT_RUN("Course"),
    SPORT_BIKE("Vélo");

    private  String name;

    SportType(String n) {
        name = n;
    }

    public String getName(){
        return name;
    }
}
