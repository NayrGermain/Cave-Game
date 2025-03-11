package model.observers;

import java.awt.Point;

public interface TileInteractionObserver {
    void onTileClicked(Point tilePosition, String direction);
}
