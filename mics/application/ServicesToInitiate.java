package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.services.*;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class ServicesToInitiate implements Serializable {

    public TimerFromInput time;
    public int selling;
    public int inventoryService;
    public int logistics;
    public int resourcesService;
    public Customer[] customers;
    public Thread[] array;
    private CountDownLatch count;
    public HashMap<Integer,Customer> CustomerHash;


    public void setCustomers(){
        for (int i=0; i<customers.length; i++)
            customers[i].setCustomer();
    }

    public void start() {
        int numOfThreads = selling + inventoryService + logistics + resourcesService + customers.length, counter=0;
        this.count = new CountDownLatch(numOfThreads);
        array = new Thread[numOfThreads];

        for (int i = 1; i <= selling; i++) {
            Runnable seller = new SellingService("SellingService" + i, count);
            Thread thread = new Thread(seller);
            thread.start();
            array[counter] = thread;
            counter++;

        }
        for (int i = 1; i <= inventoryService; i++) {
            Runnable inventSer = new InventoryService("inventoryService" + i, count);
            Thread thread = new Thread(inventSer);
            thread.start();
            array[counter] = thread;
            counter++;

        }
        for (int i = 1; i <= logistics; i++) {
            Runnable logSer = new LogisticsService("LogisticsService" + i, count);
            Thread thread = new Thread(logSer);
            thread.start();
            array[counter] = thread;
            counter++;

        }
        for (int i = 1; i <= resourcesService; i++) {
            Runnable ResSer = new ResourceService("ResourceService" + i, count);
            Thread thread = new Thread(ResSer);
            thread.start();
            array[counter] = thread;
            counter++;
        }
        for (int i=1; i<=customers.length;i++){
            Runnable webAPI = new APIService("APIService" +i, customers[i-1],count);
            Thread thread = new Thread(webAPI);
            thread.start();
            array[counter] = thread;
            counter++;
        }

        Runnable timeService = new TimeService(time.getSpeed(), time.getDuration());
        Thread timeThread = new Thread(timeService);
        try {
            count.await();
        } catch (InterruptedException ignored) {
        }
        timeThread.start();
        try {
            timeThread.join();
            for (int i=0; i<array.length; i++)
                array[i].join();
        }
        catch (InterruptedException ignore){}
    }

    public void toPrints (Inventory inventory, String a, String b, String c, String d){
        CustomerHash=new HashMap<>();
        for (Customer customer : customers) {
            CustomerHash.put(customer.getId(), customer);
        }
        MoneyRegister moneyRegister = MoneyRegister.getInstance();
        moneyRegister.printOrderReceipts(c);
        inventory.printInventoryToFile(b);
        try (FileOutputStream file = new FileOutputStream(a)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(file)) {
                objectOutputStream.writeObject(CustomerHash);
                objectOutputStream.close();
                file.close();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: "+ c);
        }catch (IOException e) {
            System.out.println("Error Writing file: '" +c+"'");
        }
        try (FileOutputStream file = new FileOutputStream(d)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(file)) {
                objectOutputStream.writeObject(moneyRegister);
                objectOutputStream.close();
                file.close();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: "+ d);
        }catch (IOException e) {
            System.out.println("Error Writing file: '" +d+"'");
        }
    }
}
