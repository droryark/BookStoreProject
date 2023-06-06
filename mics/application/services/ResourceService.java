package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.AcquireEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class ResourceService extends MicroService{

	private ResourcesHolder resource;
	private int currTick;
	private CountDownLatch count;
	private Vector<Future<DeliveryVehicle>> resolveWhenTerminate;

	public ResourceService(String name,CountDownLatch count) {
		super(name);
		this.resource = ResourcesHolder.getInstance();
		this.count = count;
		resolveWhenTerminate=new Vector<>();
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
			currTick=tickBroadcast.getTick();
			if (tickBroadcast.getLast()) {
				this.terminate();
				for (Future<DeliveryVehicle> vec : resolveWhenTerminate) {
					if (vec != null && !vec.isDone())
						vec.resolve(null);
				}
			}
		});
		this.subscribeEvent(AcquireEvent.class, acquireEvent ->{
			Future<DeliveryVehicle> deliveryVehicleFuture = resource.acquireVehicle();
			resolveWhenTerminate.addElement(deliveryVehicleFuture);
			complete(acquireEvent, deliveryVehicleFuture);
		});
		this.subscribeEvent(ReleaseVehicleEvent.class, releaseVehicleEvent -> {
			resource.releaseVehicle(releaseVehicleEvent.getDeliveryVehicle());
			complete(releaseVehicleEvent,null);
		});
		this.count.countDown();
	}

}
