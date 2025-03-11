package model.observers;

public interface ActionFinishedObserver {

    void onFinishedAction(int minerId, String stateIDLE);

}
