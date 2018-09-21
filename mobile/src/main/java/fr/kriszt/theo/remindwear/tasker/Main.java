package fr.kriszt.theo.remindwear.tasker;

public class Main {

	public static void main(String[] args) {

		System.out.println("*************************\nTEST CATEGORY\n*************************");

		Category c = new Category("test");
		Category c2 = new Category("test");
		Category c3 = new Category("test2");
		
		System.out.println("INITIALIZE");
		Tasker t = new Tasker();
		Boolean b = t.addCategory(c);
		if(b) {
			System.out.println("CAT INSERTED");
		}else {
			System.out.println("CAT NOT INSERTED");
		}
		b = t.addCategory(c2);
		if(b) {
			System.out.println("CAT INSERTED");
		}else {
			System.out.println("CAT NOT INSERTED");
		}
		b = t.addCategory(c3);
		if(b) {
			System.out.println("CAT INSERTED");
		}else {
			System.out.println("CAT NOT INSERTED");
		}
		
		System.out.println("BEFORE");
		System.out.println(t.getListCategories().toString());
		t.serializeList(t.getListCategories(), "category.txt");
		System.out.println("SERIALIZED");
		t.removeCategory(c);
		t.removeCategory(c2);
		System.out.println("TASK REMVED");
		System.out.println(t.getListCategories().toString());
		System.out.println("UNSERIALIZE");
		t.unserializeListCategories();
		System.out.println(t.getListCategories().toString());

		
	}

}
