package bgu.spl.mics.application.passiveObjects;


import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class MoneyRegister implements Serializable {

	private final List<OrderReceipt> receipts;
	private final String lock;

	private static class SingletonHolder
	{
		private static MoneyRegister instance = new MoneyRegister();
	}

	private MoneyRegister ()
	{
		this.receipts = new LinkedList<>();
		this.lock = new String();
	}

	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}

	public void file (OrderReceipt r) {
		synchronized (lock) {
			receipts.add(r);
		}
	}

	public int getTotalEarnings() {
		int output=0;
		synchronized (lock) {
			for (OrderReceipt r : receipts) {
				output += r.getPrice();
			}
		}
		return output;

	}

	public void chargeCreditCard(Customer c, int amount) {
		c.setAvailableAmountInCreditCard(amount);
	}

	public void printOrderReceipts(String filename) {

		try (FileOutputStream file = new FileOutputStream(filename)) {
			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(file)) {
				objectOutputStream.writeObject(receipts);
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
