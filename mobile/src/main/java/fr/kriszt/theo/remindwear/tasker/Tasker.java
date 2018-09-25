package fr.kriszt.theo.remindwear.tasker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class Tasker {

	private static ArrayList<Category> listCategories = new ArrayList<Category>();
	private static ArrayList<Task> listTasks = new ArrayList<Task>();
	private static Context context;

    private static Tasker INSTANCE = null;
    public static synchronized Tasker getInstance(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Tasker(context);
        }
        return INSTANCE;
    }

	public Tasker(Context context) {
	    this.context = context;
	    unserializeLists();
	    Category c = new Category("Aucune",0);
	    this.addCategory(c);
	    serializeLists();
    }

	public ArrayList<Task> getListTasks() {return listTasks;}
	public void setListTasks(ArrayList<Task> listTasks) {this.listTasks = listTasks;}
	public static void removeTask(Task t) {listTasks.remove(t);}
	public Boolean addTask(Task t) {
		for(Task x : listTasks) {
			if(x.toString().equals(t.toString())) {
				return false;
			}
		}
		listTasks.add(t);
		return true;
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

    static public void changeWithSaveIsActivatedNotification(Task t) {
        //unserializeLists();
        t.setIsActivatedNotification(!t.getIsActivatedNotification());
        Log.e("&&&&&&&&&&&&&&&&&&&&&",listTasks.toString());
	    serializeLists();
    }

	public static void serializeLists() {
		serializeList(listCategories, "Category.txt");		
		serializeList(listTasks, "Task.txt");
		
	}	
	
	public static void serializeList(ArrayList<?> list, String name) {
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
	
	public static void unserializeLists() {
		unserializeListCategories();
		unserializeListTasks();
	}
	
	public static void unserializeListCategories() {
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
	
	public static void unserializeListTasks() {
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
	
	public static void garbageCollectOld() {
	    unserializeLists();
		ArrayList<Integer> deletes = new ArrayList<>();
		Calendar now = new GregorianCalendar();
		for(int i=0; i < listTasks.size(); i++){
			if(listTasks.get(i).getDateDeb() != null && listTasks.get(i).getDateDeb().compareTo(now) >= 0) {
				   deletes.add(i);
			}
		}
		for(Integer i : deletes) {
			removeTask(listTasks.get(i));
		}
		serializeLists();
	}
	
	public static void sort() {
	    unserializeLists();
		Collections.sort(listTasks, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				int res = 1;
				//TODO auusi heure et minutes
                Calendar o1deb = null;
                Calendar o2deb = null;
                if(o1.getDateDeb() == null){

                }else{
                    o1deb = o1.getDateDeb();
                }
                if(o2.getDateDeb() == null){

                }else{
                    o2deb = o1.getDateDeb();
                }
				/*if(o1deb.after(o2deb)) {
					res = -1;
				}else {
					res = 1;
				}*/
				return res;
			}
		});
		serializeLists();
	}

	public ArrayList<Task> getTasksByName(String seq) {
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
			if(t.getDateDeb() != null || t.getDateDeb().after(calDeb)) {
				res.add(t);
			}
			if(t.getDateDeb() == null){
				//TODO
			}
		}
		return res;
	}
	
	public ArrayList<Task> getTasksByDate(Calendar calDeb,Calendar calEnd){
		ArrayList<Task> res = new ArrayList<>();
		for(Task t : listTasks){
			if(t.getDateDeb() != null ||(t.getDateDeb().after(calDeb) && t.getDateDeb().before(calEnd))) {
				res.add(t);
			}
			if(t.getDateDeb() == null){
				//TODO
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
	}

}
