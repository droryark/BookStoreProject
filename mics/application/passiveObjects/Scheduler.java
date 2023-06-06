package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class Scheduler implements Serializable {
    private String bookTitle;
    private int tick;
    public Scheduler(String name, int tick){
        this.bookTitle = name;
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }

    public String getName() {
        return bookTitle;
    }
}
