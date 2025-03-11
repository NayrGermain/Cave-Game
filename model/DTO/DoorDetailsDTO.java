package model.DTO;

import java.awt.Point;

public class DoorDetailsDTO {
    private int id;
    private int totalTime;
    private int remainingTime;
    private Point tilePosition; // Position de la tuile sur la carte
    private Point doorPosition; // Position locale de la porte sur la tuile

    public DoorDetailsDTO(int doorId, int totalTime, int remainingTime, Point tilePosition, Point doorPosition) {
        this.id = doorId;
        this.totalTime = totalTime;
        this.remainingTime = remainingTime;
        this.tilePosition = tilePosition;
        this.doorPosition = doorPosition;
    }

    public int getId() {
        return id;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public Point getTilePosition() {
        return tilePosition;
    }

    public Point getDoorPosition() {
        return doorPosition;
    }
}
