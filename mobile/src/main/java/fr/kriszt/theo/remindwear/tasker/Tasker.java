package fr.kriszt.theo.remindwear.tasker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.workers.ReminderWorker;

public class Tasker {

	private ArrayList<Category> listCategories = new ArrayList<>();
	private ArrayList<Task> listTasks = new ArrayList<>();
	private ArrayList<SportTask> listSportTasks = new ArrayList<>();
	private Context context;

	public static final String CATEGORY_NONE_TAG = "Aucune";
	public static final String CATEGORY_SPORT_TAG = "Sport";

    private static Tasker INSTANCE = null;

    public static synchronized Tasker getInstance(@Nullable Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Tasker(context);
        }
        return INSTANCE;
    }

	public Tasker(Context context) {
	    this.context = context;
	    //if(INSTANCE == null){
            unserializeLists();

            if (getCategoryByName(CATEGORY_NONE_TAG) == null){
				addCategory( new Category(CATEGORY_NONE_TAG, R.drawable.ic_base_0, 0));
			}
			if (getCategoryByName(CATEGORY_SPORT_TAG) == null){
				addCategory( new Category(CATEGORY_SPORT_TAG, R.drawable.baseline_directions_run_24, 0));
			}
            serializeLists();
        //}
    }

	public ArrayList<Task> getListTasks() {return listTasks;}
	public void setListTasks(ArrayList<Task> listTasks) {this.listTasks = listTasks;}
	public void removeTask(Task t) {listTasks.remove(t);}
	public void removeTaskByID(int id){
    	int temp =-1;
    	for (int i =0; i < listTasks.size(); i++){
    		if(listTasks.get(i).getID() == id){
    			temp = i;
    			break;
			}
		}
		if (temp != -1){
    		listTasks.remove(temp);
		}
	}

	public Boolean addTask(Task t) {

        if (t instanceof SportTask){
            return addSportTask((SportTask) t);
        }

		for(Task x : listTasks) {
			if(x.toString().equals(t.toString())) {
				return false;
			}
		}
		listTasks.add(t);
		ReminderWorker.scheduleWorker(t);
		return true;
	}

	public ArrayList<SportTask> getListSportTasks() {return listSportTasks;}
	public void setListSportTasks(ArrayList<SportTask> listSportTasks) {this.listSportTasks = listSportTasks;}
	public void removeSportTask(SportTask t) {listSportTasks.remove(t);}
	public void removeSportTaskByID(int id){
		int temp =-1;
		for (int i =0; i < listSportTasks.size(); i++){
			if(listSportTasks.get(i).getID() == id){
				temp = i;
				break;
			}
		}
		if (temp != -1){
			listSportTasks.remove(temp);
		}
	}

	public Boolean addSportTask(SportTask t) {
		for(Task x : listSportTasks) {
			if(x.toString().equals(t.toString())) {
				return false;
			}
		}
		listSportTasks.add(t);
		return true;
	}

	public void editCategoryById(int id, Category c){
		Category cat = getCategoryByID(id);
		cat.setColor(c.getColor());
		cat.setIcon(c.getIcon());
		cat.setName(c.getName());
	}

	public ArrayList<Category> getListCategories() {return listCategories;}
	public void setListCategories(ArrayList<Category> listCategories) {this.listCategories = listCategories;}
	public void removeCategory(Category c) {listCategories.remove(c);}
	public Boolean addCategory(Category c) {
		for(Category x : listCategories) {
			if(x.toString().equals(c.toString())) {
				return false;
			}
		}
		listCategories.add(c);
		return true;
	}

    public void changeWithSaveIsActivatedNotification(Task t) {
        t.setIsActivatedNotification(!t.getIsActivatedNotification());
	    serializeLists();
    }

	public void serializeLists() {
		serializeList(listCategories, "Category.txt");
		serializeList(listTasks, "Task.txt");
		serializeList(listSportTasks, "SportTask.txt");

	}	
	
	public void serializeList(ArrayList<?> list, String name) {
        try {
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void unserializeLists() {
		unserializeListCategories();
		unserializeListTasks();
		unserializeListSportTasks();
	}
	
	public void unserializeListCategories() {
		ArrayList<Category> list = new ArrayList<Category>();
        try {
            FileInputStream fis = context.openFileInput("Category.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            list = (ArrayList<Category>) is.readObject();
            is.close();
            fis.close();

        }catch (Exception e){
            serializeLists();
            try{
                FileInputStream fis = context.openFileInput("Category.txt");
                ObjectInputStream is = new ObjectInputStream(fis);
                list = (ArrayList<Category>) is.readObject();
                is.close();
                fis.close();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
		listCategories  = list;
	}
	
	public void unserializeListTasks() {
		ArrayList<Task> list = new ArrayList<Task>();
        try {
            FileInputStream fis = context.openFileInput("Task.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            list = (ArrayList<Task>) is.readObject();
            is.close();
            fis.close();

        }catch (Exception e){
            serializeLists();
            try{
                FileInputStream fis = context.openFileInput("Task.txt");
                ObjectInputStream is = new ObjectInputStream(fis);
                list = (ArrayList<Task>) is.readObject();
                is.close();
                fis.close();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
        listTasks = list;
    }

	public void unserializeListSportTasks() {
		ArrayList<SportTask> list = new ArrayList<>();
		try {
			FileInputStream fis = context.openFileInput("SportTask.txt");
			ObjectInputStream is = new ObjectInputStream(fis);
			list = (ArrayList<SportTask>) is.readObject();
			is.close();
			fis.close();

		}catch (Exception e){
			serializeLists();
			try{
				FileInputStream fis = context.openFileInput("SportTask.txt");
				ObjectInputStream is = new ObjectInputStream(fis);
				list = (ArrayList<SportTask>) is.readObject();
				is.close();
				fis.close();
			}catch (Exception e2){
				e2.printStackTrace();
			}
			e.printStackTrace();
		}
		listSportTasks = list;
	}
	
	public void garbageCollectOld() {
	    unserializeLists();
		ArrayList<Integer> deletes = new ArrayList<>();
		Calendar now = new GregorianCalendar();
		for(int i=0; i < listTasks.size(); i++){
			if(listTasks.get(i).getDateDeb() != null && listTasks.get(i).getNextDate().compareTo(now) < 0) {
				   deletes.add(listTasks.get(i).getID());
			}
		}
		for(Integer i : deletes) {
			removeTaskByID(i);
		}
		serializeLists();
	}
	
	public void sort(final Boolean growing) {
        unserializeLists();
        Collections.sort(listTasks, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                int res = 1;
                if(growing){
                    res *= -1;
                }
                Calendar o1deb = o1.getNextDate();
                Calendar o2deb = o2.getNextDate();

                if (o1deb.after(o2deb)) {
                    return res*-1;
                }

                if (o1deb.before(o2deb)) {
                    return res;
                }
                return 0;
            }
        });
        serializeLists();
    }

    public void sportSort(final Boolean growing) {
        unserializeLists();
        Collections.sort(listSportTasks, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                int res = 1;
                if(growing){
                    res *= -1;
                }
                Calendar o1deb = o1.getNextDate();
                Calendar o2deb = o2.getNextDate();

                if (o1deb.after(o2deb)) {
                    return res*-1;
                }

                if (o1deb.before(o2deb)) {
                    return res;
                }
                return 0;
            }
        });
        serializeLists();
    }

	public ArrayList<Task> filter(String seq, Boolean growing) {
		ArrayList<Task> res = new ArrayList<>();
		sort(growing);
		seq = seq.toUpperCase();
		for(Task t : listTasks){
			SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy");
            String dateFormated = format.format(t.getNextDate().getTime());
			if(t.getName().toUpperCase().contains(seq)) {
				res.add(t);
			}else if(t.getCategory().getName().toUpperCase().contains(seq)){
				res.add(t);
			}else if(t.getDescription().toUpperCase().contains(seq)){
				res.add(t);
			}else if(dateFormated.toUpperCase().contains(seq)){
				res.add(t);
			}
		}
		return res;
	}

    public ArrayList<SportTask> sportFilter(String seq, Boolean growing) {
        ArrayList<SportTask> res = new ArrayList<>();
        sort(growing);
        seq = seq.toUpperCase();
        for(SportTask t : listSportTasks){
            SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy");
            String dateFormated = format.format(t.getNextDate().getTime());
            if(t.getName().toUpperCase().contains(seq)) {
                res.add(t);
            }else if(t.getCategory().getName().toUpperCase().contains(seq)){
                res.add(t);
            }else if(t.getDescription().toUpperCase().contains(seq)){
                res.add(t);
            }else if(dateFormated.toUpperCase().contains(seq)){
                res.add(t);
            }
        }
        return res;
    }

	/*public ArrayList<Task> getTasksByName(String seq) {
		ArrayList<Task> res = new ArrayList<>();
		for(Task t : listTasks){
			if(t.getName().contains(seq)) {
				res.add(t);
			}
		}
		return res;
	}
	
	public ArrayList<Task> getTasksByDescription(String seq) {
		ArrayList<Task> res = new ArrayList<>();
		for(Task t : listTasks){
			if(t.getDescription().contains(seq)) {
				res.add(t);
			}
		}
		return res;
	}
	
	public ArrayList<Task> getTasksByCategory(Category c) {
		ArrayList<Task> res = new ArrayList<>();
		for(Task t : listTasks){
			if(t.getCategory() != null && t.getCategory().equals(c)) {
				res.add(t);
			}
		}
		return res;
	}
	
	public ArrayList<Task> getTasksByDate(Calendar calDeb) {
		ArrayList<Task> res = new ArrayList<>();
		for(Task t : listTasks){
			if(t.getNextDate() != null && t.getNextDate().after(calDeb)) {
				res.add(t);
			}
		}
		return res;
	}
	
	public ArrayList<Task> getTasksByDate(Calendar calDeb,Calendar calEnd){
		ArrayList<Task> res = new ArrayList<>();
		for(Task t : listTasks){
			if(t.getNextDate() != null && (t.getNextDate().after(calDeb) && t.getNextDate().before(calEnd))) {
				res.add(t);
			}
		}
		return res;
	}
	
	public ArrayList<Task> getTasksByActivated(Boolean b ){ 
		ArrayList<Task> res = new ArrayList<>();
		for(Task t : listTasks){
			if(b == t.getIsActivatedNotification()) {
				res.add(t);
			}
		}
		return res;
	}*/

	public Task getTaskByID(int id){
		for(Task t : listTasks){
			if( t.getID() ==  id) {
				return t;
			}
		}
    	return null;
	}

    public SportTask getSportTaskByID(int id){
        for(SportTask t : listSportTasks){
            if( t.getID() ==  id) {
                return t;
            }
        }
        return null;
    }

	public Category getCategoryByID(int id){
		for(Category c : listCategories){
			if( c.getID() ==  id) {
				return c;
			}
		}
		return null;
	}

	public Category getCategoryByName(String catName){
    	for (Category c : listCategories){
    		if (c.getName().equals(catName)){
    			return c;
			}
		}
		return null;
	}

    public ArrayList<Task> getTasksByCategory(Category c){
        ArrayList<Task> matches = new ArrayList<>();
        for (Task t : getListTasks()){
            if (t.getCategory().equals(c)){
                matches.add(t);
            }
        }
        return matches;
    }

}
