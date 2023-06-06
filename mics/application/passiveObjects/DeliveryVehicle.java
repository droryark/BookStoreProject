package bgu.spl.mics.application.passiveObjects;

public class DeliveryVehicle {
	private int license;
	private int speed;

	 public DeliveryVehicle(int license, int speed) {
		this.license=license;
		this.speed=speed;
	  }

	public int getLicense() {
		return this.license;
	}

	public int getSpeed() {
		return this.speed;
	}

	public void deliver(String address, int distance) {
		try {
			Thread.sleep(distance*getSpeed());
		}
		catch (InterruptedException ignored) {}
	}
}
