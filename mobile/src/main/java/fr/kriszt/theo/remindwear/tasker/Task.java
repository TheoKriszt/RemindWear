package fr.kriszt.theo.remindwear.tasker;

import java.io.Serializable;
import java.util.Calendar;

public class Task implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private Category category;
	private Calendar dateDeb;
	private int warningBefore;

	public Task(String name, String description, Category category, Calendar dateDeb, int warningBefore) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.dateDeb = dateDeb;
		this.warningBefore = warningBefore;
	}	
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	
	public Category getCategory() {return category;}
	public void setCategory(Category category) {this.category = category;}
	
	public Calendar getDateDeb() {return dateDeb;}
	public void setDateDeb(Calendar dateDeb) {this.dateDeb = dateDeb;}
	
	public int getWarningBefore() {return warningBefore;}
	public void setWarningBefore(int warningBefore) {this.warningBefore = warningBefore;}
	
	public String toString() {
		return " [ "
				+ "\n\t"+name
				+ "\n\t"+description
				+ "\n\t"+category
				+ "\n\t"+dateDeb
				+ "\n\t"+warningBefore
				+" ] ";
	}
	

}
