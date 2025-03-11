package controller;

import model.Storage;
import view.Market.InfosMarketPane;

public class DecreaseMoney extends Thread {
    private Storage storage;
    private int compteur;
    private final int delay=10;
    public int k = 0;
    public int ressourceType; // 0-iron 1-silver 2-gold 3-money
    public InfosMarketPane paneToRepaint;

    public DecreaseMoney() {
        this(3);
    }
    public DecreaseMoney(int ressourceType) {
        this.storage = Storage.getInstance();
        this.ressourceType = ressourceType;
    }

    @Override
    public void run() {

        while (k < compteur) {
            try {
                switch (ressourceType)
                {
                    case 0 :storage.removeStorage("iron", 1); break;
                    case 1 :storage.removeStorage("silver", 1); break;
                    case 2 :storage.removeStorage("gold", 1); break;
                    case 3 :storage.removeStorage("money", 1); break;

                }


                // paneToRepaint.repaint();
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            k++;
        }
    }

    public void initiate(int x) {
        compteur = x;
    }
}
