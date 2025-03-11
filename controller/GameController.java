package controller;

import model.Constants;
import model.GameMap;
import model.Storage;

import model.characters.Miner;
import model.observers.NextLevelObserver;
import model.observers.TileHoverObserver;
import model.observers.TileHoverOreObserver;
import model.observers.TileInteractionObserver;
import view.MainFrame;
import view.MapPanel;
import view.MiniMapPanel;
import view.Northbar;
import view.TrRedessine;
import view.Market.MarketPanel;

import java.awt.Cursor;
import java.awt.Point;

public class GameController
        implements TileInteractionObserver, TileHoverObserver, TileHoverOreObserver, NextLevelObserver {

    // VARIABLES
    private MainFrame frame;
    private GameMap map;
    private MapPanel mapPanel;
    private Point currentTile;
    private MarketPanel marketPanel;
    private Northbar northbar;
    private OreManager oreManager;
    private MiniMapPanel miniMapPanel;
    private TrGameTimer gameTimer;
    private TrRedessine redessine;
    private EventsController eventsController;

    // TODO : centraliser la logique d'evenement dans l'event Controller
    // Permet d'indiquer la cible d'un Hover event
    public enum HoverState {
        NONE,
        ORE,
        OPENING,
        MINER,
        VOLEUR
        // ajouter les hover des personnages etc... ici
    }

    private HoverState currentHoverState;

    // CONSTRUCTOR
    public GameController(MapPanel mapPanel, MiniMapPanel miniMapPanel, MarketPanel marketPanel, Northbar northbar,
            MainFrame frame) {

        // instances
        this.mapPanel = mapPanel;
        this.marketPanel = marketPanel;
        this.northbar = northbar;
        this.miniMapPanel = miniMapPanel;
        this.frame = frame;

        startGame();
        attachObservers();
        setupGame();
    }

    private void startGame() {
        // Initialiser les instances
        this.map = new GameMap(Constants.MAP_WIDTH, Constants.MAP_HEIGHT); // la map découpée en tuiles
        this.currentTile = map.getStartPoint(); // Point de départ du jeu
        this.oreManager = new OreManager(map); // la gestion des minerais
        this.redessine = new TrRedessine(mapPanel);
        this.gameTimer = new TrGameTimer(northbar, map, mapPanel, miniMapPanel);
        Storage.getInstance().attachObserver(northbar);
        this.eventsController = new EventsController(map, mapPanel, this, miniMapPanel, marketPanel);

        // Démarrer les threads
        TrActionManager.getInstance(); // demarrer le singleton des entités
        redessine.start();
        gameTimer.start();

    }

    private void attachObservers() {
        map.attachObserver(mapPanel); // Abonnez la map aux changements dans GameMap
        map.attachObserver(oreManager);
        oreManager.attachObserver(mapPanel); // Abonnez la gestion des minerais aux changements dans GameMap
        map.attachObserver(miniMapPanel); // Abonnez la minimap aux changements dans GameMap
        frame.attachNextLevelObserver(this);
    }

    private void setupGame() {
        this.currentHoverState = HoverState.NONE;
        map.setCurrentTile(currentTile); // Mettre à jour la tuile courante dans la map et notifie les observers
        oreManager.placeMineralsOnMap(); // Placez les minerais sur la map

        // Placer les premiers mineurs sur la map
        generateNewMiner(new Point(Constants.MAP_WIDTH / 2, Constants.MAP_HEIGHT / 2), 4, 4, true);
        generateNewMiner(new Point(Constants.MAP_WIDTH / 2, Constants.MAP_HEIGHT / 2), 4, 4, true);
    }

    public void endGame() {
        String gameTime = gameTimer.getTime();
        gameTimer.stopRunning();
        TrActionManager.getInstance().stopRunning();
        redessine.stopRunning();

        // Afficher l'overlay de fin de partie
        frame.displayEndGameOverlay(gameTime);
    }

    public void nextStage() {
        // TODO : logique pour passer au niveau suivant
        System.out.println("TODO : Passer au niveau suivant");
    }

    // METHODS
    public void generateNewMiner(Point positionMap, int moveSpeed, double miningSpeed, boolean isBought) {
        Miner mineur = new Miner(positionMap, 4, 1);
        map.addMineur(mineur);
        marketPanel.getMarket().addMineur(mineur);
        mineur.addObserver(mapPanel); // Abonnez le mineur aux changements dans GameMap

        // si la création du mineur a pour origine le market, on le signalera à la vue
        if (isBought) {
            if (currentTile.equals(mineur.getPositionMap())) {
                // ajouter le mineur a la vue
                mapPanel.setBoughtMiner(mineur.getId(), mineur.getPositionPixel(), null);
                // attacher observer

            } else {
                mapPanel.setMinerState(mineur.getId(), "IDLE", null);
            }
        }
        northbar.setMinerCount(map.getMineurs().size());

    }

    // Getter pour les test unitaires
    public GameMap getGameMap() {
        return this.map;
    }

    // OBSEVER PATTERN
    // Méthodes pour gérer les événements de clic
    public void onTileClicked(Point tilePosition, String direction) {
        // Logique pour gérer le clic sur une tile
        Point newTile = calculateNewTileBasedOnDirection(direction);
        if (isTileChangeValid(newTile)) {
            // Modifier la tile courante, son statut exploré etc..
            currentTile.setLocation(newTile);
            map.setCurrentTile(currentTile); // Mettre à jour la tile courante dans le modèle qui notifie la minimap et
                                             // la map
        }
    }

    // Définis la tuile à afficher en fonction de la porte clické par l'utilisateur
    public Point calculateNewTileBasedOnDirection(String direction) {
        direction = direction.toLowerCase(); // S'assurer que les string match
        // Calculer la nouvelle tuile en fonction de la direction
        if (direction.equals("north")) {
            return new Point(currentTile.x, currentTile.y - 1);
        } else if (direction.equals("south")) {
            return new Point(currentTile.x, currentTile.y + 1);
        } else if (direction.equals("east")) {
            return new Point(currentTile.x + 1, currentTile.y);
        } else if (direction.equals("west")) {
            return new Point(currentTile.x - 1, currentTile.y);
        }
        return currentTile;
    }

    // Vérifie si le changement de tuile est valide
    public boolean isTileChangeValid(Point newTile) {
        // Vérifie si la nouvelle tuile est dans les limites de la grille
        return newTile.x >= 0 && newTile.x < map.getWidth() &&
                newTile.y >= 0 && newTile.y < map.getHeight();
    }

    // Méthodes pour gérer les événements de survol de minerais
    public void onTileHoverOre(Point tilePosition, boolean isHovering, int id, String type) {
        if (isHovering) {
            // Change l'etat de la variable de survol pour indiquer que la souris est sur un
            // minerai
            currentHoverState = HoverState.ORE;
        } else if (currentHoverState == HoverState.ORE) {
            currentHoverState = HoverState.NONE;
        }
        updateCursorBasedOnHoverState();
    }

    // Méthodes pour gérer les événements de survol de porte
    public void onTileHoverOpening(Point tilePosition, boolean isHovering, String hoverDirection) {
        if (isHovering) {
            // Change l'etat de la variable de survol pour indiquer que la souris est sur
            // une ouverture
            currentHoverState = HoverState.OPENING;
        } else if (currentHoverState == HoverState.OPENING) {
            currentHoverState = HoverState.NONE;
        }
        updateCursorBasedOnHoverState();
    }

    public void onMinerHover(int id, boolean isHovering) {
        if (isHovering) {
            currentHoverState = HoverState.MINER;
        } else if (currentHoverState == HoverState.MINER) {
            currentHoverState = HoverState.NONE;
        }
        updateCursorBasedOnHoverState();
    }

    public void onVoleurHover(int id, boolean isHovering) {
        if (isHovering) {
            currentHoverState = HoverState.VOLEUR;
        } else if (currentHoverState == HoverState.VOLEUR) {
            currentHoverState = HoverState.NONE;
        }
        updateCursorBasedOnHoverState();
    }

    // Met à jour le curseur en fonction de l'état de survol
    private void updateCursorBasedOnHoverState() {
        switch (currentHoverState) {
            case ORE:
            case OPENING:
            case MINER:
            case VOLEUR:
                mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
            case NONE:
                mapPanel.setCursor(Cursor.getDefaultCursor());
                break;
        }
    }

    @Override
    public void onNextLevel() {
        // Code pour réinitialiser et redémarrer le jeu
        System.out.println("Passer au niveau suivant");
        nextStage();
    }
}
