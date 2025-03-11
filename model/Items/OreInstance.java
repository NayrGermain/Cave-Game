package model.Items;

import java.awt.Point;

// Classe pour représenter une instance de minerai avec sa position
public class OreInstance {
    private static int nextId = 0; // Compteur global pour générer des IDs uniques
    private final int id; // Identifiant unique de l'instance
    private Ores ore;
    private Point position;
    private String isTargeted;

    public OreInstance(Ores ore, Point position) {
        this.id = nextId++; // Attribuer un ID unique à chaque nouvelle instance
        this.ore = ore;
        this.position = position;
        this.isTargeted = null;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public Ores getOre() {
        return ore;
    }

    public Point getPosition() {
        return position;
    }

    public String isTargeted() {
        return isTargeted;
    }

    public void setTargeted(String targeted) {
        isTargeted = targeted;
    }
}
