package test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.Test;

import controller.GameController;

import model.Tile;
import view.MainFrame;
import view.MapPanel;
import view.Market.MarketPanel;
import view.MiniMapPanel;
import view.Northbar;

public class TestInteraction {
    // Attributs
    private MainFrame frame;
    private MapPanel mapPanel;
    private MiniMapPanel miniMapPanel;
    private GameController gameController;
    private MarketPanel marketPanel;
    private Northbar northbar;

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
    /* Test que le clic sur une ouverture renvois bien la bonne tuile */
    @Test
    public void testClickOnOpening() {
        for (int n = 0; n < 1000; n++) {
            initGame();

            // Récupère la position originale et la tuile courante
            Point originalPoint = gameController.getGameMap().getCurrentTile();
            Tile currentTile = gameController.getGameMap().getTile(originalPoint.x, originalPoint.y);

            // Simuler un clic dans chaque direction si ouvert
            simulateClickInDirection("north", originalPoint, currentTile.isNorthOpen());
            simulateClickInDirection("south", originalPoint, currentTile.isSouthOpen());
            simulateClickInDirection("east", originalPoint, currentTile.isEastOpen());
            simulateClickInDirection("west", originalPoint, currentTile.isWestOpen());
        }
    }

    private void simulateClickInDirection(String direction, Point originalPoint, boolean isOpen) {
        Point expectedNewPoint = new Point(originalPoint.x, originalPoint.y);

        if (isOpen) {
            switch (direction) {
                case "north":
                    expectedNewPoint.y -= 1;
                    break;
                case "south":
                    expectedNewPoint.y += 1;
                    break;
                case "east":
                    expectedNewPoint.x += 1;
                    break;
                case "west":
                    expectedNewPoint.x -= 1;
                    break;
            }

            gameController.onTileClicked(originalPoint, direction); // Simule un clic dans la direction indiquée
            assertEquals("After clicking on an open direction, the current tile should update correctly.",
                    expectedNewPoint, gameController.getGameMap().getCurrentTile()); // Vérifie que la tuile courante a
                                                                                     // été mise à jour
        } else {
            gameController.onTileClicked(originalPoint, direction); // Simule un clic dans la direction
            assertEquals("If the direction is not open, the current tile should remain unchanged.",
                    originalPoint, gameController.getGameMap().getCurrentTile()); // Vérifie que la tuile courante reste
                                                                                  // inchangée
        }
    }

    /********************************************************* *************/
    /* Test si le hover est bien détécté sur les ouvertures */
    // A venir
}
