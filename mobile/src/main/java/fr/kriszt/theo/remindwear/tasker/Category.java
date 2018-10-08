package fr.kriszt.theo.remindwear.tasker;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	private int ID;
	private String name;
	private int icon;
	private int color;

	public Category(String name, int icon , int color) {
		this.ID = (int) System.currentTimeMillis()/1000;
		this.setName(name);
		this.setIcon(icon);
		this.setColor(color);
	}

	public int getID() {return ID;}
	
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

	@Override
	public boolean equals(@Nullable Object obj) {
		return this.toString().equals(obj.toString());
	}
}
