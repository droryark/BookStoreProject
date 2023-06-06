package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class CreditCard implements Serializable {
    public int number;
    public int amount;

    public CreditCard(int am, int num){
        this.amount=am;
        this.number=num;
    }
    public int getAmount() {
        return amount;
    }

    public int getNumber() {
        return number;
    }
}
