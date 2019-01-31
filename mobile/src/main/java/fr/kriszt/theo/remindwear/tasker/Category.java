package fr.kriszt.theo.remindwear.tasker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    private int ID;
    private int icon;
    private int color;
    private String name;

    public Category(String name, int icon, int color) {
        this.ID = (int) System.currentTimeMillis() / 1000;
        this.setName(name);
        this.setIcon(icon);
        this.setColor(color);
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @NonNull
    @Override
    public String toString() {
        return "Category \"" + name + "\" : color=" + color + ", icon=" + icon;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        assert obj != null;
        return this.toString().equals(obj.toString());
    }
}
