package view;

import model.Constants;

public class TrRedessine extends Thread {
    // VARIABLE
    private MapPanel mapPanel;
    private Boolean isRunning;

    // CONSTRUCTEUR
    public TrRedessine(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    // RUN
    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            mapPanel.repaint(); // refresh les animation IDLE des mineurs
            try {
                Thread.sleep(Constants.DELAY);
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    }

    // Interrupt sans interrompre le thread
    public void stopRunning() {
        isRunning = false;
    }

    // Rejouer une partie
    public void restart() {
        isRunning = true;
    }

}
