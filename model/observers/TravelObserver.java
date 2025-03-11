package model.observers;

import java.awt.Point;

public interface TravelObserver {
    void onMinerTravel(int id, Point mapPosition, Point positionPixel);

    void onVoleurTravel(int id, Point mapPosition, Point positionPixel);
}
