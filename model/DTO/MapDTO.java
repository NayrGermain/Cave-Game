package model.DTO;

import java.awt.Point;
import java.util.Map;

public class MapDTO {
    // VARIABLES
    private Point currentTile;
    private int[][] layout;

    private Map<Integer, Point> miners;
    private Map<Integer, Point> voleurs;

    // CONSTRUCTOR
    public MapDTO(Point currentTile, int[][] exploredTiles, Map<Integer, Point> miners, Map<Integer, Point> voleurs) {
        this.currentTile = currentTile;
        this.layout = exploredTiles;
        this.miners = miners;
        this.voleurs = voleurs;
    }

    // Getters
    public Point getCurrentTile() {
        return currentTile;
    }

    public int[][] getTilesType() {
        return layout;
    }

    public Map<Integer, Point> getMiners() {
        return miners;
    }

    public Map<Integer, Point> getVoleurs() {
        return voleurs;
    }
}
