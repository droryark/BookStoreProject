package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class OrderReceipt implements Serializable {

	private int orderId;
	private String seller;
	private int customer;
	private String bookTitle;
	private int price;
	private int issuedTick;
	private int orderTick;
	private int proccessTick;

	public OrderReceipt (int orderId, String seller, int cus, String name, int price, int isTick, int orTick, int prTick)
	{
		this.bookTitle = name;
		this.customer = cus;
		this.issuedTick = isTick;
		this.orderId = orderId;
		this.seller = seller;
		this.price = price;
		this.orderTick = orTick;
		this.proccessTick = prTick;
	}

	public int getOrderId() {
		return orderId;
	}

	public String getSeller() {
		return seller;
	}

	public int getCustomerId() {
		return customer;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public int getPrice() {
		return price;
	}

	public int getIssuedTick() {
		return issuedTick;
	}

	public int getOrderTick() {
		return orderTick;
	}

	public int getProcessTick() {
		return proccessTick;
	}

	@Override
	public String toString() {
		return "OrderReceipt{" +
				"seller='" + seller + '\'' +
				", customer=" + customer +
				", bookTitle='" + bookTitle + '\'' +
				", price=" + price +
				", issuedTick=" + issuedTick +
				", orderTick=" + orderTick +
				", proccessTick=" + proccessTick +
				'}';
	}
}
