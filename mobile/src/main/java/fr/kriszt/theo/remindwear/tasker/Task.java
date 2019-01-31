package fr.kriszt.theo.remindwear.tasker;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class
Task implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String TAG = Task.class.getSimpleName();
    private int ID; // id de la tâche
    private UUID workID; // ID du Worker qui planifie son rappel
    private String name;
    private String description;
    private Category category;
    private Calendar dateDeb;
    private int warningBefore;
    private Boolean isActivatedNotification;
    private int timeHour;
    private int timeMinutes;
    private Boolean[] repete;

    public Task(String name, String description, Category category, Calendar dateDeb, int warningBefore, int timeHour, int timeMinutes) {
        this.ID = (int) System.currentTimeMillis() / 1000;
        this.workID = null;
        this.name = name;
        this.description = description;
        this.category = category;
        this.dateDeb = dateDeb;
        this.warningBefore = warningBefore;
        this.setIsActivatedNotification(true);
        this.setTimeHour(timeHour);
        this.setTimeMinutes(timeMinutes);
        this.repete = new Boolean[]{
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

    public int getID() {
        return ID;
    }

    public UUID getWorkID() {
        return workID;
    }

    public void setWorkID(UUID uuid) {
        workID = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Calendar getDateDeb() {
        return dateDeb;
    }

    public int getWarningBefore() {
        return warningBefore;
    }

    public int getWarningBeforeSeconds() {
        return getWarningBefore() * 60;
    }

    public Boolean getIsActivatedNotification() {
        return isActivatedNotification;
    }

    void setIsActivatedNotification(Boolean isActivatedNotification) {
        this.isActivatedNotification = isActivatedNotification;
    }

    public Boolean[] getRepete() {
        return repete;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(int time) {
        this.timeHour = time;
    }

    public int getTimeMinutes() {
        return timeMinutes;
    }

    public void setTimeMinutes(int timeMinutes) {
        this.timeMinutes = timeMinutes;
    }

    @NonNull
    public String toString() {
        StringBuilder r = new StringBuilder();
        String[] daysOfWeek = {"lun", "mar", "mer", "jeu", "ven", "sam", "dim"};
        for (int i = 0; i < this.repete.length; i++) {
            r.append(daysOfWeek[i]).append("=").append(this.repete[i]).append(", ");
        }
        for (Boolean x : this.repete) {
            r.append("\n\t\t").append(x);
        }
        return " [ "
                + "\n\tID : " + ID
                + "\n\t" + name
                + "\n\t" + description
                + "\n\t" + category
                + "\n\t" + dateDeb.get(Calendar.HOUR_OF_DAY) + "h " + dateDeb.get(Calendar.MINUTE)
                + "\n\t" + warningBefore
                + "\n\t" + timeHour
                + "\n\t" + timeMinutes
                + "\n\t" + isActivatedNotification
                + "\n\t["
                + r
                + "\n\t]"
                + "\n] ";
    }

    /**
     * Utilisé pour les tâches récurrentes
     * @return la prochaine occurence de la tâche en prenant en compte si elle se répète certains jours ou non
     */
    public Calendar getNextDate() {
        if (dateDeb == null) {
            Calendar now = new GregorianCalendar();
            int day = 0;
            int first = 0;
            for (int i = 0; i < getRepete().length; i++) {
                if (getRepete()[i] && first == 0) {
                    first = i;
                }
                if ((i + 1 >= now.get(Calendar.DAY_OF_WEEK)) && getRepete()[i]) {
                    day = i;
                    break;
                }
            }

            if (day == 0) {
                day = first + 7;
            }
            Calendar c = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), getTimeHour(), getTimeMinutes());
            c.add(Calendar.DAY_OF_MONTH, day + 2 - now.get(Calendar.DAY_OF_WEEK));
            Calendar oneWeekBefore = (Calendar) c.clone();
            oneWeekBefore.add(Calendar.WEEK_OF_YEAR, -1);
            if (oneWeekBefore.after(new GregorianCalendar())) {
                c = oneWeekBefore;
            }
            return c;
        } else {
            return new GregorianCalendar(dateDeb.get(Calendar.YEAR), dateDeb.get(Calendar.MONTH), dateDeb.get(Calendar.DAY_OF_MONTH), getTimeHour(), getTimeMinutes());
        }
    }

    private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param timeUnit ex  : SECONDS | MINITES
     * @return le temps restant avant la prochaine execution de la tâche
     */
    public long getRemainingTime(TimeUnit timeUnit) {
        Calendar cal = getNextDate();
        cal.set(Calendar.HOUR, getTimeMinutes());
        cal.set(Calendar.MINUTE, getTimeMinutes());
        cal.add(Calendar.MINUTE, -1 * getWarningBefore());

        Date mDate = getNextDate().getTime();
        return getDateDiff(mDate, new Date(), timeUnit);
    }

}
