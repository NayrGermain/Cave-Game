package model.observers;

public interface StorageObserver {
    void onStorageChange(String type, int amount);
}
