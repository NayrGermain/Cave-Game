package model.characters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Constants;
import model.GameMap;
import model.Storage;
import model.DTO.MinerDTO;
import model.DTO.VoleurDTO;
import model.Items.OreInstance;
import model.observers.CatchingThiefObserver;

public class CatchThiefAction extends MinerAction {

    // VARIABLES
    private GameMap gameMap;
    private Voleur voleur;
    private Storage inventaire;
    private int drop;
    private OreInstance currentTarget;

    private List<CatchingThiefObserver> observers;

    public CatchThiefAction(Miner mineur, Point destination, Voleur voleur, GameMap gameMap) {
        super(mineur, destination);
        this.voleur = voleur;
        this.inventaire = Storage.getInstance(); // Récupérer le singleton de l'inventaire
        this.gameMap = gameMap; // Permet de supprimer le voleur de la map
        this.currentTarget = null;

        this.drop = 0;

        this.observers = new ArrayList<>();
    }

    @Override
    public void execute() {
        // Vérifiez si le mineur est suffisamment proche de sa destination pour miner
        double distance = destination.distance(mineur.getPositionPixel());
        if (mineur.getState().equals("TO_ENEMY") && distance <= Constants.MOVE_THRESHOLD * 2) {
            // Le mineur arrive au contact du voleur, début de la capture
            if (voleur.getTargetMineral() != null) {
                // On recupere l'instance qu'il miné avant que VoleurAction la libère
                currentTarget = voleur.getTargetMineral();
            }
            mineur.setState("CAPTURING");
            voleur.setState("CAPTURING");
            mineur.setPositionPixel(destination);

            notifyCaptureStarted();
        }
        // Déplacez le mineur vers la destination
        else if (mineur.getState().equals("TO_ENEMY")) {
            mineur.deplacerVers(destination);
        }
        // Capture en cours
        else if (mineur.getState().equals("CAPTURING")) {
            // On mine le minerai
            capture();

            notifyCaptureProgress();
        }
        // etat transition pour retirer definitivement le voleur apres avoir néttoyé ses
        // ref
        else if (mineur.getState().equals("REMOVE_THIEF")) {
            notifyCaptureCompleted();

            // Suppression de l'instance du voleur de la map
            gameMap.removeVoleurByID(voleur.getId());
            mineur.setState("IDLE");
        } else {
            cancelCapture();
        }
    }

    // METHODS
    // Capture le voleur
    private void capture() {
        // Tant que le voleur n'est pas capturé
        if (voleur.getState().equals("CAPTURING")) {
            // On capture le voleur
            drop = voleur.inCapture();

            // Si le voleur a été capturé, ajouté l'argent à l'inventaire du mineur
            if (drop > 0) {
                inventaire.addStorage("money", drop);
                mineur.setState("REMOVE_THIEF");
            }
        } else {
            cancelCapture();
        }
    }

    // Méthode pour annuler le minage
    private void cancelCapture() {
        // Vérifie si le minerais est encore en cours de minage
        if (voleur.getState().equals("CAPTURING")
                && (!mineur.getState().equals("CAPTURING") || !mineur.getState().equals("SELECTED"))) {

            // Notifier la vue que la capture a été annulée
            notifyCaptureCompleted();

            voleur.setState("TO_NEXT_TILE"); // le voleur fuis
            voleur.getClosestMineral().setTargeted(null); // rends dispo le minerai aux autres
            voleur.setTargetMineral(null); // reset le minerai ciblé
        }
    }

    // OBSERVER
    public void attachObserver(CatchingThiefObserver observer) {
        observers.add(observer);
    }

    private void notifyCaptureStarted() {
        MinerDTO mineurData = buildMinerDTO();
        VoleurDTO voleurData = buildVoleurDTO();
        int oreId = currentTarget != null ? currentTarget.getId() : -1;

        for (CatchingThiefObserver observer : observers) {
            observer.onCaptureStarted(mineurData, voleurData, oreId);
        }
    }

    private void notifyCaptureProgress() {
        MinerDTO mineurData = buildMinerDTO();
        VoleurDTO voleurData = buildVoleurDTO();

        for (CatchingThiefObserver observer : observers) {
            observer.onCaptureProgress(mineurData, voleurData);
        }
    }

    private void notifyCaptureCompleted() {
        MinerDTO mineurData = buildMinerDTO();
        VoleurDTO voleurData = buildVoleurDTO();
        int hasDrop = drop;
        boolean isRegen = false;
        int oreId = currentTarget != null ? currentTarget.getId() : -1;

        if (drop > 0) {
            isRegen = true;
            drop = 0;
        }
        for (CatchingThiefObserver observer : observers) {
            observer.onCaptureCompleted(mineurData, voleurData, isRegen, oreId, hasDrop);
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

    private VoleurDTO buildVoleurDTO() {
        Map<Integer, String> states = new HashMap<>();
        states.put(voleur.getId(), voleur.getState());

        return new VoleurDTO(
                voleur.getId(),
                voleur.getPositionMap(),
                voleur.getPositionPixel(),
                states,
                voleur.getMaxCaptureTime(),
                voleur.getCaptureTime());
    }

}
