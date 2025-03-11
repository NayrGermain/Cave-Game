package model.DTO;

import model.Items.OreInfo;

public class MineralDetailsDTO {
    // ATTRIBUTES
    private int id;
    private OreInfo oreInfo;

    // CONSTRUCTOR
    public MineralDetailsDTO(int id, OreInfo oreInfo) {
        this.id = id;
        this.oreInfo = oreInfo;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public OreInfo getOreInfo() {
        return oreInfo;
    }
}
