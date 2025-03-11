package model.observers;

import java.awt.Point;

public interface TileHoverOreObserver {
    void onTileHoverOre(Point tilePosition, boolean isHovering, int id, String type);
}