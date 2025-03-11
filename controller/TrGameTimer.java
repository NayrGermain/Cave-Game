package controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.GameMap;
import model.Storage;
import model.Tile;
import model.characters.Miner;
import model.characters.MiningVoleurAction;
import model.characters.Voleur;
import model.Constants;
import view.MapPanel;
import view.MiniMapPanel;
import view.Northbar;

public class TrGameTimer extends Thread {
    // ATTRIBUTS
    private Northbar northbar;
    private GameMap gameMap;
    private MapPanel mapPanel;
    private MiniMapPanel miniMapPanel;
    private Storage storage;
    private TrVoleurManager actionManager;
    private boolean isRunning;
    private long startTime;
    private long currentTime;
    private long elapsedTime;
    private Random random;

    // CONSTRUCTEUR
    public TrGameTimer(Northbar northbar, GameMap gameMap, MapPanel mapPanel, MiniMapPanel miniMapPanel) {
        this.northbar = northbar;
        this.gameMap = gameMap;
        this.mapPanel = mapPanel;
        this.miniMapPanel = miniMapPanel;
        this.actionManager = TrVoleurManager.getInstance();
        this.storage = Storage.getInstance();

        startTime = System.currentTimeMillis();
        random = new Random(); // Initialisation de l'objet Random

        isRunning = true;
    }

    // THREAD
    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                Thread.sleep(1000); // Attendre une seconde
                currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - startTime;

                // Incrémenter les piéce du joueur chaque seconde
                storage.addStorage("money", 1);

                northbar.setTime(formatTime(elapsedTime)); // Mettre à jour le temps dans la fenêtre

                // Toutes les 30 secondes, faire apparaitre un voleur dans la limite défini par
                // la constante
                if (gameMap.getVoleurs().size() < Constants.MAX_THIEF && (elapsedTime / 1000) % 30 == 0) {
                    spawnThief();
                }

            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    }

    // METHODS

    // Méthode pour faire apparaitre un voleur
    private void spawnThief() {
        List<Miner> miners = new ArrayList<>(gameMap.getMineurs().values());
        if (!miners.isEmpty()) {
            Miner miner = miners.get(random.nextInt(miners.size()));
            Point positionMap = miner.getPositionMap();
            Tile minerTile = gameMap.getTile(positionMap.x, positionMap.y);

            // Créer un voleur à la position d'un mineur
            Voleur voleur = new Voleur(new Point(positionMap.x, positionMap.y), 4, 1, minerTile.getMineralInstances(),
                    10);
            gameMap.addVoleur(voleur);
            voleur.addObserver(mapPanel); // Abonnez le voleur aux changements dans GameMap

            // afficher la liste des voleurs
            MiningVoleurAction miningVoleurAction = new MiningVoleurAction(gameMap, voleur, positionMap, minerTile);
            miningVoleurAction.attachObserver(mapPanel); // Abonnement pour le changement d'état des minerais
            miningVoleurAction.attachNextTileObserver(miniMapPanel); // abonnement pour le changement de tuile sur la
                                                                     // minimap
            miningVoleurAction.attachNextTileObserver(mapPanel); // abonnement pour le changement de tuile sur la map
            actionManager.addMiningAction(voleur, miningVoleurAction);
        }
    }

    // Fonction pour formater le temps en minutes et secondes
    private String formatTime(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);
        return timeString;
    }

    public String getTime() {
        return formatTime(elapsedTime);
    }

    // Interrupt sans interrompre le thread
    public void stopRunning() {
        isRunning = false;
    }

    // Remettre les paramètres à leurs états initiaux
    public void restart() {
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
        isRunning = true;
    }
}