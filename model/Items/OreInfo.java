package model.Items;

import java.awt.Point;

// Structure de données d'un minerais pour le DTO afin de découpler la vue du modèle
public class OreInfo {
    private int id;
    private String type;
    private int quantity;
    private Point position;
    private String state;
    private int harvestTime;

    public OreInfo(int id, String type, int quantity, Point position, String state, int harvestTime) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.position = position;
        this.state = state;
        this.harvestTime = harvestTime;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public Point getPosition() {
        return position;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getHarvestTime() {
        return harvestTime;
    }
}
