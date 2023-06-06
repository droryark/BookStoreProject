package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.AcquireEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

public class LogisticsService extends MicroService {
	private CountDownLatch count;
	private int currTick;

	public LogisticsService(String name,CountDownLatch count) {
		super(name);
		this.count = count;

	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
			currTick=tickBroadcast.getTick();
			if (tickBroadcast.getLast())
				this.terminate();
		});
		this.subscribeEvent(DeliveryEvent.class, deliveryEvent -> {
			Future<Future<DeliveryVehicle>> fVehicle = sendEvent(new AcquireEvent());
			if (fVehicle !=null && fVehicle.get() != null) {
				DeliveryVehicle vehicle = fVehicle.get().get();
				if (vehicle != null) {
					vehicle.deliver(deliveryEvent.getCustomer().getAddress(), deliveryEvent.getCustomer().getDistance());
					sendEvent(new ReleaseVehicleEvent(vehicle));
				}
			}
			complete(deliveryEvent,null); // No real meaning
		});
		this.count.countDown();
	}

}
