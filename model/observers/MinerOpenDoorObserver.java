package model.observers;

import model.DTO.DoorDetailsDTO;
import model.DTO.MinerDTO;

public interface MinerOpenDoorObserver {
    void onUnlockStart(DoorDetailsDTO doorData, MinerDTO minerData);

    void onUnlockProgress(DoorDetailsDTO doorData, MinerDTO minerData);

    void onUnlockCompleted(DoorDetailsDTO doorData, MinerDTO minerData);
}
