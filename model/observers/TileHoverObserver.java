package model.observers;

import java.awt.Point;

public interface TileHoverObserver {
    void onTileHoverOpening(Point tilePosition, boolean isHovering, String hoverDirection);
}