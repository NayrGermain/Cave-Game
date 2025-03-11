package model.Items;

import java.util.List;
import java.util.Random;

import model.Constants;

public class IronOre extends Ores {

    public IronOre() {
        super("Iron", OreState.FULL, 30, 30,
                List.of(
                        Constants.WOODEN_PICKAXE,
                        Constants.IRON_PICKAXE,
                        Constants.SILVER_PICKAXE,
                        Constants.GOLD_PICKAXE),
                new Random().nextInt(20), OreStatus.AVAILABLE,
                0);
        this.quantity = Math.max(this.quantity, 10); // On s'assure que la quantité est au moins de 10
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
        return Math.max(new Random().nextInt(20), 10); // Retourne à la classe mère la quantité régénérée
    }
}
