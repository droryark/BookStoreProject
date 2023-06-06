package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TryPurchaseBookEvent implements Event<Boolean> {

    private String book;
    public TryPurchaseBookEvent(String b){
        this.book = b;
    }
    public String getBook() {
        return book;
    }
}
