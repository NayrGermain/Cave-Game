
import javax.swing.SwingUtilities;

import controller.GameController;
import view.MainFrame;
import view.MapPanel;
import view.Market.MarketPanel;
import view.MiniMapPanel;
import view.Northbar;

public class App {

    public static void main(String[] args) {
        // Méthode principale pour démarrer l'application
        SwingUtilities.invokeLater(() -> {

            // VIEW
            MapPanel mapPanel = new MapPanel(); // l'affichage de la map tuile par tuile
            MiniMapPanel miniMapPanel = new MiniMapPanel(); // une minimap pour l'ensemble des tuiles
            Northbar northbar = new Northbar(); // la barre d'information en haut de l'écran
            MarketPanel marketPanel = new MarketPanel(); // le marché

            MainFrame frame = new MainFrame(mapPanel, miniMapPanel, northbar, marketPanel); // la fenêtre principale

            // CONTROLLER
            new GameController(mapPanel, miniMapPanel, marketPanel, northbar, frame);

            frame.setVisible(true);
            frame.setResizable(false); 
        });

    }
}
