package model.observers;

import model.DTO.MinerDTO;

public interface MinerPositionObserver {
    void onMinerMove(int id, MinerDTO miners);
}
