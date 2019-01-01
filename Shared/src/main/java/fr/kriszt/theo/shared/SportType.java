package fr.kriszt.theo.shared;

public enum SportType {
    SPORT_WALK("Marche", R.drawable.ic_directions_walk),
    SPORT_RUN("Course", R.drawable.ic_directions_run),
    SPORT_BIKE("Vélo", R.drawable.ic_directions_bike);

    private  String name;
    private  int icon;

    SportType(String n, int i) {
        name = n;
        icon= i;
    }

    public String getName(){
        return name;
    }
    public int getIcon(){
        return icon;
    }
}
