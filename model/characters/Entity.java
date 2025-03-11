package model.characters;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class Entity {

    // VARIABLES
    protected static int countId = 0; // Compteur d'identifiant
    protected int id; // Identifiant unique
    protected Point positionMap; // Position actuelle sur la carte
    protected int moveSpeed; // Vitesse de déplacement
    protected double miningSpeed; // Coefficient multiplicateur de minage
    protected Rectangle hitbox; // Hitbox pour la détection de collision
    protected Point positionPixel; // Position actuelle en pixel sur la tuile

    // CONSTRUCTEUR
    public Entity(Point positionMap, int moveSpeed, double miningSpeed) {
        this.id = ++countId;
        this.positionMap = positionMap;
        this.moveSpeed = moveSpeed;
        this.miningSpeed = miningSpeed;
    }

    // GETTER

    public void setMapPosition(Point positionMap) {
        this.positionMap = positionMap;
    }

    // MEHTODES
    protected abstract void deplacerVers(Point destination);

}
