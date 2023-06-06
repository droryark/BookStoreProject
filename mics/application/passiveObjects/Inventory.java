package bgu.spl.mics.application.passiveObjects;


import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Inventory implements Serializable {

	private Vector<BookInventoryInfo> storage;
	private HashMap<String,Semaphore> map;
	private HashMap<String,Integer> toPrint;

	private static class SingletonHolder
	{
		private static Inventory instance = new Inventory();
	}

	private Inventory ()
	{
		this.storage=new Vector<>();
		this.map=new HashMap<>();
		this.toPrint=new HashMap<>();
	}

	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	public void load(BookInventoryInfo[ ] inventory ) {
		for (BookInventoryInfo b: inventory)	{
			storage.addElement(b);
			Semaphore sem = new Semaphore(b.getAmountInInventory());
			map.put(b.getBookTitle(),sem);
		}
	}

	public OrderResult take(String book) {
		for (int i = 0; i < storage.size(); i++) {
			if (book.equals(storage.elementAt(i).getBookTitle()) && storage.elementAt(i).getAmountInInventory()>0)
			{
				if (map.get(book).tryAcquire()){
					storage.elementAt(i).setAmountInInventory();
					return OrderResult.SUCCESSFULLY_TAKEN;
				}
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}

	public int checkAvailabiltyAndGetPrice(String book) {
		for (int i = 0; i < storage.size(); i++) {
			if (book.equals(storage.elementAt(i).getBookTitle()) && storage.elementAt(i).getAmountInInventory()>0)
				return storage.elementAt(i).getPrice();
		}
		return -1;
	}
	private void createMap() {
		for (BookInventoryInfo bookInventoryInfo : storage) {
			this.toPrint.put(bookInventoryInfo.getBookTitle(), bookInventoryInfo.getAmountInInventory());
		}
	}

	public void printInventoryToFile(String filename){
		this.createMap();
		try (FileOutputStream file = new FileOutputStream(filename)) {
			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(file)) {
				objectOutputStream.writeObject(toPrint);
				objectOutputStream.close();
				file.close();
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: "+ filename);
		}catch (IOException e) {
			System.out.println("Error Writing file: '" +filename+"'");
		}
	}
}
