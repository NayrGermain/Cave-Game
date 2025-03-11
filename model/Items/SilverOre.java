package model.Items;

import java.util.List;
import java.util.Random;

import model.Constants;

public class SilverOre extends Ores {

    public SilverOre() {
        super("Silver", OreState.FULL, 60, 60,
                List.of(
                        Constants.IRON_PICKAXE,
                        Constants.SILVER_PICKAXE,
                        Constants.GOLD_PICKAXE),
                new Random().nextInt(25),
                OreStatus.AVAILABLE, 0);
        this.quantity = Math.max(this.quantity, 15); // On s'assure que la quantité est au moins de 15
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
        return Math.max(new Random().nextInt(15), 8); // Retourne à la classe mère la quantité régénérée
    }
}
