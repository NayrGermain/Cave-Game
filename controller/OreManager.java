package controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Constants;
import model.GameMap;
import model.Tile;
import model.DTO.MapDTO;
import model.DTO.OresDTO;
import model.Items.GoldOre;
import model.Items.IronOre;
import model.Items.OreInfo;
import model.Items.OreInstance;
import model.Items.Ores;
import model.Items.SilverOre;
import model.observers.MapObserver;
import model.observers.OresObserver;

// Représente la logique de positionnement des minerais
public class OreManager implements MapObserver {
    private GameMap gameMap;
    private Map<Integer, OreInstance> oreInstanceMap; // conserve les references des minerais
    private Point currentTile;

    private List<OresObserver> observers = new ArrayList<>();

    public OreManager(GameMap gameMap) {
        this.gameMap = gameMap;
        this.oreInstanceMap = new HashMap<>();
        this.currentTile = null;
    }

    // Garder une trace de l'oreInstance pour le controller et les interaction
    private void addOreInstance(OreInstance oreInstance) {
        oreInstanceMap.put(oreInstance.getId(), oreInstance);
    }

    // Méthode pour récupérer une OreInstance par son ID
    public OreInstance getOreInstanceById(int id) {
        return oreInstanceMap.get(id);
    }

    // Méthode pour placer les minerais sur la map
    public void placeMineralsOnMap() {
        Random rand = new Random();
        Tile[][] tiles = gameMap.getTiles();

        // Parcourir toutes les tuiles de la map
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                Tile tile = tiles[x][y];

                List<Point> floorSegments = new ArrayList<>(); // Liste des segments de type sol
                // Parcourir tout les segments de la tuile
                for (int u = 0; u < Constants.LAYOUT_NUMBER_OFCUT; u++) {
                    for (int v = 0; v < Constants.LAYOUT_NUMBER_OFCUT; v++) {
                        if (tile.getGridIndexValue(u, v) == Constants.FLOOR) { // Choisir les segments de sol
                            floorSegments.add(new Point(u, v));
                        }
                    }
                }

                int numberOfMineralsToPlace = rand.nextInt(2) + 1; // quantité de minerais par tile
                numberOfMineralsToPlace = Math.min(numberOfMineralsToPlace, floorSegments.size()); // Ne pas placer plus
                                                                                                   // de minerais que de
                                                                                                   // segments de sol

                // Placer les minerais sur les segments de sol
                for (int i = 0; i < numberOfMineralsToPlace; i++) {
                    if (!floorSegments.isEmpty()) {
                        // Sélectionner aléatoirement un segment Floor
                        int randomIndex = rand.nextInt(floorSegments.size());
                        Point selectedSegment = floorSegments.remove(randomIndex); // Evite la superposition

                        Ores mineralToPlace = selectMineralBasedOnZoneProbability(tile.getZoneLevel());
                        if (mineralToPlace != null) {
                            // Créez une nouvelle instance MineralInstance avec la position sélectionnée
                            OreInstance mineralInstance = new OreInstance(mineralToPlace, selectedSegment);
                            addOreInstance(mineralInstance); // Ajoutez l'instance de minerai à la liste

                            // Placez l'instance de minerai sur le segment de tuile sélectionné
                            tile.addMineralInstance(mineralInstance);
                        }
                    }
                }
            }
        }
        notifyObservers();
    }

    // Méthode pour sélectionner un type de minerai en fonction de la zone et selon
    // sa probabilité d'apparition
    private Ores selectMineralBasedOnZoneProbability(int zoneID) {
        Random rand = new Random();
        int probability = rand.nextInt(100);
        switch (zoneID) {
            case 1: // Pour la zone 1
                if (probability < 1)
                    return new GoldOre(); // 1% de chance
                else if (probability < 15)
                    return new SilverOre(); // 15% de chance
                else
                    return new IronOre(); // le reste
            case 2:
                if (probability < 15)
                    return new GoldOre();
                else if (probability < 60)
                    return new SilverOre();
                else
                    return new IronOre();
            case 3:
                if (probability < 50)
                    return new GoldOre();
                else if (probability < 35)
                    return new SilverOre();
                else
                    return new IronOre();
            default:
                return null;
        }
    }

    // OBSERVER PATTERN
    public void attachObserver(OresObserver observer) {
        observers.add(observer);
    }

    public void detachObserver(OresObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        OresDTO oresData = createOresDataForTile(currentTile);
        for (OresObserver observer : observers) {
            observer.onOresChange(oresData);
        }
    }

    private OresDTO createOresDataForTile(Point currentTile) {
        Tile tile = gameMap.getTile(currentTile.x, currentTile.y); // Obtenir la tuile à partir de GameMap
        List<OreInstance> oreInstances = tile.getMineralInstances(); // Récupérer les instances de minerai de la tuile

        Map<Point, OreInfo> mineralPositions = new HashMap<>();
        for (OreInstance oreInstance : oreInstances) {
            Point position = oreInstance.getPosition(); // Position du minerai sur la tuile
            OreInfo oreInfo = new OreInfo(
                    oreInstance.getId(),
                    oreInstance.getOre().getType(),
                    oreInstance.getOre().getQuantity(),
                    position,
                    oreInstance.getOre().getState(),
                    oreInstance.getOre().getHarvestingTime()); // Créer OreInfo à partir de oreInstance

            mineralPositions.put(position, oreInfo); // Associer la position du minerai à OreInfo
        }

        return new OresDTO(mineralPositions); // Créer et retourner le DTO avec les positions et infos des minerais
    }

    // Observe les changement de tuile
    @Override
    public void onTileChange(MapDTO mapData) {
        this.currentTile = mapData.getCurrentTile(); // Mettre à jour la tuile actuelle à partir de MapData
        notifyObservers(); // Notifier les observateurs
    }

}
