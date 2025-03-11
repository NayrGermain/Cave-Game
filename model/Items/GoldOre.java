package model.Items;

import java.util.List;
import java.util.Random;

import model.Constants;

public class GoldOre extends Ores {
    // CONSTRUCTOR
    public GoldOre() {
        super("Gold", OreState.FULL, 120, 120, List.of(Constants.SILVER_PICKAXE, Constants.GOLD_PICKAXE),
                new Random().nextInt(10),
                OreStatus.AVAILABLE, 0);
        this.quantity = Math.max(this.quantity, 5); // On s'assure que la quantité est au moins de 5
    }

    // GETTERS & SETTERS
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // METHODES
    @Override
    protected int getRegenerateQuantity() {
        return Math.max(new Random().nextInt(10), 5); // Retourne à la classe mère la quantité régénérée
    }

}
