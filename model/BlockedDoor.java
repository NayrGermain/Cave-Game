package model;

import java.awt.Point;
import java.util.List;

public class BlockedDoor {

    // VARIABLES
    private static int nextId = 0; // Compteur global pour générer des IDs uniques
    private final int id; // Identifiant unique de l'instance
    private String direction;
    private List<String> requirements;
    private int maxDuration;
    private int unlockingDuration;
    private Tile tile; // Ajout de la tuile associée à la porte
    private Tile symmetricDoor; // Ajout de la porte symétrique
    private Point positionDoor; // Ajout de la position de la porte sur la tuile
    private Point positionMap; // Ajout de la position de la porte sur la carte

    // CONSTRUCTOR
    public BlockedDoor(String direction, List<String> requirements, int unlockingDuration, Tile tile,
            Tile symmetricDoor, Point position,
            Point positionMap) {
        this.id = nextId++; // Attribuer un ID unique à chaque nouvelle instance
        this.direction = direction;
        this.requirements = requirements;
        this.maxDuration = unlockingDuration;
        this.unlockingDuration = unlockingDuration;
        this.tile = tile;
        this.symmetricDoor = symmetricDoor;
        this.positionDoor = position;
        this.positionMap = positionMap;
    }

    // METHODS
    public boolean unlock() {
        if (this.unlockingDuration > 0) {
            this.unlockingDuration -= 1;
        }
        return this.unlockingDuration == 0;
    }

    // Débloquer la porte et sa symétrique
    public void unlockDoor(String direction) {
        Point doorPos = getGridIndexForDirection(direction); // Récupérer la position de la porte dans la grille
        tile.setDoorBlocked(direction, false); // Débloquer la porte
        updateDoorAsset(direction, doorPos, tile); // mettre a jour l'asset basé sur sa direction

        // Débloquer la porte symétrique si elle existe
        if (symmetricDoor != null) {
            String oppositeDirection = getOppositeDirection(direction);
            Point symmetricDoorPos = getGridIndexForDirection(oppositeDirection);
            symmetricDoor.setDoorBlocked(oppositeDirection, false);
            updateDoorAsset(oppositeDirection, symmetricDoorPos, symmetricDoor);
        }
    }

    // Récupérer la position de la porte dans la grille
    private Point getGridIndexForDirection(String direction) {
        switch (direction) {
            case "NORTH":
                return new Point(0, Constants.LAYOUT_NUMBER_OFCUT / 2);
            case "SOUTH":
                return new Point(Constants.LAYOUT_NUMBER_OFCUT - 1, Constants.LAYOUT_NUMBER_OFCUT / 2);
            case "WEST":
                return new Point(Constants.LAYOUT_NUMBER_OFCUT / 2, 0);
            case "EAST":
                return new Point(Constants.LAYOUT_NUMBER_OFCUT / 2, Constants.LAYOUT_NUMBER_OFCUT - 1);
        }
        return new Point(0, 0); 
    }

    private String getOppositeDirection(String direction) {
        switch (direction) {
            case "NORTH":
                return "SOUTH";
            case "SOUTH":
                return "NORTH";
            case "WEST":
                return "EAST";
            case "EAST":
                return "WEST";
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    // Mettre a jour l'asset de la porte basé sur la direciton de celle ci
    private void updateDoorAsset(String direction, Point doorPos, Tile tile) {
        int assetValue;
        switch (direction) {
            case "NORTH":
                assetValue = Constants.NORTH_OPENING;
                break;
            case "SOUTH":
                assetValue = Constants.SOUTH_OPENING;
                break;
            case "EAST":
                assetValue = Constants.EAST_OPENING;
                break;
            case "WEST":
                assetValue = Constants.WEST_OPENING;
                break;
            default:
                return; 
        }
        tile.setGridIndexValue(doorPos.x, doorPos.y, assetValue);
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public int getUnlockingDuration() {
        return unlockingDuration;
    }

    // SETTERS
    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public void setUnlockingDuration(int unlockingDuration) {
        this.unlockingDuration = unlockingDuration;
    }

    public Tile getTile() {
        return tile;
    }

    public Point getPositionDoor() {
        return positionDoor;
    }

    public Point getPositionMap() {
        return positionMap;
    }
}
