package model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Items.OreInstance;

public class Tile {
    // VARIABLES
    private boolean northOpen, southOpen, eastOpen, westOpen; // Ouvertures
    private boolean northBlocked, southBlocked, eastBlocked, westBlocked;
    // Ouvertures bloquées entre les zones
    private int[][] grid; // Grille de la tuile, contient une valeur representant le type de la case
    private boolean isExplored; // Indique si la tuile a été explorée par le joueur
    private boolean isStart; // Indique si la tuile est le point de départ
    private boolean isExit; // Indique si la tuile est le point de sortie
    private String exitDirection; // Direction de la sortie
    private int zoneId; // Identifiant pour définir le biome de la tuile (et la logique qui va avec)
    private List<OreInstance> mineralInstances = new ArrayList<>(); // Liste des minerais sur la tuile indiquant leur
                                                                    // position
    Map<OreInstance, Rectangle> mineralRectangles = new HashMap<>();
    Map<String, Rectangle> openingsRectangles = new HashMap<>();
    private Map<String, BlockedDoor> doorRequirements = new HashMap<>();

    // TODO : -medium, low priority- ajouter un type de salle

    // CONSTRUCTOR
    public Tile() {
        // pas d'ouverture initialement
        this.northOpen = false;
        this.southOpen = false;
        this.eastOpen = false;
        this.westOpen = false;
        this.zoneId = 0;

        this.isExplored = false;
        this.isStart = false;
        this.isExit = false;
        this.exitDirection = null;

        this.grid = null;

        this.doorRequirements.put("NORTH", null);
        this.doorRequirements.put("SOUTH", null);
        this.doorRequirements.put("EAST", null);
        this.doorRequirements.put("WEST", null);
    }

    // METHODS
    // Générer un layout aléatoire propre à la tuile
    public void generateLayout(int gridSize) {
        // Les constates definisses le type d'asset à charger
        grid = new int[gridSize][gridSize];

        // Définir les bordures de la grille
        for (int i = 0; i < gridSize; i++) {
            grid[0][i] = Constants.TOP_BORDER; // Bordure du haut
            grid[gridSize - 1][i] = Constants.BOTTOM_BORDER; // Bordure du bas
            grid[i][0] = Constants.LEFT_BORDER; // Bordure de gauche
            grid[i][gridSize - 1] = Constants.RIGHT_BORDER; // Bordure de droite
            // coins
            grid[0][0] = Constants.TOP_LEFT_BORDER;
            grid[0][gridSize - 1] = Constants.TOP_RIGHT_BORDER;
            grid[gridSize - 1][0] = Constants.BOTTOM_LEFT_BORDER;
            grid[gridSize - 1][gridSize - 1] = Constants.BOTTOM_RIGHT_BORDER;
        }

        // Remplir l'intérieur de la grille avec du sol
        for (int i = 1; i < gridSize - 1; i++) {
            for (int j = 1; j < gridSize - 1; j++) {
                grid[i][j] = Constants.FLOOR;
            }
        }

        // TODO : - easy, low priority- améliorer la logique pour les obstacles +
        // variété
        // Ajouter des obstacles aléatoirement
        Random rand = new Random();
        for (int i = 1; i < gridSize - 1; i++) {
            for (int j = 1; j < gridSize - 1; j++) {
                if (rand.nextDouble() < 0.1) { // 10% chance de mettre un obstacle
                    grid[i][j] = Constants.OBSTACLE;
                }
            }
        }

        // Créer des ouvertures selon les booléens de la tuile
        if (isNorthOpen()) {
            grid[0][gridSize / 2] = Constants.NORTH_OPENING; // Ouverture au nord
        }
        if (isSouthOpen()) {
            grid[gridSize - 1][gridSize / 2] = Constants.SOUTH_OPENING; // Ouverture au sud
        }
        if (isEastOpen()) {
            grid[gridSize / 2][gridSize - 1] = Constants.EAST_OPENING; // Ouverture à l'est
        }
        if (isWestOpen()) {
            grid[gridSize / 2][0] = Constants.WEST_OPENING; // Ouverture à l'ouest
        }
    }

    // Méthode pour obtenir les rectangles des minerais pour les events (clic,
    // hover, etc.)
    public Map<OreInstance, Rectangle> getMineralRectangles(int segmentSizeWidth, int segmentSizeHeight) {
        mineralRectangles.clear();
        for (OreInstance oreInstance : mineralInstances) {
            Point position = oreInstance.getPosition(); // Récupère la position du minerai sur la tuile
            // Utilise les dimensions dynamiques pour x, y, width, et height
            int x = position.x * segmentSizeWidth;
            int y = position.y * segmentSizeHeight;
            Rectangle mineralRect = new Rectangle(x, y, segmentSizeWidth, segmentSizeHeight);
            mineralRectangles.put(oreInstance, mineralRect);
        }
        return mineralRectangles;
    }

    // Même chose que pour les minerais, mais pour les ouvertures
    public Map<String, Rectangle> getOpeningsRectangles(int segmentSizeWidth, int segmentSizeHeight) {
        openingsRectangles.clear();
        int halfWidth = segmentSizeWidth * Constants.LAYOUT_NUMBER_OFCUT / 2;
        int halfHeight = segmentSizeHeight * Constants.LAYOUT_NUMBER_OFCUT / 2;
        if (isNorthOpen()) {
            openingsRectangles.put("NORTH",
                    new Rectangle(halfWidth - segmentSizeWidth / 2, 0, segmentSizeWidth, segmentSizeHeight));
        }
        if (isSouthOpen()) {
            openingsRectangles.put("SOUTH", new Rectangle(halfWidth - segmentSizeWidth / 2,
                    (Constants.LAYOUT_NUMBER_OFCUT - 1) * segmentSizeHeight, segmentSizeWidth, segmentSizeHeight));
        }
        if (isEastOpen()) {
            openingsRectangles.put("EAST", new Rectangle((Constants.LAYOUT_NUMBER_OFCUT - 1) * segmentSizeWidth,
                    halfHeight - segmentSizeHeight / 2, segmentSizeWidth, segmentSizeHeight));
        }
        if (isWestOpen()) {
            openingsRectangles.put("WEST",
                    new Rectangle(0, halfHeight - segmentSizeHeight / 2, segmentSizeWidth, segmentSizeHeight));
        }
        return openingsRectangles;
    }

    // Récupérer la position de la porte dans la grille
    public Point getGridIndexForDirection(String direction) {
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
        return new Point(0, 0); // default in case of unexpected direction
    }

    // GETTERS & SETTERS
    public int getGridSize() {
        return grid.length;
    }

    public void setNorthOpen(boolean open) {
        this.northOpen = open;
    }

    public void setSouthOpen(boolean open) {
        this.southOpen = open;
    }

    public void setEastOpen(boolean open) {
        this.eastOpen = open;
    }

    public void setWestOpen(boolean open) {
        this.westOpen = open;
    }

    public boolean isNorthOpen() {
        return northOpen;
    }

    public boolean isSouthOpen() {
        return southOpen;
    }

    public boolean isEastOpen() {
        return eastOpen;
    }

    public boolean isWestOpen() {
        return westOpen;
    }

    public int[][] getLayout() {
        return grid;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public void setExplored(boolean explored) {
        isExplored = explored;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }

    public void setGridIndexValue(int x, int y, int value) {
        grid[x][y] = value;
    }

    public int getGridIndexValue(int x, int y) {
        return grid[x][y];
    }

    public void setZoneLevel(int zoneId) {
        this.zoneId = zoneId;
    }

    public int getZoneLevel() {
        return zoneId;
    }

    // Associer une tuile à un minerais
    public void addMineralInstance(OreInstance mineralInstance) {
        this.mineralInstances.add(mineralInstance);
    }

    public List<OreInstance> getMineralInstances() {
        return mineralInstances;
    }

    public String getNotBlockedOpen() {

        List<String> openDirections = new ArrayList<>();

        if (isNorthOpen() && !isNorthBlocked()) {
            openDirections.add("NORTH");
        }
        if (isSouthOpen() && !isSouthBlocked()) {
            openDirections.add("SOUTH");
        }
        if (isEastOpen() && !isEastBlocked()) {
            openDirections.add("EAST");
        }
        if (isWestOpen() && !isWestBlocked()) {
            openDirections.add("WEST");
        }

        // Récupérer une position aléatoire parmis celle trouvée dans openDirection
        if (!openDirections.isEmpty()) {
            Random rand = new Random();
            return openDirections.get(rand.nextInt(openDirections.size()));
        }

        return null;
    }

    // setter d'entrée bloqué
    public void setDoorBlocked(String direction, boolean isBlocked) {
        switch (direction.toUpperCase()) {
            case "NORTH":
                this.northBlocked = isBlocked;
                break;
            case "SOUTH":
                this.southBlocked = isBlocked;
                break;
            case "EAST":
                this.eastBlocked = isBlocked;
                break;
            case "WEST":
                this.westBlocked = isBlocked;
                break;
            default:
                break;
        }
    }

    public boolean isNorthBlocked() {
        return northBlocked;
    }

    public boolean isSouthBlocked() {
        return southBlocked;
    }

    public boolean isEastBlocked() {
        return eastBlocked;
    }

    public boolean isWestBlocked() {
        return westBlocked;
    }

    public boolean isBlocked(String direction) {
        switch (direction.toUpperCase()) {
            case "NORTH":
                return isNorthBlocked();
            case "SOUTH":
                return isSouthBlocked();
            case "EAST":
                return isEastBlocked();
            case "WEST":
                return isWestBlocked();
            default:
                return false;
        }
    }

    public void setDoorRequirements(String direction, BlockedDoor door) {
        doorRequirements.put(direction, door);
    }

    public BlockedDoor getDoorRequirements(String direction) {
        return doorRequirements.get(direction);
    }

    public int getDoorMaxDuration(String direction) {
        return doorRequirements.get(direction).getMaxDuration();
    }

    public void setExitDirection(String direction) {
        this.exitDirection = direction;
    }

    public String getExitDirection() {
        return exitDirection;
    }
}
