package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TryPurchaseBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;


public class InventoryService extends MicroService{

	private Inventory inventory;
	private CountDownLatch count;

	public InventoryService(String name,CountDownLatch count) {
		super(name);
		inventory = Inventory.getInstance();
		this.count = count;
	}

	@Override
	protected void initialize(){
		this.subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
			if (tickBroadcast.getLast())
				this.terminate();
		});
		this.subscribeEvent(CheckAvailabilityEvent.class, checkAvailabilityEvent -> {
			int price = inventory.checkAvailabiltyAndGetPrice(checkAvailabilityEvent.getBook());
			complete(checkAvailabilityEvent,price);

		});
		this.subscribeEvent(TryPurchaseBookEvent.class, tryPurchaseBookEvent -> {
			boolean isSuccessfullyTaken = inventory.take(tryPurchaseBookEvent.getBook()) == OrderResult.SUCCESSFULLY_TAKEN;
			complete(tryPurchaseBookEvent, isSuccessfullyTaken);
		});
		this.count.countDown();
	}

}
