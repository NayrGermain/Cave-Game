package model.observers;

import model.DTO.MinerDTO;
import model.DTO.VoleurDTO;

public interface CatchingThiefObserver {
    void onCaptureStarted(MinerDTO minerData, VoleurDTO thiefData, int oreId);

    void onCaptureProgress(MinerDTO minerData, VoleurDTO thiefData);

    void onCaptureCompleted(MinerDTO minerData, VoleurDTO thiefData, boolean isRegen, int oreId, int drop);
}
