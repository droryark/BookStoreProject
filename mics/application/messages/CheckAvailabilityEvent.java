package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailabilityEvent implements Event<Integer> {
    private String book;
    public CheckAvailabilityEvent(String book){this.book=book;}

    public String getBook() {
        return book;
    }
}
