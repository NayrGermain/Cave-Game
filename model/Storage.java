package model;

import java.util.ArrayList;
import java.util.List;

import model.observers.StorageObserver;

public class Storage {
    // SINGLETON PATTERN
    private static Storage instance;

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    // VARIABLES
    private int iron;
    private int gold;
    private int silver;
    private int money;
    // TODO: Add more resources

    private List<StorageObserver> observers = new ArrayList<>();

    // CONSTRUCTOR
    public Storage() {
        this.iron = 0;
        this.gold = 0;
        this.silver = 0;
        this.money =0;
    }

    // METHODS
    public void addStorage(String type, int amount) {
        type = type.toLowerCase();
        if (type.equals("iron")) {
            this.iron += amount;
            notifyObservers(type, iron);
        } else if (type.equals("gold")) {
            this.gold += amount;
            notifyObservers(type, gold);
        } else if (type.equals("silver")) {
            this.silver += amount;
            notifyObservers(type, silver);
        } else if (type.equals("money")) {
            this.money += amount;
            notifyObservers(type, money);
        }
    }

    public void removeStorage(String type, int amount) {
        type = type.toLowerCase();
        if (type.equals("iron")) {
            this.iron -= amount;
            notifyObservers(type, iron);
        } else if (type.equals("gold")) {
            this.gold -= amount;
            notifyObservers(type, gold);
        } else if (type.equals("silver")) {
            this.silver -= amount;
            notifyObservers(type, silver);
        } else if (type.equals("money")) {
            this.money -= amount;
            notifyObservers(type, money);
        }
    }

    public void resetStorage() {
        this.iron = 0;
        this.gold = 0;
        this.silver = 0;
        this.money = 0;
    }

    // GETTERS AND SETTERS
    public int getIron() {
        return this.iron;
    }

    public int getGold() {
        return this.gold;
    }

    public int getSilver() {
        return this.silver;
    }

    public int getMoney(){return this.money;}

    public void setIron(int amount) {
        this.iron = amount;
    }

    // OBSERVERS
    public void attachObserver(StorageObserver observer) {
        observers.add(observer);
    }

    protected void notifyObservers(String type, int amount) {
        for (StorageObserver observer : observers) {
            observer.onStorageChange(type, amount);
        }
    }
}
