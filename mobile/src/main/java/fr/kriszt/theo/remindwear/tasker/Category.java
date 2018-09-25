package fr.kriszt.theo.remindwear.tasker;

import java.io.Serializable;

public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int icon; 
	
	public Category(String name) {
		this.setName(name);
		this.setIcon(0);
	}
	
	public Category(String name, int icon) {
		this.setName(name);
		this.setIcon(icon);
	}
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	public int getIcon() {return icon;}
	public void setIcon(int icon) {this.icon = icon;}

	public String toString() {
		return " [ "
				+ "\n\t"+name
				+ "\n\t"+icon
				+ "\n] ";
	}

}
