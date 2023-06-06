package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class DeliveryEvent implements Event<Object> {
    private Customer customer;
    public DeliveryEvent (Customer c){
        this.customer=c;
    }
    public Customer getCustomer (){
        return customer;
    }
}
