package test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import controller.GameController;
import model.Constants;
import view.MainFrame;
import view.MapPanel;
import view.MiniMapPanel;
import view.Northbar;
import view.Market.MarketPanel;

public class TestMiniMap {
    // Attributs
    private MainFrame frame;
    private MapPanel mapPanel;
    private MiniMapPanel miniMapPanel;
    private MarketPanel marketPanel;
    private Northbar northbar;
    private GameController gameController;

    // init game
    public void initGame() {
        this.mapPanel = new MapPanel();
        this.miniMapPanel = new MiniMapPanel();
        this.marketPanel = new MarketPanel();
        this.northbar = new Northbar();
        this.frame = new MainFrame(mapPanel, miniMapPanel, northbar, marketPanel);

        this.gameController = new GameController(mapPanel, miniMapPanel, marketPanel, northbar, frame);
    }

    /********************************************************* *************/
    /* Test si les tuiles exploré son bien marqué comme tel sur la minimap */
    @Test
    public void testMinimapExploredTiles() {
        for (int n = 0; n < 1000; n++) {
            initGame();
            // Simuler la visite de quelques tuiles
            List<Point> pointsVisited = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                // selectionner les tuiles de manière aléatoire
                Point p = new Point((int) (Math.random() * Constants.MAP_WIDTH),
                        (int) (Math.random() * Constants.MAP_HEIGHT));
                gameController.getGameMap().getTile(p.x, p.y).setExplored(true);
                pointsVisited.add(p);
            }

            gameController.getGameMap().setCurrentTile(new Point(1, 0));

            // Vérifier si la minimap a marqué ces tuiles comme explorées
            // for (Point p : pointsVisited) {
            // // CE TEST NECESSITE DE METTRE exploredTiles EN PUBLIC
            // assertTrue(miniMapPanel.exploredTiles[p.x][p.y] == true);
            // }
        }
    }

    /********************************************************* *************/
    /* Test si les tuiles d'entrée et de sortie sont bien placé */
    @Test
    public void testStartAndExitTilesArePresent() {
        for (int n = 0; n < 1000; n++) {
            initGame();

            // CE TEST NECESSITE DE METTRE startPoint et exitPoint EN PUBLIC

            // Vérifier si la tuile de départ a été correctement marquée
            // assertTrue(map.getStartPoint() == miniMapPanel.startPoint);

            // Vérifier si la tuile de sortie a été correctement marquée
            // assertTrue(map.getExitPoint() == miniMapPanel.exitPoint);
        }
    }

    /********************************************************* *************/
    /* Test le positionnement des murs */
    // A venir necessite de restructurer Tile

}
