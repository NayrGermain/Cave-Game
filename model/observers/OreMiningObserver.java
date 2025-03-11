package model.observers;

import model.DTO.MinerDTO;
import model.DTO.VoleurDTO;
import model.DTO.MineralDetailsDTO;

public interface OreMiningObserver {
    void onMiningStart(int oreId, MineralDetailsDTO oresData);

    void onMiningProgress(int oreId, MineralDetailsDTO oresData, MinerDTO minerData);

    void onMiningProgress(int oreId, MineralDetailsDTO oresData, VoleurDTO voleurData);

    void onMiningComplete(int oreId, MineralDetailsDTO oresData, boolean isRegen, MinerDTO minerData);

    void onMiningComplete(int oreId, MineralDetailsDTO oresData, boolean isRegen, VoleurDTO voleurData);

}
