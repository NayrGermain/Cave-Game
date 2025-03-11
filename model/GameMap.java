package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.characters.Miner;

import model.DTO.MiniMapDTO;
import model.DTO.MapDTO;

import model.characters.Voleur;
import model.observers.MapObserver;
import model.observers.MiniMapObserver;

public class GameMap {
    // VARIABLES
    private int width; // Largeur de la grille (en nombre de tuiles)
    private int height; // Hauteur de la grille (en nombre de tuiles)
    private Tile[][] tiles; // Grille représentant l'ensemble des tuiles de la map
    private Point startPoint; // Point de départ
    private Point exitPoint; // Point de sortie
    private Point currentTile; // Tuile actuelle
    private boolean[][][] wallsMatrix; // Matrice de booléens pour les murs

    private Map<Integer, Miner> mineurs;
    private Map<Integer, Voleur> voleurs;

    private List<MiniMapObserver> miniMapObservers;
    private List<MapObserver> mapObservers;

    // CONSTRUCTOR
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.wallsMatrix = new boolean[width][height][4]; // 4 pour Nord, Est, Sud, Ouest
        this.mineurs = new HashMap<>();
        this.voleurs = new HashMap<>();

        this.miniMapObservers = new ArrayList<>();
        this.mapObservers = new ArrayList<>();

        initMap();
    }

    // METHODS
    // Initialise la map
    public void initMap() {
        // Kruskal's maze generation algorithm
        // Créez les tuiles de la map
        Set<Integer>[][] tileSets = new Set[width][height]; // Creer les set pour suivre les tuiles connectées
        List<Wall> walls = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(); // Créez une tuile pour créer un layout

                // Creer les sets pour chaque tuile
                tileSets[x][y] = new HashSet<>(); // initialement, chaque tuile est dans son propre ensemble
                tileSets[x][y].add(x * height + y); // formule simple pour avoir un identifiant unique par tuile

                // Creer les murs entre les tuiles (sépare les tuiles adjacentes)
                // faire correspondre les murs avec les tuiles
                if (x > 0)
                    walls.add(new Wall(x, y, x - 1, y)); // Mur ouest
                if (y > 0)
                    walls.add(new Wall(x, y, x, y - 1)); // Mur nord
                if (x < width - 1)
                    walls.add(new Wall(x, y, x + 1, y)); // Mur est
                if (y < height - 1)
                    walls.add(new Wall(x, y, x, y + 1)); // Mur sud
            }
        }
        Collections.shuffle(walls); // Mélangez les murs pour la sélection aléatoire

        // Fusion des chemins
        wallsMatrix = new boolean[width][height][4]; // 4 pour Nord, Est, Sud, Ouest
        for (Wall wall : walls) {
            // Vérifiez si les ensembles des tuiles sont connectés par des elements communs
            if (!setsConnected(tileSets, wall.x1, wall.y1, wall.x2, wall.y2)) {
                // Si les ensembles ne sont pas connectés mais ont des elements communs, les
                // funsionner
                mergeTiles(tileSets, wall.x1, wall.y1, wall.x2, wall.y2);
                // Créez des ouvertures dans l'ensemble nouvellement créé
                createOpening(wall.x1, wall.y1, wall.x2, wall.y2, wallsMatrix);
            }
        }

        // Générer le layout des tuiles et définir leurs niveaux de manière concentrique
        // Calculer les seuils dynamiquement basés sur la taille de la carte
        int zone1Threshold = width / 3; // Zone centrale
        int zone2Threshold = 2 * width / 3; // Zone intermédiaire

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Générer le layout de la tuile
                tiles[x][y].generateLayout(Constants.LAYOUT_NUMBER_OFCUT);

                // Définir le niveau de la zone
                int zoneLevel;
                if (x >= zone1Threshold && x < zone2Threshold && y >= zone1Threshold && y < zone2Threshold) {
                    zoneLevel = 1; // Zone centrale
                } else if (x >= zone1Threshold - 1 && x < zone2Threshold + 1 && y >= zone1Threshold - 1
                        && y < zone2Threshold + 1) {
                    zoneLevel = 2; // Zone intermédiaire
                } else {
                    zoneLevel = 3; // Zone périphérique
                }
                tiles[x][y].setZoneLevel(zoneLevel);
            }
        }

        closeDoorsBetweenZones(); // Fermer les portes entre les zones
        setStartAndExitPoints();
        setCurrentTile(startPoint); // permet de notifier les observers
    }

    // Fermer les portes entre les zones de manière coordonnée
    public void closeDoorsBetweenZones() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile currentTile = tiles[x][y];
                int currentZone = currentTile.getZoneLevel();

                // Gestion des portes Ouest et Est
                if (currentTile.isWestOpen()) {
                    Tile westTile = getTile(x - 1, y);
                    if (westTile != null && westTile.getZoneLevel() != currentZone) {
                        closeDoor(currentTile, "WEST", westTile, x, y);
                        // Fermeture symétrique de la porte Est de la tuile à l'ouest
                        closeDoor(westTile, "EAST", currentTile, x - 1, y);
                    }
                }

                if (currentTile.isEastOpen()) {
                    Tile eastTile = getTile(x + 1, y);
                    if (eastTile != null && eastTile.getZoneLevel() != currentZone) {
                        closeDoor(currentTile, "EAST", eastTile, x, y);
                        // Fermeture symétrique de la porte Ouest de la tuile à l'est
                        closeDoor(eastTile, "WEST", currentTile, x + 1, y);
                    }
                }

                // Gestion des portes Nord et Sud
                if (currentTile.isNorthOpen()) {
                    Tile northTile = getTile(x, y - 1);
                    if (northTile != null && northTile.getZoneLevel() != currentZone) {
                        closeDoor(currentTile, "NORTH", northTile, x, y);
                        // Fermeture symétrique de la porte Sud de la tuile au nord
                        closeDoor(northTile, "SOUTH", currentTile, x, y - 1);
                    }
                }

                if (currentTile.isSouthOpen()) {
                    Tile southTile = getTile(x, y + 1);
                    if (southTile != null && southTile.getZoneLevel() != currentZone) {
                        closeDoor(currentTile, "SOUTH", southTile, x, y);
                        // Fermeture symétrique de la porte Nord de la tuile au sud
                        closeDoor(southTile, "NORTH", currentTile, x, y + 1);
                    }
                }
            }
        }
    }

    // Récupérer la constante appropriée pour le matériau de la porte selon la zone
    private int getMaterialConstant(String direction, boolean isSilver) {
        switch (direction) {
            case "NORTH":
                return isSilver ? Constants.NORTH_DOOR_CLOSED_SILVER : Constants.NORTH_DOOR_CLOSED_IRON;
            case "SOUTH":
                return isSilver ? Constants.SOUTH_DOOR_CLOSED_SILVER : Constants.SOUTH_DOOR_CLOSED_IRON;
            case "EAST":
                return isSilver ? Constants.EAST_DOOR_CLOSED_SILVER : Constants.EAST_DOOR_CLOSED_IRON;
            case "WEST":
                return isSilver ? Constants.WEST_DOOR_CLOSED_SILVER : Constants.WEST_DOOR_CLOSED_IRON;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    private void closeDoor(Tile tile, String direction, Tile otherTile, int x, int y) {
        tile.setDoorBlocked(direction, true);

        // Determiner le niveau de la zone et donc le tier de porte
        boolean isSilver = (tile.getZoneLevel() == 2 && otherTile.getZoneLevel() == 3) ||
                (tile.getZoneLevel() == 3 && otherTile.getZoneLevel() == 2);

        int materialIndex = getMaterialConstant(direction, isSilver);

        // determiner la position de la porte dans la grille
        Point gridIndex = tile.getGridIndexForDirection(direction);

        // Modifié la valeur de la grille avec la valeur de l'asset appropriée
        tile.setGridIndexValue(gridIndex.x, gridIndex.y, materialIndex);

        // Créer l'objet de porte bloqué approprié au paremetre definis
        List<String> requiredTools = isSilver
                ? List.of(Constants.SILVER_PICKAXE, Constants.GOLD_PICKAXE)
                : List.of(Constants.IRON_PICKAXE, Constants.SILVER_PICKAXE,
                        Constants.GOLD_PICKAXE);
        int unlockingDuration = isSilver ? 240 : 120;
        tile.setDoorRequirements(direction,
                new BlockedDoor(direction, requiredTools, unlockingDuration, tile, otherTile, gridIndex,
                        new Point(x, y)));
    }

    public void addMineur(Miner mineur) {
        mineurs.put(mineur.getId(), mineur);
    }

    public void addVoleur(Voleur voleur) {
        voleurs.put(voleur.getId(), voleur);
    }

    // Vérifie si deux ensembles ont des éléments en commun
    private boolean setsConnected(Set<Integer>[][] tileSets, int x1, int y1, int x2, int y2) {
        Set<Integer> set1 = tileSets[x1][y1];
        Set<Integer> set2 = tileSets[x2][y2];
        for (Integer i : set1) {
            if (set2.contains(i)) {
                return true;
            }
        }
        return false;
    }

    // Fusionne deux ensembles
    private void mergeTiles(Set<Integer>[][] tileSets, int x1, int y1, int x2, int y2) {
        Set<Integer> set1 = tileSets[x1][y1];
        Set<Integer> set2 = tileSets[x2][y2];
        if (set1 != set2) {
            set1.addAll(set2);
            set2.forEach(i -> {
                int x = i / height;
                int y = i % height;
                tileSets[x][y] = set1; // Mise à jour des références pour tous les éléments de set2
            });
        }
    }

    // Crée une ouverture entre deux tuiles et construit la matrice de booléens
    // representatrice des murs
    private void createOpening(int x1, int y1, int x2, int y2, boolean[][][] wallsMatrix) {
        Tile tile1 = tiles[x1][y1];
        Tile tile2 = tiles[x2][y2];
        int direction = -1;

        // Determine la direction de l'ouverture en comparant les coordonnées
        if (x1 == x2) {
            if (y1 < y2) { // Sud
                tile1.setSouthOpen(true);
                tile2.setNorthOpen(true);
                direction = 2; // Index pour Sud
            } else { // Nord
                tile1.setNorthOpen(true);
                tile2.setSouthOpen(true);
                direction = 0; // Index pour Nord
            }
        } else {
            if (x1 < x2) { // Est
                tile1.setEastOpen(true);
                tile2.setWestOpen(true);
                direction = 1; // Index pour Est
            } else { // Ouest
                tile1.setWestOpen(true);
                tile2.setEastOpen(true);
                direction = 3; // Index pour Ouest
            }
        }

        // Mettre à jour la matrice des murs pour les deux tuiles
        if (direction != -1) {
            wallsMatrix[x1][y1][direction] = true; // Enlève le mur entre les deux tuiles
            int oppositeDirection = (direction + 2) % 4; // Calcul de la direction opposée
            wallsMatrix[x2][y2][oppositeDirection] = true; // Enlève le mur opposé entre les deux tuiles
        }
    }

    // Définir les points de départ et de sortie avec la porte en or comme sortie
    private void setStartAndExitPoints() {
        int x_start = this.width / 2;
        int y_start = this.height / 2;
        startPoint = new Point(x_start, y_start);

        // Choisir un coin aléatoire pour la sortie
        int x_exit = (int) (Math.random() * 2) * (width - 1);
        int y_exit = (int) (Math.random() * 2) * (height - 1);
        exitPoint = new Point(x_exit, y_exit);
        Tile exitTile = tiles[x_exit][y_exit];

        // Définir la première tuile comme point de départ
        tiles[x_start][y_start].setStart(true);
        tiles[x_start][y_start].setExplored(true);
        exitTile.setExit(true);

        // Determiner sur quel mur de la sortie placé la porte de fin de jeu
        int exitAssetValue;
        Point doorPosition = new Point();
        String direction = "";
        if (exitPoint.x == 0 && exitPoint.y == 0) {
            exitAssetValue = Constants.NORTH_DOOR_CLOSED_GOLD;
            doorPosition = exitTile.getGridIndexForDirection("NORTH"); // NORTH DOOR
            direction = "NORTH";
            exitTile.setNorthOpen(true);
            exitTile.setExitDirection(direction);

        } else if (exitPoint.x == width - 1 && exitPoint.y == 0) {
            exitAssetValue = Constants.NORTH_DOOR_CLOSED_GOLD;
            doorPosition = exitTile.getGridIndexForDirection("NORTH"); // NORTH DOOR
            direction = "NORTH";
            exitTile.setNorthOpen(true);
            exitTile.setExitDirection(direction);

        } else if (exitPoint.x == 0 && exitPoint.y == height - 1) {
            exitAssetValue = Constants.SOUTH_DOOR_CLOSED_GOLD;
            doorPosition = exitTile.getGridIndexForDirection("SOUTH"); // SOUTH DOOR
            direction = "SOUTH";
            exitTile.setSouthOpen(true);
            exitTile.setExitDirection(direction);

        } else {
            exitAssetValue = Constants.SOUTH_DOOR_CLOSED_GOLD;
            doorPosition = exitTile.getGridIndexForDirection("SOUTH"); // SOUTH DOOR
            direction = "SOUTH";
            exitTile.setSouthOpen(true);
            exitTile.setExitDirection(direction);

        }

        // Set the exit asset at the determined position
        exitTile.setGridIndexValue(doorPosition.x, doorPosition.y, exitAssetValue);
        exitTile.setDoorBlocked(direction, true);
        exitTile.setDoorRequirements(direction,
                new BlockedDoor(
                        direction,
                        List.of(Constants.GOLD_PICKAXE),
                        560, exitTile,
                        null,
                        exitTile.getGridIndexForDirection(direction),
                        exitPoint));
    }

    // OBSERVER PATTERN
    /* MINIMAP */
    public void attachObserver(MiniMapObserver observer) {
        miniMapObservers.add(observer);
    }

    public void detachObserver(MiniMapObserver observer) {
        miniMapObservers.remove(observer);
    }

    /* MAP */
    public void attachObserver(MapObserver observer) {
        mapObservers.add(observer);
    }

    public void detachObserver(MapObserver observer) {
        mapObservers.remove(observer);
    }

    // Change la tuile actuelle
    public void setCurrentTile(Point newTile) {
        this.currentTile = newTile;
        Tile current = getTile(newTile.x, newTile.y);

        // Marquez la nouvelle tuile comme explorée
        current.setExplored(true);

        // Créez les DTOs
        MiniMapDTO miniMapData = buildMiniMapDTO();
        MapDTO mapData = buildMapDTO();

        // Notifiez les observateurs de la minimap
        for (MiniMapObserver observer : miniMapObservers) {
            observer.onTileChange(miniMapData);
        }

        // Notifiez les observateurs de la map
        for (MapObserver observer : mapObservers) {
            observer.onTileChange(mapData);
        }
    }

    // Construit le DTO pour la minimap
    private MiniMapDTO buildMiniMapDTO() {
        // récupérer les tuiles explorées
        boolean[][] exploredTiles = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                exploredTiles[x][y] = tiles[x][y].isExplored(); // matrice de boolean pour les tuiles explorées
                                                                // (minimap)
                if (tiles[x][y].isStart()) {
                    startPoint = new Point(x, y); // point de départ
                }
            }
        }

        // récupérer la position sur la carte de tous les mineurs
        Map<Integer, Point> mineursPosition = new HashMap<>();
        for (Map.Entry<Integer, Miner> entry : getMineurs().entrySet()) {
            Miner mineur = entry.getValue();
            Point positionMap = mineur.getPositionMap();
            if (positionMap != null) {
                mineursPosition.put(entry.getKey(), positionMap);
            }
        }

        // récupérer la position sur la carte de tous les voleurs
        Map<Integer, Point> voleursPosition = new HashMap<>();
        for (Map.Entry<Integer, Voleur> entry : getVoleurs().entrySet()) {
            Voleur voleur = entry.getValue();
            Point positionMap = voleur.getPositionMap();
            if (positionMap != null) {
                voleursPosition.put(entry.getKey(), positionMap);
            }
        }
        return new MiniMapDTO(currentTile, exploredTiles, startPoint, exitPoint, wallsMatrix, mineursPosition,
                voleursPosition);
    }

    // Construit le DTO pour la map
    private MapDTO buildMapDTO() {
        Tile current = getTile(currentTile.x, currentTile.y); // tuile actuelle
        int[][] layout = current.getLayout(); // layout de la tuile actuelle

        Map<Integer, Point> mineursPosition = new HashMap<>(); // Map pour stocker les positions des mineurs avec leur
                                                               // ID
        for (Map.Entry<Integer, Miner> entry : getMineurs().entrySet()) { // parcourir la map des mineurs
            Miner mineur = entry.getValue();
            Point positionMap = mineur.getPositionMap(); // position sur la map
            if (positionMap != null && positionMap.equals(currentTile)) { // si la position est égale à la tuile
                                                                          // actuelle

                mineursPosition.put(entry.getKey(), mineur.getPositionPixel()); // ajoutez l'ID du mineur et sa position
                                                                                // au sein de la tuile
            }
        }

        // build les voleurs
        Map<Integer, Point> voleursPosition = new HashMap<>();
        for (Map.Entry<Integer, Voleur> entry : getVoleurs().entrySet()) {
            Voleur voleur = entry.getValue();
            Point positionMap = voleur.getPositionMap();
            if (positionMap != null && positionMap.equals(currentTile)) {
                voleursPosition.put(entry.getKey(), voleur.getPositionPixel());
            }
        }
        return new MapDTO(currentTile, layout, mineursPosition, voleursPosition);
    }

    // GETTERS & SETTERS
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getExitPoint() {
        return exitPoint;
    }

    public Point getCurrentTile() {
        return currentTile;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public Map<Integer, Miner> getMineurs() {
        return mineurs;
    }

    public Miner getMineurs(int id) {
        return mineurs.get(id);
    }

    public Map<Integer, Voleur> getVoleurs() {
        return voleurs;
    }

    public void removeVoleurByID(int id) {
        voleurs.remove(id);
    }

}
