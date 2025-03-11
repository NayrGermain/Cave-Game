package model.characters;

import java.awt.Point;

// Classe mère des differentes actions que peut effectuer un mineur
public abstract class MinerAction {
    protected Miner mineur;
    protected Point destination;

    // CONSTRUCTOR
    public MinerAction(Miner mineur, Point destination) {
        this.mineur = mineur;
        this.destination = destination;
    }

    // METHODS
    public abstract void execute(); // Execute l'action définie par la classe fille
}
