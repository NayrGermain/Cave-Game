package model.characters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.BlockedDoor;
import model.Constants;
import model.DTO.DoorDetailsDTO;
import model.DTO.MinerDTO;
import model.observers.MinerOpenDoorObserver;

public class MineOpeningAction extends MinerAction {
    // VARIABLES
    private BlockedDoor blockedDoor;

    private List<MinerOpenDoorObserver> observers;

    // CONSTRUCTOR
    public MineOpeningAction(Miner mineur, Point destination, BlockedDoor blockedDoor) {
        super(mineur, destination);
        this.blockedDoor = blockedDoor;

        this.observers = new ArrayList<>();
    }

    // METHODS
    @Override
    public void execute() {
        // Vérifiez si le mineur est suffisamment proche de sa destination pour
        // debloquer la porte
        double distance = destination.distance(mineur.getPositionPixel());
        if (mineur.getState().equals("TO_BLOCKED_DOOR") && distance <= Constants.MOVE_THRESHOLD) {
            // Le mineur est arrivé sur la porte on passe les statut du minerai/mineur en
            // mode minage
            mineur.setState("UNLOCKING_DOOR");
            mineur.setPositionPixel(destination);
            notifyUnlockStarted();
        } else if (mineur.getState().equals("TO_BLOCKED_DOOR")) {
            // Déplacez le mineur vers la destination
            mineur.deplacerVers(destination);
        } else if (mineur.getState().equals("UNLOCKING_DOOR")) {
            // On mine la porte
            boolean isUnlocked = blockedDoor.unlock();
            notifyUnlockProgress();
            if (isUnlocked) {
                mineur.setState("IDLE");
                blockedDoor.unlockDoor(blockedDoor.getDirection());
                notifyUnlockCompleted();
            }
        } else if (mineur.getState().equals("IDLE")) {
            cancelUnlocking("IDLE");
        } else if (mineur.getState().equals("SELECTED")) {
            cancelUnlocking("SELECTED");
        }
    }

    // Méthode pour annuler le debloquage
    private void cancelUnlocking(String state) {
        mineur.setState(state);
        notifyUnlockCompleted();
    }

    // GETTERS
    public Miner getMineur() {
        return mineur;
    }

    public BlockedDoor getBlockedDoor() {
        return blockedDoor;
    }

    // SETTERS
    public void setMineur(Miner mineur) {
        this.mineur = mineur;
    }

    public void setBlockedDoor(BlockedDoor blockedDoor) {
        this.blockedDoor = blockedDoor;
    }

    // OBSERVERS
    public void attachObserver(MinerOpenDoorObserver observer) {
        observers.add(observer);
    }

    public void notifyUnlockStarted() {
        MinerDTO minerData = buildMinerDTO();
        DoorDetailsDTO doorData = buildDoorDTO();
        for (MinerOpenDoorObserver observer : observers) {
            observer.onUnlockStart(
                    doorData,
                    minerData);
        }
    }

    public void notifyUnlockProgress() {
        DoorDetailsDTO doorData = buildDoorDTO();
        MinerDTO minerData = buildMinerDTO();
        for (MinerOpenDoorObserver observer : observers) {
            observer.onUnlockProgress(doorData, minerData);
        }
    }

    public void notifyUnlockCompleted() {
        MinerDTO minerData = buildMinerDTO();
        DoorDetailsDTO doorData = buildDoorDTO();
        for (MinerOpenDoorObserver observer : observers) {
            observer.onUnlockCompleted(doorData, minerData);
        }
    }

    private MinerDTO buildMinerDTO() {
        Map<Integer, String> states = new HashMap<>();
        states.put(mineur.getId(), mineur.getState());

        return new MinerDTO(
                mineur.getId(),
                mineur.getPositionMap(),
                mineur.getPositionPixel(),
                states);
    }

    private DoorDetailsDTO buildDoorDTO() {
        return new DoorDetailsDTO(
                blockedDoor.getId(),
                blockedDoor.getMaxDuration(),
                blockedDoor.getUnlockingDuration(),
                blockedDoor.getPositionMap(),
                blockedDoor.getPositionDoor());
    }

}
