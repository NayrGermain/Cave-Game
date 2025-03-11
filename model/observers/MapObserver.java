package model.observers;

import model.DTO.MapDTO;

public interface MapObserver {
    void onTileChange(MapDTO mapData);
}
