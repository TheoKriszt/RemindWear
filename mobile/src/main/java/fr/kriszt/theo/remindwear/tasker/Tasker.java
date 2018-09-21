package fr.kriszt.theo.remindwear.tasker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Tasker {
	

    /*
    phone: 5.1
    wear: 4.4

    class tache (
            date
        duree
        jour
        serialisation
        methode static create tache
            methode crud
    )

    database??? espace de staockage android
            */
	
	
	private ArrayList<Category> listCategories = new ArrayList<Category>();
	private ArrayList<Task> listTasks = new ArrayList<Task>();
	
	public Tasker() {
		
	}

	public ArrayList<Task> getListTasks() {return listTasks;}
	public void setListTasks(ArrayList<Task> listTasks) {this.listTasks = listTasks;}
	public void removeTask(Task t) {listTasks.remove(t);}
	public Boolean addTask(Task t) {
		for(Task x : listTasks) {
			if(x.equals(t)) {
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
			if(x.getName().equals(c.getName())) {
				return false;
			}
		}
		listCategories.add(c);
		return true;
	}
	
	public void serializeList(ArrayList<?> list, String name) {
	       try{
	         FileOutputStream fos= new FileOutputStream(name);
	         ObjectOutputStream oos= new ObjectOutputStream(fos);
	         oos.writeObject(list);
	         oos.close();
	         fos.close();
	       }catch(IOException ioe){
	            ioe.printStackTrace();
	        }
	}
	
	public void unserializeListCategories() {
		ArrayList<Category> list = new ArrayList<Category>();
		try
        {
            FileInputStream fis = new FileInputStream("Category.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<Category>) ois.readObject();
            ois.close();
            fis.close();
         }catch(IOException ioe){
             ioe.printStackTrace();
             return;
          }catch(ClassNotFoundException c){
             System.out.println("Class not found");
             c.printStackTrace();
             return;
          }
        setListCategories(list);
	}
	
	public void unserializeListTasks() {
		ArrayList<Task> list = new ArrayList<Task>();
		try
        {
            FileInputStream fis = new FileInputStream("Category.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<Task>) ois.readObject();
            ois.close();
            fis.close();
         }catch(IOException ioe){
             ioe.printStackTrace();
             return;
          }catch(ClassNotFoundException c){
             System.out.println("Class not found");
             c.printStackTrace();
             return;
          }
        setListTasks(list);
	}

}
