package test;

import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Test;

import controller.GameController;
import model.Constants;
import model.Tile;
import view.MainFrame;
import view.MapPanel;
import view.MiniMapPanel;
import view.Northbar;
import view.Market.MarketPanel;

public class TestMap {
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

    /********************************************************* */
    /***** Une carte est composé d'une entrée ET d'une sortie */
    @Test
    public void testStartAndExitTilesArePresent() {
        for (int n = 0; n < 1000; n++) {
            initGame();
            Tile startTile = gameController.getGameMap().getTile(gameController.getGameMap().getStartPoint().x,
                    gameController.getGameMap().getStartPoint().y);
            Tile exitTile = gameController.getGameMap().getTile(gameController.getGameMap().getExitPoint().x,
                    gameController.getGameMap().getExitPoint().y);

            // Vérifier si la tuile de départ a été correctement marquée
            assertTrue(startTile.isStart());

            // Vérifier si la tuile de sortie a été correctement marquée
            assertTrue(exitTile.isExit());
        }
    }

    /***********************************/
    /****** TEST Entrée => Sortie ********/
    @Test
    public void TestTheStartLeadToExit() {
        // 10000 génrations de map
        for (int n = 0; n < 1000; n++) {
            initGame();
            assertTrue(bfs(gameController.getGameMap().getTiles(), gameController.getGameMap().getStartPoint(),
                    gameController.getGameMap().getExitPoint()));
        }
    }

    public boolean bfs(Tile[][] tiles, Point entry, Point exit) {
        boolean[][] visited = new boolean[tiles.length][tiles[0].length]; // Matrice pour marquer les tuiles visitées
        Queue<Point> queue = new LinkedList<>(); // File pour le parcours en largeur
        queue.add(entry);
        visited[entry.x][entry.y] = true;

        // Parcours en largeur
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.equals(exit)) { // cas de base: sortie trouvée
                return true;
            }

            // Ajoutez les voisins non visités et accessible à la file
            for (Point neighborPos : getNeighbors(current.x, current.y, tiles)) {
                if (!visited[neighborPos.x][neighborPos.y]) {
                    queue.add(neighborPos);
                    visited[neighborPos.x][neighborPos.y] = true;
                }
            }
        }

        return false; // Sortie non trouvée
    }

    // Renvoie les voisins accessibles d'une tuile
    private List<Point> getNeighbors(int x, int y, Tile[][] tiles) {
        List<Point> neighbors = new ArrayList<>(); // Liste pour stocker les voisins
        int width = tiles.length; // Largeur de la grille
        int height = tiles[0].length; // Hauteur de la grille

        // Nord
        if (y > 0 && tiles[x][y - 1].isSouthOpen())
            neighbors.add(new Point(x, y - 1));
        // Sud
        if (y < height - 1 && tiles[x][y].isSouthOpen())
            neighbors.add(new Point(x, y + 1));
        // Est
        if (x < width - 1 && tiles[x][y].isEastOpen())
            neighbors.add(new Point(x + 1, y));
        // Ouest
        if (x > 0 && tiles[x - 1][y].isEastOpen())
            neighbors.add(new Point(x - 1, y));

        return neighbors;
    }

    /********************************************* */
    /* Toutes les tuiles ont au moins une ouverture */
    @Test
    public void AllTilesHaveAtLeastOneOpening() {
        for (int n = 0; n < 1000; n++) {
            initGame();
            for (int i = 0; i < Constants.MAP_WIDTH; i++) {
                for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                    Tile tile = gameController.getGameMap().getTile(i, j);
                    assertTrue(tile.isNorthOpen() || tile.isSouthOpen() || tile.isEastOpen() || tile.isWestOpen());
                }
            }
        }
    }
}
