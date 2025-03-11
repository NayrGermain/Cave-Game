package model.DTO;

import java.awt.Point;
import java.util.Map;

// Sert à notifier à la fois la position et pixel du mineur et la position des mineurs sur la map
public class MinerDTO {

    // VARIABLES
    private int id;
    private Point positionMap;
    private Point positionPixel;
    private Map<Integer, String> states;

    // CONSTRUCTEUR
    public MinerDTO(int id, Point positionMap, Point positionPixel, Map<Integer, String> states) {
        this.id = id;
        this.positionMap = positionMap;
        this.positionPixel = positionPixel;
        this.states = states;
    }

    // GETTER
    public int getId() {
        return id;
    }

    public Point getPositionMap() {
        return positionMap;
    }

    public Point getPositionPixel() {
        return positionPixel;
    }

    public Map<Integer, String> getState() {
        return states;
    }

}
