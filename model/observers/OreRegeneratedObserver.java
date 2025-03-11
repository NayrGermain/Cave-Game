package model.observers;

import model.DTO.MineralDetailsDTO;

public interface OreRegeneratedObserver {
    void onOreRegenerated(int instanceID, MineralDetailsDTO mineralDetails);
}
