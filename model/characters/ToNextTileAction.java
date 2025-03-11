package model.characters;

import java.awt.Point;
import java.util.List;

import model.Constants;
import model.observers.TravelObserver;

public class ToNextTileAction extends MinerAction {

    // VARIABLES
    private Point newMapPosition;
    private String direction;

    private List<TravelObserver> observers;

    // CONSTRUCTOR
    public ToNextTileAction(Miner mineur, Point destination, Point newMapPosition, String direction) {
        super(mineur, destination);
        this.newMapPosition = newMapPosition;
        this.direction = direction;
        this.observers = new java.util.ArrayList<>();
    }

    // METHODS
    public void execute() {
        // Vérifiez si le mineur est suffisamment proche de la porte
        double distance = destination.distance(mineur.getPositionPixel());
        if (mineur.getState().equals("TO_NEXT_TILE") && distance <= Constants.MOVE_THRESHOLD) {
            // Le mineur est arrivé proche de la destination on ajuste sa position
            mineur.setState("TRAVEL");
            mineur.setPositionPixel(destination);
        } else if (mineur.getState().equals("TO_NEXT_TILE")) {
            // Déplacez le mineur vers la destination
            mineur.deplacerVers(destination);
        } else if (mineur.getState().equals("TRAVEL")) {
            // Destination atteinte on change le mineur de tuile
            mineur.setState("IDLE");
            mineur.setMapPosition(newMapPosition);

            // Ajuster la position pixel du mineur sur la tuile suivante
            switch (direction) {
                case "NORTH":
                    // Positionner le mineur au sud de la tuile
                    mineur.setPositionPixel(new Point(destination.x, Constants.FRAME_HEIGHT - Constants.MARGIN));
                    break;
                case "SOUTH":
                    // Positionner le mineur au nord de la tuile
                    mineur.setPositionPixel(new Point(destination.x, Constants.MARGIN));
                    break;
                case "EAST":
                    // Positionner le mineur à l'ouest de la tuile
                    mineur.setPositionPixel(new Point(Constants.MARGIN, destination.y));
                    break;
                case "WEST":
                    // Positionner le mineur à l'est de la tuile
                    mineur.setPositionPixel(new Point(Constants.FRAME_WIDTH - Constants.MARGIN, destination.y));
                    break;
            }

            // Notifie la vue que le mineur a changé de tuile
            notifyTravelCompleted(mineur.getId(), mineur.positionMap, mineur.getPositionPixel());
        }
    }

    public void attachObserver(TravelObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TravelObserver observer) {
        observers.remove(observer);
    }

    public void notifyTravelCompleted(int id, Point mapPosition, Point positionPixel) {
        for (TravelObserver observer : observers) {
            observer.onMinerTravel(id, mapPosition, positionPixel);
        }
    }

}
