package model.DTO;

import java.awt.Point;
import java.util.Map;

import model.Items.OreInfo;

public class OresDTO {
    private Map<Point, OreInfo> mineralPositions;

    public OresDTO(Map<Point, OreInfo> mineralPositions) {
        this.mineralPositions = mineralPositions;
    }

    // Getters
    public Map<Point, OreInfo> getMineralPositions() {
        return mineralPositions;
    }
}