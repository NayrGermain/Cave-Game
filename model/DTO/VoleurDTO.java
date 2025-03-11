package model.DTO;

import java.awt.Point;
import java.util.Map;

// Sert à notifier à la fois la position et pixel du mineur et la position des mineurs sur la map
public class VoleurDTO {

    // VARIABLES
    private int id;
    private Point positionMap;
    private Point positionPixel;
    private Map<Integer, String> states;
    private int maxCaptureTime;
    private int captureTime;

    // CONSTRUCTEUR
    public VoleurDTO(int id, Point positionMap, Point positionPixel, Map<Integer, String> states, int maxCaptureTime,
            int captureTime) {
        this.id = id;
        this.positionMap = positionMap;
        this.positionPixel = positionPixel;
        this.states = states;
        this.maxCaptureTime = maxCaptureTime;
        this.captureTime = captureTime;
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

    public int getMaxCaptureTime() {
        return maxCaptureTime;
    }

    public int getCaptureTime() {
        return captureTime;
    }

}
