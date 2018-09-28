package fr.kriszt.theo.remindwear.tasker;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int icon;
	private int color;

	public Category(String name, int icon , int color) {
		this.setName(name);
		this.setIcon(icon);
		this.setColor(color);
	}
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	public int getIcon() {return icon;}
	public void setIcon(int icon) {this.icon = icon;}

	public int getColor() {return color;}
	public void setColor(int color) {this.color = color;}

	public String toString() {
		return " [ "
				+ "\n\t"+name
				+ "\n\t"+icon
				+ "\n\t"+color
				+ "\n] ";
	}


}
