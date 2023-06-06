package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class ResourcesHolder {

	private final ConcurrentLinkedQueue<Future<DeliveryVehicle>> toBeResolved;
	private final ConcurrentLinkedQueue<DeliveryVehicle> vehicles;
	private Semaphore sem;
	private final Object lock;

	private static class SingletonHolder
	{
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	private ResourcesHolder ()
	{
		toBeResolved = new ConcurrentLinkedQueue<>();
		vehicles = new ConcurrentLinkedQueue<>();
		lock = new Object();
	}

	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}

	public Future<DeliveryVehicle> acquireVehicle() {
		synchronized (lock) {
			Future<DeliveryVehicle> output = new Future<>();
			if (sem.tryAcquire())
				output.resolve(this.vehicles.poll());
			else {
				this.toBeResolved.add(output);
			}
			return output;
		}
	}

	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (lock) {
			if (this.toBeResolved.isEmpty())
				sem.release();
			else
				this.toBeResolved.poll().resolve(vehicle);
		}
	}

	public void load(DeliveryVehicle[] vehicles) {
		this.vehicles.addAll(Arrays.asList(vehicles));
		this.sem = new Semaphore(vehicles.length);
	}

}
