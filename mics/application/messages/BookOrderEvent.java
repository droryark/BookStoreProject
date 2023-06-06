package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String book;
    private int tick;

    public BookOrderEvent(Customer c, String book, int tick){
        this.customer = c;
        this.book = book;
        this.tick = tick;
    }

    public Customer getCustomer() {
        return customer;
    }
    public String getBook() {
        return book;
    }
    public int getTick() { return tick;}
}
