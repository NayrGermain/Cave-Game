package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import controller.GameController;
import model.Constants;
import model.Tile;
import view.MainFrame;
import view.MapPanel;
import view.MiniMapPanel;
import view.Northbar;
import view.Market.MarketPanel;

public class TestTile {
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
    /*
     * Confirmer que chaque celulle de la tuile a une image correspondant a la
     * disposition attendue
     */
    @Test
    public void TheEdgeOfTheMapIsEitherACornerABorderOrAnOpeningsElseFloor() {
        for (int n = 0; n < 100; n++) {
            initGame();
            for (int x = 0; x < Constants.MAP_WIDTH; x++) {
                for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
                    Tile tile = gameController.getGameMap().getTile(x, y);

                    for (int i = 0; i < Constants.LAYOUT_NUMBER_OFCUT - 1; i++) {
                        for (int j = 0; j < Constants.LAYOUT_NUMBER_OFCUT - 1; j++) {
                            if (i == 0 && j == 0) { // Coin supérieur gauche
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.TOP_LEFT_BORDER);
                            } else if (i == 0 && j == Constants.MAP_HEIGHT - 1) { // Coin inférieur gauche
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.BOTTOM_LEFT_BORDER);
                            } else if (i == Constants.MAP_WIDTH - 1 && j == 0) { // Coin supérieur droit
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.TOP_RIGHT_BORDER);
                            } else if (i == Constants.MAP_WIDTH - 1 && j == Constants.MAP_HEIGHT - 1) { // Coin
                                                                                                        // inférieur
                                                                                                        // droit
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.BOTTOM_RIGHT_BORDER);
                            } else if (j == 0) { // Bordure gauche
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.LEFT_BORDER || tile.isWestOpen());
                            } else if (i == Constants.MAP_WIDTH - 1) { // Bordure droite
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.RIGHT_BORDER || tile.isEastOpen());
                            } else if (i == 0) { // Bordure supérieure
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.TOP_BORDER || tile.isNorthOpen());
                            } else if (j == Constants.MAP_HEIGHT - 1) { // Bordure inférieure
                                assertTrue(
                                        tile.getGridIndexValue(i, j) == Constants.BOTTOM_BORDER || tile.isSouthOpen());
                            } else {
                                assertTrue(tile.getGridIndexValue(i, j) == Constants.FLOOR
                                        || tile.getGridIndexValue(i, j) == Constants.OBSTACLE
                                        || tile.getGridIndexValue(i, j) == Constants.EXIT_ASSET
                                        || tile.getGridIndexValue(i, j) == Constants.START_ASSET);
                            }
                        }
                    }

                }
            }
        }
    }

}
