package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
output1:
HashMap<Integer,Customer> - which contains all the customers in the system. The key is the id
of the customer and the value is the reference of the customer instance.

output2:
HashMap<String,Integer> which contains all the books in the inventory and their remained
amount. The key is the books title and the value is the remained amount.

output3:
List<OrderReceipt> which holds all the order receipts which are currently in the MoneyRegister.

output4: The MoneyRegister object
 */



public class CheckFiles {
    public static void main(String[] args) {
        List<OrderReceipt> lst = new ArrayList<>();
        try (FileInputStream file = new FileInputStream("output3")) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(file)) {
                lst = (List<OrderReceipt>) objectInputStream.readObject();
                objectInputStream.close();
                file.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (OrderReceipt receipt : lst){
            System.out.println(receipt);
        }


    }
}
