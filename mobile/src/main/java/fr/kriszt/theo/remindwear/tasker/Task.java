package fr.kriszt.theo.remindwear.tasker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

public class Task implements Serializable {
		
	private static final long serialVersionUID = 1L;
    private int ID;
    private UUID workID;
    private String name;
    private String description;
    private Category category;
    private Calendar dateDeb;
    private int warningBefore;
    private Boolean isActivatedNotification;
    private int timeHour;
    private int timeMinutes;
    private Boolean[] repete ;

	/*private Task(String name, String description, Category category, Calendar dateDeb, int warningBefore, int timeHour, int timeMinutes){
        this.name = name;
        this.description = description;
        this.category = category;
        this.dateDeb = dateDeb;
        this.warningBefore = warningBefore;
        this.setIsActivatedNotification(true);
        this.setTimeHour(timeHour);
        this.setTimeMinutes(timeMinutes);
        this.ID = (int) System.currentTimeMillis()/1000;
    }*/

	public Task(String name, String description, Category category, Calendar dateDeb, int warningBefore, int timeHour, int timeMinutes) {
        this.ID = (int) System.currentTimeMillis()/1000;
        this.workID = null;
        this.name = name;
        this.description = description;
        this.category = category;
        this.dateDeb = dateDeb;
        this.warningBefore = warningBefore;
        this.setIsActivatedNotification(true);
        this.setTimeHour(timeHour);
        this.setTimeMinutes(timeMinutes);
        this.repete = new Boolean[] {
				  false, 
				  false, 
				  false, 
				  false, 
				  false, 
				  false, 
				  false,    
				};
    }
	
	public Task(String name, String description, Category category, Calendar dateDeb, int warningBefore, int timeHour, int timeMinutes, Boolean[] repete) {
		this(name, description, category, dateDeb, warningBefore, timeHour, timeMinutes);
		this.repete = repete;
	}

    public int getID() {return ID;}

    public UUID getWorkID() { return workID; }
    public void setWorkID(UUID uuid) { workID = uuid; }

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
	
	public Boolean getIsActivatedNotification() {return isActivatedNotification;}
	public void setIsActivatedNotification(Boolean isActivatedNotification) {this.isActivatedNotification = isActivatedNotification;}

	public Boolean[] getRepete() {return repete;}
	public void setRepete(int index) {this.repete[index] = !this.repete[index];}
	
	public int getTimeHour() {return timeHour;}
	public void setTimeHour(int time) {this.timeHour = time;}

    public int getTimeMinutes() {return timeMinutes;}
    public void setTimeMinutes(int timeMinutes) {this.timeMinutes = timeMinutes;}

	public String toString() {
		String r = "";
		for(Boolean x : this.repete) {					
			r+="\n\t\t"+x;
		}
		return " [ "
                + "\n\t"+name
				+ "\n\t"+description
				+ "\n\t"+category
				+ "\n\t"+dateDeb
				+ "\n\t"+warningBefore
                + "\n\t"+timeHour
                + "\n\t"+timeMinutes
				+ "\n\t"+isActivatedNotification
				+"\n\t["
				+r
				+"\n\t]"
				+"\n] ";
	}
}
