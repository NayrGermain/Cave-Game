package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import model.Constants;
import model.DTO.MiniMapDTO;
import model.observers.TravelObserver;
import model.observers.MiniMapObserver;

public class MiniMapPanel extends JPanel implements MiniMapObserver, TravelObserver {
    // VARIABLES
    // DTO pour les données du modèle map notifié par le modèle
    private Point currentTile;
    private boolean[][] exploredTiles;
    private Point startPoint;
    private Point exitPoint;
    private boolean[][][] walls; // Affiche les chemin du Maze
    private Map<Integer, Point> minerPositions;
    private Map<Integer, Point> voleurPositions;

    // CONSTRUCTEUR
    public MiniMapPanel() {
        setPreferredSize(new Dimension(200, 200)); // Taille arbitraire pour la minimap
        setOpaque(false); // Rendre le panel non opaque
        this.minerPositions = new HashMap<>();
    }

    // MÉTHODES
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (exploredTiles == null || walls == null) {
            return;
        }

        // Calculer la taille de la tuile pour tenir compte de l'espace pour les murs
        int tileSize = Math.min(
                getWidth() / Constants.MAP_WIDTH,
                getHeight() / Constants.MAP_HEIGHT);

        // Dessiner les tuiles et les murs
        for (int x = 0; x < Constants.MAP_WIDTH; x++) {
            for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
                int tileX = x * tileSize;
                int tileY = y * tileSize;

                // Dessiner la tuile
                if (exploredTiles[x][y]) {
                    // transparent light gray
                    g.setColor(new Color(192, 192, 192, 128)); // Couleur pour les tuiles explorées
                } else {
                    // transparent dark gray
                    g.setColor(new Color(128, 128, 128, 128)); // Couleur pour les tuiles non explorées
                }
                g.fillRect(tileX, tileY, tileSize, tileSize);

                // Dessiner les murs
                g.setColor(Color.BLACK);
                if (!walls[x][y][0]) { // Mur au nord
                    g.drawLine(tileX, tileY, tileX + tileSize, tileY);
                }
                if (!walls[x][y][1]) { // Mur à l'est
                    g.drawLine(tileX + tileSize, tileY, tileX + tileSize, tileY + tileSize);
                }
                if (!walls[x][y][2]) { // Mur au sud
                    g.drawLine(tileX, tileY + tileSize, tileX + tileSize, tileY + tileSize);
                }
                if (!walls[x][y][3]) { // Mur à l'ouest
                    g.drawLine(tileX, tileY, tileX, tileY + tileSize);
                }

                // Marquer la tuile spéciale
                if (currentTile != null && currentTile.equals(new Point(x, y))) {
                    g.setColor(Color.GREEN); // Tuile actuelle
                    g.drawRect(tileX + 1, tileY + 1, tileSize - 2, tileSize - 2);
                }
                if (startPoint != null && startPoint.equals(new Point(x, y))) {
                    g.setColor(Color.BLUE); // Point de départ
                    g.drawRect(tileX + 1, tileY + 1, tileSize - 2, tileSize - 2);
                }
                if (exitPoint != null && exitPoint.equals(new Point(x, y))) {
                    g.setColor(Color.RED); // Point de sortie
                    g.drawRect(tileX + 1, tileY + 1, tileSize - 2, tileSize - 2);
                }
                // TODO: ajouter des tuiles spéciales pour les objets, etc.

                // Dessiner les positions des mineurs
                for (Point minerPosition : minerPositions.values()) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(minerPosition.x * tileSize + tileSize / 4, minerPosition.y * tileSize + tileSize / 4,
                            tileSize / 2, tileSize / 2);
                }

                // desinner le fond de la tuile en rouge si le voleur est present
                for (Point voleurPosition : voleurPositions.values()) {
                    g.setColor(new Color(128, 0, 0, 128));
                    g.fillRect(voleurPosition.x * tileSize, voleurPosition.y * tileSize, tileSize, tileSize);
                }
            }
        }
    }

    // Observe les changements dans le modèle et met à jour la minimap
    @Override
    public void onTileChange(MiniMapDTO mapData) {
        // Mettre à jour la tuile actuelle et redessiner la minimap
        this.exploredTiles = mapData.getExploredTiles(); // matrice de boolean pour les tuiles explorées
        this.currentTile = mapData.getCurrentTile(); // tuile actuelle
        this.startPoint = mapData.getStartPoint(); // point de départ
        this.exitPoint = mapData.getExitPoint(); // point d'arrivée
        this.walls = mapData.getWalls(); // La configuration des murs
        this.minerPositions = mapData.getMinerPositions(); // Les positions des mineurs
        this.voleurPositions = mapData.getVoleurPositions(); // Les positions des voleurs

        repaint();
    }

    @Override
    public void onMinerTravel(int id, Point mapPosition, Point positionPixel) {
        // Mettre à jour la position du mineur et redessiner la minimap
        minerPositions.put(id, mapPosition);
        repaint();
    }

    @Override
    public void onVoleurTravel(int id, Point mapPosition, Point positionPixel) {
        voleurPositions.put(id, mapPosition);
        repaint();
    }
}
