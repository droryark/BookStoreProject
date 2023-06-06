package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class APIService extends MicroService{
	private Customer customer;
	private Vector<Future<OrderReceipt>> vector;
	private CountDownLatch count;


	public APIService(String name, Customer customer, CountDownLatch count) {
		super(name);
		this.customer = customer;
		vector = new Vector<>();
		this.count=count;
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
			List<String> books = customer.getSchedules(tickBroadcast.getTick());
			if (books != null){
				vector.clear();
				for (String book : books) {
					vector.addElement(this.sendEvent(new BookOrderEvent(customer, book, tickBroadcast.getTick())));
				}
				for (Future<OrderReceipt> orderReceiptFuture : vector) {
					if (orderReceiptFuture != null && orderReceiptFuture.get() != null)
						customer.getCustomerReceiptList().add(orderReceiptFuture.get());
				}
			}
			if (tickBroadcast.getLast())
				this.terminate();
		});
		this.count.countDown();
	}

}
