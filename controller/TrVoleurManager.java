package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import model.Items.OreInfo;
import model.Items.OreInstance;
import model.characters.MiningVoleurAction;
import model.characters.Voleur;
import model.characters.VoleurAction;
import model.observers.ActionFinishedObserver;
import model.observers.OreRegeneratedObserver;
import model.Constants;
import model.DTO.MineralDetailsDTO;

public class TrVoleurManager extends Thread {
    // VARIABLES
    private ConcurrentHashMap<Voleur, Long> voleurLastMiningTime;
    private ConcurrentHashMap<Integer, OreInstance> mineraisARegenerer;
    private ConcurrentHashMap<Voleur, VoleurAction> voleursActions;
    private ConcurrentHashMap<Voleur, Long> voleurAttente;

    private boolean isRunning;
    private long lastRegenerationTime; // Définis le temps de la dernière régénération de minerais

    private List<OreRegeneratedObserver> oreRegeneratedObserver;
    private List<ActionFinishedObserver> actionFinishedObserver;

    // SINGLETON PATTERN
    private static TrVoleurManager instance;

    public static TrVoleurManager getInstance() {
        if (instance == null) {
            instance = new TrVoleurManager();
        }
        return instance;
    }

    // CONSTRUCTOR
    public TrVoleurManager() {
        mineraisARegenerer = new ConcurrentHashMap<>();

        voleurLastMiningTime = new ConcurrentHashMap<>();
        voleursActions = new ConcurrentHashMap<>();
        voleurAttente = new ConcurrentHashMap<>();

        lastRegenerationTime = System.currentTimeMillis();

        oreRegeneratedObserver = new ArrayList<>();
        actionFinishedObserver = new ArrayList<>();

        this.isRunning = true;
        this.start();
    }

    // METHODS
    // Ajouter une action de minage
    public void addMiningAction(Voleur voleur, VoleurAction action) {
        voleursActions.put(voleur, action);
    }

    // Retirer un voleur de la liste des voleurs actifs / fin d'action
    public void removeActiveVoleur(Voleur voleur) {
        voleursActions.remove(voleur);
    }

    // Ajouter un minerai à regénérer
    public void ajouterMineraiARegenerer(OreInstance oreInstance) {
        mineraisARegenerer.put(oreInstance.getId(), oreInstance);
    }

    @Override
    public void run() {
        while (isRunning) {
            ConcurrentHashMap<Voleur, VoleurAction> actionsToBeRemovedVoleur = new ConcurrentHashMap<>();
            long currentTime = System.currentTimeMillis();

            // Regeneration des minerais
            if (currentTime - lastRegenerationTime >= 1000) { // Vérifiez si une seconde s'est écoulée
                for (OreInstance oreInstance : mineraisARegenerer.values()) {
                    // Appel la méthode pour regénérer le minerais seconde par seconde
                    boolean isRegenerated = oreInstance.getOre().regenerateOre();
                    if (isRegenerated) {
                        // Retirer le minerais de la liste des minerais à regénérer
                        mineraisARegenerer.remove(oreInstance.getId());

                        oreInstance.getOre().setStatus("AVAILABLE");
                        MineralDetailsDTO mineralDetails = buildDTO(oreInstance); // preparer les informations pour
                                                                                  // notifier la vue
                        notifyOreRegenerated(oreInstance.getId(), mineralDetails); // Notifier la vue que le minerais
                                                                                   // est a nouveau dispo
                    }
                }
                lastRegenerationTime = currentTime; // Mettre a jour le temps de la dernière régénération
            }

            // Appeler la méthode execute() des actions des voleurs
            voleursActions.forEach((voleur, action) -> {

                if (voleurAttente.containsKey(voleur)) {
                    if (currentTime - voleurAttente.get(voleur) >= 8000) { // Le voleur attend 8 secondes après avoir
                                                                           // miné un minerai pour bouger
                        System.out.println("Ca retire le voleur : " + voleur.getState());
                        // Si le voleur n'est pas en cours de capture
                        if (!voleur.getState().equals("CAPTURING") && !voleur.getState().equals("TO_REMOVE")) {
                            voleur.setState("IDLE");
                            voleur.notifyObservers();
                        }
                        voleurAttente.remove(voleur);

                    }
                } else {

                    if (voleur.getState().equals("MINING") && action instanceof MiningVoleurAction) {

                        voleurLastMiningTime.putIfAbsent(voleur, 0L);
                        long lastMiningTime = voleurLastMiningTime.get(voleur);
                        if (currentTime - lastMiningTime >= 1000) {
                            action.execute();
                            if (voleur.getState().equals("WAITING")) {
                                System.out.println("Ca ajoute le voleur");
                                System.out.println(currentTime);
                                voleurAttente.put(voleur, currentTime);
                            }
                            voleurLastMiningTime.put(voleur, currentTime);
                        }
                    }
                    // Voleur capturé
                    else if (voleur.getState().equals("TO_REMOVE")) {
                        // Marquer l'action pour retrait
                        System.out.println("Ca retire le voleur : " + voleur.getState());
                        actionsToBeRemovedVoleur.put(voleur, action);

                    } else {
                        // Les autres actions du mineur
                        action.execute();
                    }
                }

                // TODO : RETIRER UN MINEUR = LE CAPTURER
            });

            // Retirer les actions des mineurs marquées
            actionsToBeRemovedVoleur.forEach((voleur, action) -> {
                removeActiveVoleur(voleur);

                notifyFinishedAction(voleur.getId(), "REMOVE_THIEF");
            });

            try {
                Thread.sleep(Constants.DELAY);
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    }

    public void stopRunning() {
        isRunning = false;
    }

    // OBSERVER PATTERN
    public void attachOreRegenObserver(OreRegeneratedObserver observer) {
        oreRegeneratedObserver.add(observer);
    }

    public void attachedFinishedObserver(ActionFinishedObserver observer) {
        actionFinishedObserver.add(observer);
    }

    public void removeReRegenObserver(OreRegeneratedObserver observer) {
        oreRegeneratedObserver.remove(observer);
    }

    public void removeFinishedObserver(ActionFinishedObserver observer) {
        actionFinishedObserver.remove(observer);
    }

    public void notifyOreRegenerated(int oreId, MineralDetailsDTO mineralDetails) {
        for (OreRegeneratedObserver observer : oreRegeneratedObserver) {
            observer.onOreRegenerated(oreId, mineralDetails);
        }
    }

    public void notifyFinishedAction(int minerId, String stateIDLE) {
        for (ActionFinishedObserver observer : actionFinishedObserver) {
            observer.onFinishedAction(minerId, stateIDLE);
        }
    }

    // Construis le DTO pour notifier la vue
    private MineralDetailsDTO buildDTO(OreInstance oreInstance) {
        OreInfo oreInfo = new OreInfo(
                oreInstance.getOre().getId(),
                oreInstance.getOre().getType(),
                oreInstance.getOre().getQuantity(),
                oreInstance.getPosition(),
                oreInstance.getOre().getState(),
                oreInstance.getOre().getHarvestingTime());

        return new MineralDetailsDTO(oreInstance.getId(), oreInfo);
    }

}
