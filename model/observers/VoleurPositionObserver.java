package model.observers;

import model.DTO.VoleurDTO;

public interface VoleurPositionObserver {
    void onVoleurMove(int id, VoleurDTO voleurs);
}