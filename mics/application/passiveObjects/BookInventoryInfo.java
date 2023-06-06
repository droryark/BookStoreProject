package bgu.spl.mics.application.passiveObjects;

public class BookInventoryInfo {

	private String bookTitle;
	private int amount;
	private int price;

	public BookInventoryInfo (String name,int amount, int price)
	{
		this.amount = amount;
		this.bookTitle = name;
		this.price = price;
	}

	public String getBookTitle() {
		return this.bookTitle;
	}

	public int getAmountInInventory() {
		return this.amount;
	}

	public int getPrice() {
		return this.price;
	}

	public void setAmountInInventory()
	{
		this.amount--;
	}

}
