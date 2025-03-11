package model.characters;

import model.Constants;
import model.Storage;
import model.DTO.MinerDTO;
import model.DTO.MineralDetailsDTO;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.TrActionManager;
import model.Items.OreInfo;
import model.Items.OreInstance;
import model.Items.Ores;
import model.observers.OreMiningObserver;

public class MiningAction extends MinerAction {
    // VARIABLES
    private OreInstance oreInstance;
    private Storage inventaire;

    private List<OreMiningObserver> observers;

    // CONSTRUCTOR
    public MiningAction(Miner mineur, Point destination, OreInstance oreToMine) {
        super(mineur, destination);
        this.inventaire = Storage.getInstance(); // Récupérer le singleton de l'inventaire
        this.oreInstance = oreToMine;
        this.observers = new ArrayList<>();
    }

    // METHODS
    @Override
    public void execute() {
        // Vérifiez si le mineur est suffisamment proche de sa destination pour miner
        double distance = destination.distance(mineur.getPositionPixel());
        if (mineur.getState().equals("TO_MINING") && distance <= Constants.MOVE_THRESHOLD) {
            // Le mineur est arrivé sur le minerais on passe les statut du minerai/mineur en
            // mode minage
            mineur.setState("MINING");
            oreInstance.getOre().setStatus("MINING");
            mineur.setPositionPixel(destination);
        } else if (mineur.getState().equals("TO_MINING")) {
            // Déplacez le mineur vers la destination
            mineur.deplacerVers(destination);
        } else if (mineur.getState().equals("MINING")) {
            // On mine le minerai
            mine(oreInstance);
        } else {
            cancelMining();
        }
    }

    // Méthode pour miner le minerais
    private void mine(OreInstance oreInstance) {

        // Tant que le minerai est en cours de minage (non épuisé)
        if (oreInstance.getOre().getStatus().equals("MINING")
                && (mineur.getState().equals("MINING") || mineur.getState().equals("SELECTED"))) {

            // Appel la méthode mine() de la classe Ores et retourne la quantité minée
            // (50% de la quantité à 50% de la progression du minage, les reste à la fin du
            // minage)
            int qteMined = oreInstance.getOre().mine(); // miner depuis la fonction de minage de Ores
            if (qteMined > 0) {
                inventaire.addStorage(oreInstance.getOre().getType(), qteMined);
            }
            // Notifier la vue que le minage a progressé pour mettre a jour la progression
            // visuelle
            notifyMiningProgress(oreInstance.getId(), oreInstance.getOre(), oreInstance.getPosition(),
                    qteMined);

            // Si le minerai est épuisé, l'ajouter à la liste des minerais à regénérer
            if (oreInstance.getOre().getStatus().equals("RESPAWN")) {
                TrActionManager.getInstance().ajouterMineraiARegenerer(oreInstance);
            }
        } else {
            // Notifié la vue que le minage est terminé
            mineur.setState("IDLE");
            oreInstance.setTargeted(null); // de nouveau ciblable par les voleurs
            notifyMiningComplete(oreInstance.getId(), oreInstance.getOre(), oreInstance.getPosition(), false);
        }
    }

    // Méthode pour annuler le minage
    public void cancelMining() {
        // Vérifie si le minerais est encore en cours de minage
        if (oreInstance.getOre().getStatus().equals("MINING")
                && (!mineur.getState().equals("MINING") || !mineur.getState().equals("SELECTED"))) {
            // Réinitialiser le statut du minerai en "AVAILABLE"
            oreInstance.getOre().setStatus("AVAILABLE");

            // Notifier la vue que leminage a été annulé
            notifyMiningComplete(oreInstance.getId(), oreInstance.getOre(), oreInstance.getPosition(), false);
        }
    }

    // OBSERVER
    public void attachObserver(OreMiningObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OreMiningObserver observer) {
        observers.remove(observer);
    }

    public void notifyMiningStart(int oreId, Ores ore, Point position) {
        // build oresData
        MineralDetailsDTO mineralDTO = buildDTO(oreId, ore, position, 0);

        for (OreMiningObserver observer : observers) {
            observer.onMiningStart(oreId, mineralDTO);
        }
    }

    public void notifyMiningProgress(int oreId, Ores ore, Point position, int qtyMined) {
        // Le DTO permet de notifier la vue lors de l'incrémentation de minerais et pour
        // l'état visuel du minerais
        MineralDetailsDTO mineralDTO = buildDTO(oreId, ore, position, qtyMined);
        MinerDTO mineurDTO = buildMinerDTO();

        for (OreMiningObserver observer : observers) {
            observer.onMiningProgress(oreId, mineralDTO, mineurDTO);
        }
    }

    public void notifyMiningComplete(int oreId, Ores ore, Point position, boolean isRegen) {
        // Le DTO est nécéssaire pendant la regeneration du minerais pour update son
        // image
        MineralDetailsDTO mineralDTO = buildDTO(oreId, ore, position, 0); // Notifié que le minerais est épuisé

        MinerDTO mineurDTO = buildMinerDTO(); // Notifié que l'état du mineur a changé

        for (OreMiningObserver observer : observers) {
            observer.onMiningComplete(oreId, mineralDTO, isRegen, mineurDTO);
        }
    }

    // Construis le DTO pour notifier la vue
    private MineralDetailsDTO buildDTO(int oreId, Ores ore, Point position, int qtyMined) {
        return new MineralDetailsDTO(oreId,
                new OreInfo(
                        ore.getId(),
                        ore.getType(),
                        qtyMined,
                        position,
                        ore.getState(),
                        ore.getHarvestingTime()));
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
}
