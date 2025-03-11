package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import model.Items.OreInfo;
import model.Items.OreInstance;
import model.characters.MinerAction;
import model.characters.CatchThiefAction;
import model.characters.MineOpeningAction;
import model.characters.Miner;
import model.characters.MiningAction;
import model.characters.MoveMinerAction;
import model.observers.ActionFinishedObserver;
import model.observers.OreRegeneratedObserver;
import model.Constants;
import model.DTO.MineralDetailsDTO;

public class TrActionManager extends Thread {
    // VARIABLES
    private ConcurrentHashMap<Miner, Long> minerLastMiningTime;
    private ConcurrentHashMap<Integer, OreInstance> mineraisARegenerer;
    private ConcurrentHashMap<Miner, MinerAction> minersActions;

    private boolean isRunning;
    private long lastRegenerationTime; // Définis le temps de la dernière régénération de minerais

    private List<OreRegeneratedObserver> oreRegeneratedObserver;
    private List<ActionFinishedObserver> actionFinishedObserver;

    // SINGLETON PATTERN
    private static TrActionManager instance;

    public static TrActionManager getInstance() {
        if (instance == null) {
            instance = new TrActionManager();
        }
        return instance;
    }

    // CONSTRUCTOR
    public TrActionManager() {
        minersActions = new ConcurrentHashMap<>();
        minerLastMiningTime = new ConcurrentHashMap<>();
        mineraisARegenerer = new ConcurrentHashMap<>();

        lastRegenerationTime = System.currentTimeMillis();

        oreRegeneratedObserver = new ArrayList<>();
        actionFinishedObserver = new ArrayList<>();

        this.isRunning = true;
        this.start();
    }

    // METHODS
    // Ajouter une action de minage
    public void addMiningAction(Miner miner, MinerAction action) {
        minersActions.put(miner, action);
    }

    public void addUnlockingAction(Miner miner, MineOpeningAction action) {
        minersActions.put(miner, action);
    }

    // Ajouter une action de de changement de tuile
    public void addNextTileAction(Miner miner, MinerAction action) {
        minersActions.put(miner, action);
    }

    // Ajouter une action de déplacement
    public void addMoveAction(Miner miner, MoveMinerAction action) {
        minersActions.put(miner, action);
    }

    public void addCatchThiefAction(Miner miner, MinerAction action) {
        minersActions.put(miner, action);
    }

    // Retirer un miner de la liste des miners actifs / fin d'action
    public void removeActiveminer(Miner miner) {
        minersActions.remove(miner);
    }

    // Ajouter un minerai à regénérer
    public void ajouterMineraiARegenerer(OreInstance oreInstance) {
        mineraisARegenerer.put(oreInstance.getId(), oreInstance);
    }

    @Override
    public void run() {
        while (isRunning) {
            ConcurrentHashMap<Miner, MinerAction> actionsToBeRemovedMiner = new ConcurrentHashMap<>();
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

            // Appeler la méthode execute() des actions
            minersActions.forEach((miner, action) -> {
                // Simule les execution chaque seconde, on executeras les action prenant x sec
                // ici
                if ((action instanceof MiningAction || action instanceof MineOpeningAction
                        || action instanceof CatchThiefAction)
                        && (miner.getState().equals("MINING") || miner.getState().equals("UNLOCKING_DOOR")
                                || miner.getState().equals("CAPTURING"))) {

                    minerLastMiningTime.putIfAbsent(miner, 0L);
                    long lastMiningTime = minerLastMiningTime.get(miner);
                    if (currentTime - lastMiningTime >= 1000) {
                        action.execute();
                        minerLastMiningTime.put(miner, currentTime);
                    }
                }
                // Vérifier l'état du miner après l'exécution pour savoir si l'action est
                // terminée
                else if (miner.getState().equals("IDLE")) {
                    // Marquer l'action pour retrait
                    actionsToBeRemovedMiner.put(miner, action);
                }
                // Appeler la methode execute pour les autres actions
                else {
                    action.execute();
                }
            });

            // Retirer les actions marquées
            actionsToBeRemovedMiner.forEach((miner, action) -> {
                removeActiveminer(miner);

                notifyFinishedAction(miner.getId(), "IDLE");
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
