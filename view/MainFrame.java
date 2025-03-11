package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import model.Constants;
import model.observers.NextLevelObserver;
import view.Market.MarketPanel;

public class MainFrame extends JFrame {
    // VARIABLES
    private MapPanel mapPanel;
    private MiniMapPanel miniMapPanel;
    private Northbar topPanel;
    private MarketPanel marketPanel;
    private JLayeredPane endGameOverlay;
    private JPanel endGamePanel;

    private JLabel miniMapStatusLabel;

    private NextLevelObserver nextLevelObserver;

    // CONSTRUCTEUR
    public MainFrame(MapPanel mapPanel, MiniMapPanel miniMapPanel, Northbar topPanel, MarketPanel marketPanel) {
        this.mapPanel = mapPanel;
        this.miniMapPanel = miniMapPanel;
        this.topPanel = topPanel;
        this.marketPanel = marketPanel;
        this.endGameOverlay = new JLayeredPane();

        initUI();
        // TODO : -high priority- Déplacer dans l'EventController
        setupKeyBinding(); // evenement de pression de touche
    }

    // MÉTHODES
    private void initUI() {
        setTitle("Cave");
        setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT + 80);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT));

        // Configuration et positionnement de mapPanel
        mapPanel.setBounds(0, 0, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        layeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER); // Ajoute mapPanel en couche de base

        // Configuration et positionnement de marketPanel
        marketPanel.setBounds(0, 0, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        marketPanel.setVisible(false);
        layeredPane.add(marketPanel, JLayeredPane.PALETTE_LAYER); // Ajoute le panel du marché en bas
        // Configuration et positionnement de miniMapPanel
        miniMapPanel.setBounds(Constants.FRAME_WIDTH - Constants.MINIMAP_WIDTH, 0, Constants.MINIMAP_HEIGHT,
                Constants.MINIMAP_HEIGHT);
        layeredPane.add(miniMapPanel, JLayeredPane.PALETTE_LAYER); // Ajoute miniMapPanel au-dessus de mapPanel

        // Configurer le message UX pour la minimap
        miniMapStatusLabel = new JLabel("M Pour ouvrir", SwingConstants.CENTER);
        miniMapStatusLabel.setForeground(new Color(255, 255, 255));
        miniMapStatusLabel.setBounds(Constants.FRAME_WIDTH - Constants.MINIMAP_WIDTH, Constants.MINIMAP_HEIGHT,
                Constants.MINIMAP_WIDTH, 20);
        layeredPane.add(miniMapStatusLabel, JLayeredPane.PALETTE_LAYER);

        // Configuration de endGameOverlay
        endGameOverlay.setBounds(0, 0, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT - 13);
        endGameOverlay.setOpaque(false);
        endGameOverlay.setVisible(false); // Il sera visible uniquement lors de l'affichage de l'overlay de fin de jeu
        layeredPane.add(endGameOverlay, Integer.valueOf(JLayeredPane.MODAL_LAYER));

        // Ajout de layeredPane à MainFrame
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH); // Ajoute le panel d'information en haut
        add(layeredPane, BorderLayout.CENTER); // Ajoute layeredPane (qui contient mapPanel et miniMapPanel)

    }

    public void displayEndGameOverlay(String gameTime) {
        this.endGamePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150)); // Fond semi-transparent
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        endGamePanel.setOpaque(false);
        endGamePanel.setSize(getWidth(), getHeight());

        JLabel messageLabel = new JLabel(
                "<html><center><font color='white'>Félicitations !<br>Vous vous êtes échappé de la grotte.<br>Temps passé : "
                        + gameTime + "</font></center></html>",
                JLabel.CENTER);
        endGamePanel.add(messageLabel, BorderLayout.CENTER);

        JButton nextLevelBtn = new JButton("Niveau suivant");
        endGamePanel.add(nextLevelBtn, BorderLayout.SOUTH);
        nextLevelBtn.addActionListener(e -> {
            if (nextLevelObserver != null) {
                nextLevelObserver.onNextLevel();
            }
        });

        endGameOverlay.setLayout(new BorderLayout()); // Utilisez BorderLayout pour endGameOverlay
        endGameOverlay.add(endGamePanel, BorderLayout.CENTER); // Ajoutez endGamePanel à endGameOverlay
        endGameOverlay.setVisible(true); // afficher l'overlay

        revalidate();
        repaint();
    }

    public void removeEndGameOverlay() {
        endGameOverlay.remove(endGamePanel);
        endGameOverlay.setVisible(false); // Cachez endGameOverlay après la suppression
        revalidate();
        repaint();
    }

    /*********************/
    /***** ACTIONS *******/
    // Méthode pour ajouter un raccourci clavier pour basculer la visibilité de la
    // minimap
    private void setupKeyBinding() {
        JRootPane rootPane = this.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0),
                "toggleMiniMap");
        // La pression de la touche M affiche ou cache la miniMap
        rootPane.getActionMap().put("toggleMiniMap", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMiniMapVisibility();

            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0),
                "toggleShop");
        // La pression de la touche M affiche ou cache le shop
        rootPane.getActionMap().put("toggleShop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleShopVisibility();
            }
        });
    }

    // Méthode pour basculer la visibilité de la minimap
    private void toggleMiniMapVisibility() {
        boolean isVisible = miniMapPanel.isVisible();
        // Inverse la visibilité
        miniMapPanel.setVisible(!isVisible);

        // Met à jour le texte
        miniMapStatusLabel.setText(miniMapPanel.isVisible() ? "M pour cacher" : "M pour ouvrir");

        // Met à jour la position
        if (miniMapPanel.isVisible()) {
            // Si la minimap est maintenant visible, positionner le label en dessous
            miniMapStatusLabel.setBounds(
                    Constants.FRAME_WIDTH - Constants.MINIMAP_WIDTH,
                    Constants.MINIMAP_HEIGHT,
                    Constants.MINIMAP_HEIGHT, 20);
        } else {
            // Si la minimap est cachée, positionner le label au-dessus de sa position
            // normale
            miniMapStatusLabel.setBounds(
                    Constants.FRAME_WIDTH - Constants.MINIMAP_WIDTH,
                    0,
                    Constants.MINIMAP_WIDTH,
                    20);
        }

        repaint();
    }

    private void toggleShopVisibility() {
        marketPanel.setVisible(!marketPanel.isVisible());
        marketPanel.getMarket().changePanelFromState(0);
        repaint();
    }

    // OBSERVER
    public void attachNextLevelObserver(NextLevelObserver nextLevelObserver) {
        this.nextLevelObserver = nextLevelObserver;
    }

}
