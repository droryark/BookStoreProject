package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class BookStoreRunner {
    public static void main(String[] args) {

        Gson gson = new Gson();
        try {
            JsonParser jsonParser = gson.fromJson(new String(Files.readAllBytes(Paths.get(args[0]))), JsonParser.class);
            Inventory inventory = Inventory.getInstance();
            inventory.load(jsonParser.initialInventory);
            ResourcesHolder.getInstance().load(jsonParser.initialResources[0].vehicles);
            jsonParser.services.setCustomers();
            jsonParser.services.start();
            jsonParser.services.toPrints(inventory, args[1], args[2], args[3], args[4]);
            // check files

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
