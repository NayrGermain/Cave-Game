package model.characters;

import model.Constants;
import model.GameMap;
import model.Tile;
import model.DTO.VoleurDTO;
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
import model.observers.TravelObserver;
import model.observers.OreMiningObserver;

public class MiningVoleurAction extends VoleurAction {
    // VARIABLES
    private GameMap gameMap; // Permet de connaitre les tuiles adjacentes
    private OreInstance closestMineral;
    private Tile currentTile;
    private Point changeTile;
    private String direction;

    private List<OreMiningObserver> observers;
    private List<TravelObserver> voleurTravelObservers;

    // CONSTRUCTOR
    public MiningVoleurAction(GameMap gameMap, Voleur voleur, Point destination, Tile currentTile) {
        super(voleur, destination);
        this.gameMap = gameMap;
        this.currentTile = currentTile;
        this.changeTile = null;
        this.closestMineral = null;
        this.direction = null;

        this.observers = new ArrayList<>();
        this.voleurTravelObservers = new ArrayList<>();

    }

    // METHODS
    @Override
    public void execute() {
        if (closestMineral == null) {
            closestMineral = voleur.getClosestMineral();
        }
        // Initie un deplacement vers le minerai
        if (voleur.getState().equals("IDLE")) {
            // Récupérer la liste des minerais de la tile du voleur
            // Si l'ensemble des minerais est vide le voleur change de tile
            if (closestMineral == null) {
                voleur.setState("TO_NEXT_TILE");
            } else {
                // Récupérer le minerai à miner
                voleur.setState("TO_MINING");
            }
        }
        // Se deplace vers un minerai ou une autre tuile
        else if (voleur.getState().equals("TO_MINING")) {

            // Si il reste des minerais dispo le voleur se déplace vers le minerai le plus
            // proche
            if (closestMineral != null) {
                voleur.deplacerVers(closestMineral.getPosition());
            } else {
                voleur.setState("WAITING");
            }
        }
        // Si le voleur est pret a miner on change son etat et notifie la vue
        else if (voleur.getState().equals("READY_TO_MINE")) {
            if (closestMineral != null && closestMineral.getOre().getStatus().equals("AVAILABLE")) {
                voleur.setState("MINING");
                closestMineral.getOre().setStatus("MINING");
                notifyMiningStart(closestMineral.getId(), closestMineral.getOre(), closestMineral.getPosition());
            }
        }
        // Tant que le minerai est en cours de minage (non épuisé)
        else if (voleur.getState().equals("MINING")) {
            if (closestMineral != null) {
                mine(closestMineral);
            } else {
                voleur.setState("TO_NEXT_TILE");
            }
        }
        // Se deplace vers une nouvelle tuile
        else if (voleur.getState().equals("TO_NEXT_TILE")) {
            if (direction == null) {
                direction = currentTile.getNotBlockedOpen();
            }

            Point destinationTuile = calculateNewTileBasedOnDirection(direction);

            if (isTileChangeValid(destinationTuile)) {
                // Déplacer le voleur vers la nouvelle tuile
                voleur.deplacerVers(calculatePixelPosition(destinationTuile, direction));
                changeTile = destinationTuile; // preset les coordonnées de la prochaine tuile
            } else {
                voleur.setState("IDLE");
            }
        }
        // Déplacé le voleur sur la nouvelle tuile
        else if (voleur.getState().equals("TRAVEL") && changeTile != null) {
            // Déplacer le voleur vers la nouvelle tuile
            closestMineral = null;
            voleur.setPositionMap(changeTile, direction);

            // ajoute la tuile à la liste des tuiles explorées
            if (!voleur.addAncienneTuile(currentTile)) {
                // System.out.println("Le voleur tourne en rond");
                voleur.setState("IDLE");
            }

            // Mettre a jour la liste d'ores à miner
            currentTile = gameMap.getTile(changeTile.x, changeTile.y);
            voleur.setOresToMine(currentTile.getMineralInstances());

            // remettre changeTile à null pour éviter de bouger à chaque tick
            changeTile = null;
            direction = null;
            voleur.setState("IDLE");

            // Notifier la vue que le voleur a changé de tuile
            notifyTravelCompleted(voleur.getId(), voleur.getPositionMap(), voleur.getPositionPixel());

        } else if (voleur.getState().equals("CAPTURING")) {
            // libérer le minerai
            closestMineral.getOre().setStatus("AVAILABLE");
            closestMineral.setTargeted(null);
        } else {
            System.out.println("ici");
            cancelMining();
            voleur.setState("IDLE");
            voleur.notifyObservers();
        }
    }

    // Méthode pour miner le minerais
    private void mine(OreInstance oreInstance) {
        // Tant que le minerai est en cours de minage (non épuisé)
        if (closestMineral.getOre().getStatus().equals("MINING")
                && (voleur.getState().equals("MINING"))) {

            closestMineral.getOre().voleurMine(); // Minage du voleur ne renvois rien

            // Notifier la vue que le minage a progressé pour mettre a jour la progression
            // visuelle
            notifyMiningProgress(closestMineral.getId(), closestMineral.getOre(), closestMineral.getPosition(),
                    0);

            // Si le minerai est épuisé, l'ajouter à la liste des minerais à regénérer
            if (closestMineral.getOre().getStatus().equals("RESPAWN")) {
                TrActionManager.getInstance().ajouterMineraiARegenerer(closestMineral);
            }
        } else {
            // Notifié la vue que le minage est terminé
            notifyMiningComplete(closestMineral.getId(), closestMineral.getOre(), closestMineral.getPosition(), false);
            closestMineral.setTargeted(null); // de nouveau ciblable
            closestMineral = null;
            voleur.setState("WAITING");
            voleur.notifyObservers();
        }
    }

    // Méthode pour annuler le minage
    public void cancelMining() {
        // Vérifie si le minerai est encore en cours de minage
        if (closestMineral.getOre().getStatus().equals("MINING")
                && (!voleur.getState().equals("MINING"))) {
            // Réinitialiser le statut du minerai en "AVAILABLE"
            closestMineral.getOre().setStatus("AVAILABLE");

            // Notifier la vue que leminage a été annulé
            notifyMiningComplete(closestMineral.getId(), closestMineral.getOre(), closestMineral.getPosition(), false);
        }
    }

    // Définis la tuile à afficher en fonction de la porte clické par l'utilisateur
    public Point calculateNewTileBasedOnDirection(String direction) {
        Point newTile = voleur.positionMap;

        direction = direction.toLowerCase(); // S'assurer que les string match
        // Calculer la nouvelle tuile en fonction de la direction
        if (direction.equals("north")) {
            return new Point(newTile.x, newTile.y - 1);
        } else if (direction.equals("south")) {
            return new Point(newTile.x, newTile.y + 1);
        } else if (direction.equals("east")) {
            return new Point(newTile.x + 1, newTile.y);
        } else if (direction.equals("west")) {
            return new Point(newTile.x - 1, newTile.y);
        }
        return newTile;
    }

    // Vérifie si le changement de tuile est valide
    public boolean isTileChangeValid(Point newTile) {
        // Vérifie si la nouvelle tuile est dans les limites de la grille
        return newTile.x >= 0 && newTile.x < Constants.FRAME_WIDTH - 1 &&
                newTile.y >= 0 && newTile.y < Constants.FRAME_HEIGHT - 1;
    }

    private Point calculatePixelPosition(Point newTile, String direction) {
        direction = direction.toLowerCase(); // S'assurer que les string match
        if (direction.equals("north")) {
            return new Point(Constants.LAYOUT_NUMBER_OFCUT / 2, 0);
        } else if (direction.equals("south")) {
            return new Point(Constants.LAYOUT_NUMBER_OFCUT / 2, Constants.LAYOUT_NUMBER_OFCUT - 1);
        } else if (direction.equals("east")) {
            return new Point(Constants.LAYOUT_NUMBER_OFCUT - 1, Constants.LAYOUT_NUMBER_OFCUT / 2);
        } else if (direction.equals("west")) {
            return new Point(0, Constants.LAYOUT_NUMBER_OFCUT / 2);
        }
        return null;
    }

    // OBSERVER
    public void attachObserver(OreMiningObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OreMiningObserver observer) {
        observers.remove(observer);
    }

    public void attachNextTileObserver(TravelObserver observer) {
        voleurTravelObservers.add(observer);
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
        // l'état visuel du minerai
        MineralDetailsDTO mineralDTO = buildDTO(oreId, ore, position, qtyMined);
        VoleurDTO voleurDTO = buildVoleurDTO();

        for (OreMiningObserver observer : observers) {
            observer.onMiningProgress(oreId, mineralDTO, voleurDTO);
        }
    }

    public void notifyMiningComplete(int oreId, Ores ore, Point position, boolean isRegen) {
        // Le DTO est nécéssaire pendant la regeneration du minerais pour update son
        // image
        MineralDetailsDTO mineralDTO = buildDTO(oreId, ore, position, 0); // Notifié que le minerais est épuisé

        VoleurDTO voleurDTO = buildVoleurDTO(); // Notifié que l'état du mineur a changé

        for (OreMiningObserver observer : observers) {
            observer.onMiningComplete(oreId, mineralDTO, isRegen, voleurDTO);
        }
    }

    public void notifyTravelCompleted(int voleurId, Point mapPosition, Point positionPixel) {
        for (TravelObserver observer : voleurTravelObservers) {
            observer.onVoleurTravel(voleurId, mapPosition, positionPixel);
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
