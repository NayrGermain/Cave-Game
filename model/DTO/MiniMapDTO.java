package model.DTO;

import java.awt.Point;
import java.util.Map;

public class MiniMapDTO {
    // VARIABLES
    private Point currentTile;
    private boolean[][] exploredTiles;
    private Point startPoint;
    private Point exitPoint;
    private boolean[][][] walls; // [x][y][cardinalité ouverture, ouvert ou fermée]
    private Map<Integer, Point> minerPositions;
    private Map<Integer, Point> voleurPositions;

    // CONSTRUCTEUR
    public MiniMapDTO(Point currentTile, boolean[][] exploredTiles, Point startPoint, Point exitPoint,
            boolean[][][] walls, Map<Integer, Point> minerPositions, Map<Integer, Point> voleurPositions) {
        this.currentTile = currentTile;
        this.exploredTiles = exploredTiles;
        this.startPoint = startPoint;
        this.exitPoint = exitPoint;
        this.walls = walls;
        this.minerPositions = minerPositions;
        this.voleurPositions = voleurPositions;
    }

    // GETTERS
    public Point getCurrentTile() {
        return currentTile;
    }

    public boolean[][] getExploredTiles() {
        return exploredTiles;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getExitPoint() {
        return exitPoint;
    }

    public boolean[][][] getWalls() {
        return walls;
    }

    public Map<Integer, Point> getMinerPositions() {
        return minerPositions;
    }

    public Map<Integer, Point> getVoleurPositions() {
        return voleurPositions;
    }

}
