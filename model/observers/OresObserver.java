package model.observers;

import model.DTO.OresDTO;

public interface OresObserver {
    void onOresChange(OresDTO oresData);
}
