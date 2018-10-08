package fr.kriszt.theo.remindwear.tasker;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

public class
Task implements Serializable {
		
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
                + "\n\tID : "+ID
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

	public Calendar getNextDate(){
		if(dateDeb == null){
			Calendar now = new GregorianCalendar();
            int day=0;
            int first = 0;
            for(int i=0; i<getRepete().length; i++){
                if(getRepete()[i] && first == 0){
                    first = i;
                }
                if((i+1 >= now.get(Calendar.DAY_OF_WEEK)) && getRepete()[i]){
                    day = i;
                    break;
                }
            }
            Log.e("ddddddddddddddddddDAY", String.valueOf(day));
            Log.e("ffffffffffffffffirst", String.valueOf(first));
            if(day == 0){
                day = first + 7;
            }
            Calendar c = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), getTimeHour(), getTimeMinutes());
            c.add(Calendar.DAY_OF_MONTH, day+2 - now.get(Calendar.DAY_OF_WEEK));
            Log.e("ccccccccccccccccccccc", String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
			return c;
		}else{
            Calendar c = new GregorianCalendar(dateDeb.get(Calendar.YEAR), dateDeb.get(Calendar.MONTH), dateDeb.get(Calendar.DAY_OF_MONTH), getTimeHour(), getTimeMinutes());
            return c;
		}
	}

    private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

	public long getDuration(TimeUnit timeUnit){
		Calendar cal = getNextDate();
		cal.set(Calendar.HOUR, getTimeMinutes());
		cal.set(Calendar.MINUTE, getTimeMinutes());
		cal.add(Calendar.MINUTE, -1 * getWarningBefore());
		//cal.roll(Calendar.MINUTE, getWarningBefore());
	    Date mDate  = getNextDate().getTime();
        /*mDate.setHours(getTimeHour());
	    mDate.setMinutes(getTimeMinutes());*/
        return getDateDiff(mDate,new Date(),timeUnit);
    }

}
