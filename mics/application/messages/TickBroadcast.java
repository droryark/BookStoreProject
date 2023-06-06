package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private int tick;
    private boolean last;

    public TickBroadcast(int tick, boolean last){
        this.tick=tick;
        this.last=last;
    }
    public int getTick(){
        return this.tick;
    }
    public boolean getLast() {
        return this.last;
    }
}
