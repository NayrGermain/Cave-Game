package model.characters;

import java.awt.Point;

import model.Constants;

public class MoveMinerAction extends MinerAction {
    public MoveMinerAction(Miner mineur, Point destination) {
        super(mineur, destination);
    }

    @Override
    public void execute() {
        // marge de distance
        double distance = destination.distance(mineur.getPositionPixel());
        if (mineur.getState().equals("WALKING") && distance <= Constants.MOVE_THRESHOLD) {
            mineur.setState("IDLE");
            mineur.setPositionPixel(destination);
        } else {
            mineur.deplacerVers(destination);
        }
    }
}
