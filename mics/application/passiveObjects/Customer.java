package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Customer implements Serializable {

	private int id;
	private String name;
	private String address;
	private int distance;
	private CreditCard creditCard;
	private Scheduler[] orderSchedule;
	private int availableAmountInCreditCard;
	private HashMap<Integer,List<String>> schedules;
	private CreditCard lock;
	private int creditCardNumber;
	private List<OrderReceipt> Receipts;


	public Customer (int id, String name, String add, int dis, int credit, int money, HashMap<Integer,List<String>> schedules)
	{
		this.address = add;
		this.id = id;
		this.name = name;
		this.distance = dis;
		this.availableAmountInCreditCard = money;
		this.Receipts = new LinkedList<>();
		this.schedules = schedules;
		this.lock = new CreditCard(0,0);
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public String getAddress() {
		return this.address;
	}

	public int getDistance() {
		return this.distance;
	}

	public List<OrderReceipt> getCustomerReceiptList() {
		return this.Receipts;
	}

	public int getAvailableCreditAmount() {
		return this.availableAmountInCreditCard;
	}

	public int getCreditNumber() {
		return creditCard.getNumber();
	}

	public void setAvailableAmountInCreditCard(int toReduce)
	{
		this.availableAmountInCreditCard-=toReduce;
	}

	public List<String> getSchedules(Integer tick) {
		return schedules.get(tick);
	}

	public CreditCard getLock() {
		return lock;
	}
	public void setCustomer(){
		schedules=new HashMap<>();

		for (Scheduler curr : this.orderSchedule) {
			if (schedules.containsKey(curr.getTick()))
				schedules.get(curr.getTick()).add(curr.getName());
			else {
				List<String> list = new ArrayList<>();
				list.add(curr.getName());
				schedules.put(curr.getTick(), list);
			}
		}
		this.availableAmountInCreditCard = this.creditCard.getAmount();
		this.creditCardNumber = this.creditCard.getNumber();
		this.lock = new CreditCard(0,0);
		this.Receipts = new LinkedList<>();
	}
}
