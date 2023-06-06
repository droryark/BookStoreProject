package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminateTimeServiceEvent;
import java.util.Timer;
import java.util.TimerTask;


public class 	TimeService extends MicroService{
	private int speed,duration;
	private Timer timer;
	private TimerTask task;
	private int currentTime;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		this.currentTime = 1;
		this.timer = new Timer();
		this.task = new TimerTask() {
			@Override
			public void run() {
				if (currentTime == duration){
					sendBroadcast(new TickBroadcast(currentTime,true));
					sendEvent(new TerminateTimeServiceEvent());
					timer.cancel();
					task.cancel();
				}
				else {
					sendBroadcast(new TickBroadcast(currentTime,false));
				}
				currentTime++;
			}
		};
	}

	@Override
	protected void initialize() {
		this.subscribeEvent(TerminateTimeServiceEvent.class, terminateTimeServiceEvent ->{
			this.terminate();
		});
		timer.scheduleAtFixedRate(task,0, speed);
	}

}
