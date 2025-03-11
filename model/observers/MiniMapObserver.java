package model.observers;

import model.DTO.MiniMapDTO;

public interface MiniMapObserver {
    void onTileChange(MiniMapDTO info);
}