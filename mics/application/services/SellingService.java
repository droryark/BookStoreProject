package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.messages.TryPurchaseBookEvent;

import java.util.concurrent.CountDownLatch;


public class SellingService extends MicroService{
	private MoneyRegister moReg;
	private int currTick;
	private CountDownLatch count;

	public SellingService(String name,CountDownLatch count) {
		super(name);
		this.moReg=MoneyRegister.getInstance();
		this.count=count;
	}

	@Override
	protected void initialize() {
		subscribeToTickBroadcast();
		subscribeToBookOrderEvent();
		this.count.countDown();
	}

	private void subscribeToTickBroadcast() {
		this.subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
			currTick=tickBroadcast.getTick();
			if (tickBroadcast.getLast())
				this.terminate();
		});
	}

	private void subscribeToBookOrderEvent() {
		this.subscribeEvent(BookOrderEvent.class, bookOrderEvent -> {
			Future<Integer> futurePriceIfAvailable = sendEvent(new CheckAvailabilityEvent(bookOrderEvent.getBook()));
			if (futurePriceIfAvailable != null) {
				int price = futurePriceIfAvailable.get();
				if (price != -1) {
					processOrder(bookOrderEvent, price);
				} else {
					complete(bookOrderEvent, null);
				}
			} else {
				complete(bookOrderEvent, null);
			}
		});
	}
	private void processOrder(BookOrderEvent bookOrderEvent, int price) {
		synchronized (bookOrderEvent.getCustomer().getLock()) {
			if (bookOrderEvent.getCustomer().getAvailableCreditAmount() >= price) {
				if (tryToPurchaseBook(bookOrderEvent, price)) {
					sendEvent(new DeliveryEvent(bookOrderEvent.getCustomer()));
					completeAndFileOrderReceipt(bookOrderEvent, price);
				} else {
					complete(bookOrderEvent, null);
				}
			} else {
				complete(bookOrderEvent, null);
			}
		}
	}

	private boolean tryToPurchaseBook(BookOrderEvent bookOrderEvent, int price) {
		Future<Boolean> successPurchaseFuture = sendEvent(new TryPurchaseBookEvent(bookOrderEvent.getBook()));
		if (successPurchaseFuture != null && Boolean.TRUE.equals(successPurchaseFuture.get())) {
			moReg.chargeCreditCard(bookOrderEvent.getCustomer(), price);
			return true;
			}
		return false;
	}


	private void completeAndFileOrderReceipt(BookOrderEvent bookOrderEvent, int price) {
		OrderReceipt rec = new OrderReceipt(0, this.getName(), bookOrderEvent.getCustomer().getId(),
				bookOrderEvent.getBook(), price, currTick, bookOrderEvent.getTick(), currTick);
		complete(bookOrderEvent, rec);
		moReg.file(rec);
	}
}
