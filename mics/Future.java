package bgu.spl.mics;

import java.util.concurrent.TimeUnit;


public class Future<T> {

	private T result;
	private boolean isDone;

	public Future() {
		isDone=false;
	}


	public synchronized T get() {
		try {
			if (!isDone())
				this.wait();
		}
		catch (InterruptedException e)
		{}
		return this.result;
	}


	public synchronized void resolve (T result) {
		this.result = result;
		isDone = true;
		this.notifyAll();
	}

	public boolean isDone() {
		return isDone;
	}


	public T get(long timeout, TimeUnit unit) {
		if (!isDone()) {
			try {
				Thread.sleep(unit.toMillis(timeout));
			}catch (InterruptedException ignored) {}
		}
		return result;
	}

}
