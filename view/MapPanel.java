package view;

import model.DTO.*;
import model.Items.OreInfo;
import model.observers.*;
import model.observers.MapObserver;
import model.observers.MinerOpenDoorObserver;
import model.observers.MinerPositionObserver;
import model.observers.OreMiningObserver;
import model.observers.OreRegeneratedObserver;
import model.observers.ActionFinishedObserver;
import model.observers.OresObserver;
import utils.ImageLoader;

import javax.swing.JPanel;

import controller.TrActionManager;
import controller.TrVoleurManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

public class MapPanel extends JPanel
        implements MapObserver, OresObserver, OreMiningObserver, MinerPositionObserver, VoleurPositionObserver,
        TravelObserver,
        OreRegeneratedObserver, ActionFinishedObserver, MinerOpenDoorObserver, CatchingThiefObserver {
    // VARIABLES
    private int[][] tilesType; // Matrice de type de tuile
    private Point currentTile; // Tuile actuelle
    private ImageLoader imageLoader;

    // DTO <- Observers
    private Map<Point, OreInfo> oresInfo; // Stocke les minerais de la tuile
    private Map<Point, Map<Integer, DoorDetailsDTO>> doorsProgress; // Stocke la progression de la porte par tuile
    private Map<Integer, MineralDetailsDTO> miningProgress; // Stocke la progression du minage par ID
                                                            // de minerai
    private Map<Point, Map<Integer, VoleurDTO>> catchingProgress; // Stocke la progression de la porte par tuile

    private Map<Integer, Point> oreIdToPosition;
    private int spriteNum;
    private int spriteNumVoleur;
    private int spriteCounter;

    private int moneyLooted;
    private long lastDrawMoneyTime;

    // Variables pour afficher les détails du minerai dans une fenetre dessinée
    private boolean showOreDetails;
    private Point oreDetailsPosition;
    private String oreType, oreState;
    private int oreQuantity;
    private int timeRemaining;

    // Variables pour afficher les détails de la porte dans une fenetre dessinée
    private boolean showDoorDetails;
    private String doorDetails;
    private List<String> requiredTools;
    private Point doorDetailsPosition;
    private int unlockingTime;

    // Variable pour afficher l'inventaire du mineur selected
    private Point inventoryPosition;

    // message d'alerte (ex : X outils requis pour miner...)
    private String notificationMessage;
    private long messageDisplayTime;

    // Classe interne pour stocker les informations de rendu du mineur
    // Simplifie la lecture et optimise les opération de mise à jour
    private class MinerRenderInfo {
        private Point position;
        private String state;
        private List<String> inventory;

        public MinerRenderInfo(Point position, String state, List<String> inventory) {
            this.position = position;
            this.state = state;
            this.inventory = inventory;
        }
    }

    private Map<Integer, MinerRenderInfo> minerRenderInfoMap;

    // Classe interne pour stocker les informations de rendu du voleur
    private class VoleurRenderInfo {
        private Point position;
        private String state;

        public VoleurRenderInfo(Point position, String state) {
            this.position = position;
            this.state = state;
        }
    }

    private Map<Integer, VoleurRenderInfo> voleurRenderInfoMap;

    // CONSTRUCTEUR
    public MapPanel() {
        this.spriteNum = 1;
        this.spriteNumVoleur = 1;
        this.spriteCounter = 0;
        this.notificationMessage = "";
        this.messageDisplayTime = 0;

        this.showOreDetails = false;
        this.oreDetailsPosition = new Point();
        this.timeRemaining = 0;

        this.showDoorDetails = false;
        this.doorDetails = "";
        this.requiredTools = new ArrayList<>();
        this.doorDetailsPosition = new Point();

        this.moneyLooted = 0;
        this.lastDrawMoneyTime = 0;

        this.imageLoader = new ImageLoader();

        this.oresInfo = new HashMap<>();
        this.doorsProgress = new HashMap<>();
        this.miningProgress = new HashMap<>();
        this.catchingProgress = new HashMap<>();
        this.oreIdToPosition = new HashMap<>();
        this.minerRenderInfoMap = new HashMap<>();
        this.voleurRenderInfoMap = new HashMap<>();

        // Pour chaque mineur de départ, mettre leurs étate à idle pour l'animation
        setMinerState(1, "IDLE", null);
        setMinerState(2, "IDLE", null);

        // Pour le voleur de départ, mettre son état à idle
        TrActionManager.getInstance().attachOreRegenObserver(this);
        TrActionManager.getInstance().attachedFinishedObserver(this);
        TrVoleurManager.getInstance().attachedFinishedObserver(this);
        TrVoleurManager.getInstance().attachOreRegenObserver(this);
    }

    // SETTERS
    // Trouver et mettre à jour la position du mineur
    public void addNewMiner(int id, Point positionPixel, List<String> inventory) {
        // Vérifier si le mineur existe déjà pour éviter les doublons
        if (!minerRenderInfoMap.containsKey(id)) {
            minerRenderInfoMap.put(id, new MinerRenderInfo(positionPixel, "IDLE", inventory));
        }
        repaint();
    }

    public void setMinerState(int id, String state, List<String> inventory) {
        MinerRenderInfo minerInfo = minerRenderInfoMap.get(id);
        if (minerInfo != null) {
            minerInfo.state = state;
            minerInfo.inventory = inventory;
        } else {
            minerRenderInfoMap.put(id, new MinerRenderInfo(new Point(), state, inventory));
        }
        repaint();
    }

    // Trouver et mettre à jour la position du voleur
    public void setVoleurState(int id, String state) {
        VoleurRenderInfo voleurInfo = voleurRenderInfoMap.get(id);
        if (voleurInfo != null) {
            voleurInfo.state = state;
        } else {
            voleurRenderInfoMap.put(id, new VoleurRenderInfo(new Point(), state));
        }
        repaint();
    }

    // Ajouter un nouveau mineur à la tuile
    public void setBoughtMiner(int id, Point position, List<String> inventory) {
        minerRenderInfoMap.put(id, new MinerRenderInfo(position, "IDLE", inventory));
        repaint();
    }

    // MÉTHODES
    // Logique pour dessiner la tuile
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Calculez la taille de chaque segment pour remplir la tuile actuelle sur le
        // panel
        int segmentSizeWidth = getWidth() / tilesType[0].length;
        int segmentSizeHeight = getHeight() / tilesType.length;

        if (tilesType != null && currentTile != null) {

            // Parcourir la grille de la tuile actuelle et dessinez chaque segment
            for (int i = 0; i < tilesType.length; i++) {
                for (int j = 0; j < tilesType[i].length; j++) {
                    int x = j * segmentSizeWidth;
                    int y = i * segmentSizeHeight;
                    // Selectionnez l'image de la tuile en fonction du type
                    Image tileImage = selectTileImageBasedOnType(tilesType[i][j], imageLoader, x, y,
                            segmentSizeWidth,
                            segmentSizeHeight);

                    if (tileImage != null) {
                        g.drawImage(tileImage, x, y, segmentSizeWidth, segmentSizeHeight, null);
                    } else {
                        g.fillRect(x, y, segmentSizeWidth, segmentSizeHeight);
                    }
                }
            }

            // Dessinez les minerais pour la tuile actuelle
            for (Map.Entry<Point, OreInfo> entry : oresInfo.entrySet()) {
                Point position = entry.getKey();
                OreInfo oreInfo = entry.getValue();

                // Calculez la position exacte du minerai dans le segment
                int mineralX = position.x * segmentSizeWidth;
                int mineralY = position.y * segmentSizeHeight;
                Image mineralTile = getMineralImage(oreInfo.getType(), oreInfo.getState(), imageLoader); // Récupère
                                                                                                         // l'image
                                                                                                         // du
                                                                                                         // minerai

                Dimension adjustedSize = getAdjustedSizeForState(oreInfo.getState(), segmentSizeWidth,
                        segmentSizeHeight); // Ajuster la taille du minerai selon l'état

                if (mineralTile != null) {
                    // Ajustez la position et la taille si nécessaire
                    int adjustedX = mineralX + (segmentSizeWidth - adjustedSize.width) / 2;
                    int adjustedY = mineralY + (segmentSizeHeight - adjustedSize.height) / 2;
                    g.drawImage(mineralTile, adjustedX, adjustedY, adjustedSize.width, adjustedSize.height, null);
                } else {
                    // Dessin alternatif si l'image du minerai n'est pas disponible
                    int radius = Math.min(adjustedSize.width, adjustedSize.height) / 2;
                    g.fillOval(mineralX + segmentSizeWidth / 2 - radius, mineralY + segmentSizeHeight / 2 - radius,
                            radius * 2, radius * 2);
                }
            }

            // Dessinez la progression du minage pour chaque minerai
            for (Map.Entry<Integer, MineralDetailsDTO> entry : miningProgress.entrySet()) {
                int oreId = entry.getKey();
                int progress = entry.getValue().getOreInfo().getHarvestTime();
                Point position = oreIdToPosition.get(oreId);
                if (position != null) {
                    // dessiner le cercle de progression du minage pour chaque minerai
                    int x = position.x * segmentSizeWidth;
                    int y = position.y * segmentSizeHeight;
                    drawMiningCircle(g, x, y, segmentSizeWidth, segmentSizeHeight, progress);

                    // Dessiner la quantité minée pour les minerais dont le minage est en cours
                    int quantity = entry.getValue().getOreInfo().getQuantity();
                    if (quantity != 0) {
                        Image icon = getMineralImage(entry.getValue().getOreInfo().getType(), "EMPTY", imageLoader);
                        drawMinedQuantity(g, x, y, segmentSizeWidth, segmentSizeHeight, quantity,
                                icon);
                    }
                }

            }

            // Dessiner le message de notification d'alerte si demandé
            if (!notificationMessage.isEmpty() && System.currentTimeMillis() <= messageDisplayTime) {
                // dessine la notification
                drawNotificationMessage(g, notificationMessage);
            } else if (System.currentTimeMillis() > messageDisplayTime) {
                notificationMessage = ""; // Effacer le message après son temps d'affichage
            }

        }

        // Met a jour les keyFrame pour animer les images
        this.updateFrame();

        // Dessinez les mineurs pour la tuile actuelle
        for (Map.Entry<Integer, MinerRenderInfo> entry : minerRenderInfoMap.entrySet()) {
            MinerRenderInfo minerInfo = entry.getValue();
            Point pixelPosition = minerInfo.position;

            int minerWidth = segmentSizeWidth / 2;
            int minerHeight = segmentSizeHeight / 2;

            int x = pixelPosition.x - minerWidth / 2; // Centre le mineur sur la position
            int y = pixelPosition.y - minerHeight / 2;

            // Vérifiez si le mineur est en dehors des limites de la tuile et ajustez si
            // nécessaire
            x = Math.max(0, Math.min(x, getWidth() - minerWidth));
            y = Math.max(0, Math.min(y, getHeight() - minerHeight));

            // Dessinez l'image du mineur en fonction de son état
            Image minerImage = loadMinerImage(imageLoader, minerInfo.state);
            if (minerImage != null) {
                g.drawImage(minerImage, x, y, minerWidth, minerHeight, null);
            }

            if (moneyLooted > 0) {
                Image moneyIcon = imageLoader.getImage("MONEY");
                drawMinedQuantity(g, x, y, segmentSizeWidth, segmentSizeHeight, moneyLooted, moneyIcon);

                // afficher le message 2sec puis reset lastDraw et moneyLooted
                if (System.currentTimeMillis() - lastDrawMoneyTime >= 2000) {
                    moneyLooted = 0;
                    lastDrawMoneyTime = 0;
                }

            }

            // Dessinez l'inventaire du mineur
            if (minerInfo != null && minerInfo.state.equals("SELECTED") && minerInfo.inventory != null) {
                inventoryPosition = new Point(x, y + minerHeight);
                drawMinerStorage(g, minerInfo);
            }
        }

        // Dessinez les voleurs pour la tuile actuelle
        for (

        Map.Entry<Integer, VoleurRenderInfo> entry : voleurRenderInfoMap.entrySet()) {
            VoleurRenderInfo thiefInfo = entry.getValue();
            Point pixelPosition = thiefInfo.position;

            int thiefWidth = segmentSizeWidth / 2;
            int thiefHeight = segmentSizeHeight / 2;

            int x = pixelPosition.x - thiefWidth / 2; // Centre le voleur sur la position
            int y = pixelPosition.y - thiefHeight / 2;

            // Vérifiez si le voleur est en dehors des limites de la tuile et ajustez si
            // nécessaire
            x = Math.max(0, Math.min(x, getWidth() - thiefWidth));
            y = Math.max(0, Math.min(y, getHeight() - thiefHeight));

            // Dessinez l'image du voleur en fonction de son état
            Image voleurImage = loadVoleurImage(imageLoader, thiefInfo.state);
            if (voleurImage != null) {
                g.drawImage(voleurImage, x, y, thiefWidth, thiefHeight, null);
            }
        }

        // Dessinez la progression de déblocage pour chaque porte
        Map<Integer, DoorDetailsDTO> doorsAtPosition = doorsProgress.get(currentTile);

        if (doorsAtPosition != null && !doorsAtPosition.isEmpty()) {
            for (DoorDetailsDTO info : doorsAtPosition.values()) {
                int x = info.getDoorPosition().y * segmentSizeWidth;
                int y = info.getDoorPosition().x * segmentSizeHeight;

                drawUnlockProgress(g, x, y, segmentSizeWidth, info.getRemainingTime(),
                        info.getTotalTime());
            }
        }

        // Desinnez la barre de progression pour chaque capture en cours
        Map<Integer, VoleurDTO> voleursAtPosition = catchingProgress.get(currentTile);
        if (voleursAtPosition != null && !voleursAtPosition.isEmpty()) {
            for (VoleurDTO info : voleursAtPosition.values()) {
                int x = info.getPositionPixel().y;
                int y = info.getPositionPixel().x;

                drawUnlockProgress(g, x, y, segmentSizeWidth, info.getCaptureTime(),
                        info.getMaxCaptureTime());
            }
        }

        // Afficher les détails du minerai si demandé
        if (showOreDetails) {
            // Calcule la position pour que la fenêtre ne dépasse pas les limites de
            // MapPanel
            int dialogWidth = 300; // Largeur ajustée pour plus d'espace
            int dialogHeight = 200 + 50 * requiredTools.size(); // Hauteur ajustée selon le nombre d'outils
            int x = Math.min(oreDetailsPosition.x, getWidth() - dialogWidth);
            int y = Math.min(oreDetailsPosition.y, getHeight() - dialogHeight);

            g.setColor(new Color(70, 70, 70)); // Couleur de fond adaptée au thème minier
            g.fillRect(x, y, dialogWidth, dialogHeight); // Dessine le fond de la fenêtre
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, dialogWidth, dialogHeight); // Dessine le bord de la fenêtre

            // Header avec le nom du minerai
            g.setColor(new Color(120, 100, 90)); // Couleur terre pour le header
            g.fillRect(x, y, dialogWidth, 30); // Dessine le fond du header
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(oreType + " Details", x + 10, y + 20);

            // Body avec les détails du minerai
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Etat: " + oreState, x + 10, y + 50);
            g.drawString("Temps de minage restant: " + timeRemaining + "s", x + 10, y + 80);
            g.drawString("Quantité initiale: " + oreQuantity, x + 10, y + 95);

            // Section pour les outils requis avec images
            int startY = y + 120;
            for (int i = 0; i < requiredTools.size(); i++) {
                Image pickAxeImg = imageLoader.getAxeIcon(requiredTools.get(i));
                g.setColor(new Color(200, 180, 160)); // Couleur claire pour le fond des images
                // Dessine l'image de l'outil
                g.fillRect(x + 10, startY + i * 50, 30, 30); // Espace pour l'image de l'outil
                g.drawImage(pickAxeImg, x + 10, startY + i * 50, 30, 30, null);
                g.setColor(Color.WHITE);
                g.drawRect(x + 10, startY + i * 50, 30, 30); // Bord de l'image
                g.drawString(requiredTools.get(i), x + 60, startY + i * 50 + 20); // Nom de l'outil à côté
            }
        }

        // Afficher les détails de la porte si demandé
        if (showDoorDetails) {
            // Calcule la position pour que la fenêtre ne dépasse pas les limites de
            // MapPanel
            int dialogWidth = 300;
            int dialogHeight = 150 + 50 * requiredTools.size(); // Ajusté en fonction du nombre d'outils
            int x = Math.min(doorDetailsPosition.x, getWidth() - dialogWidth);
            int y = Math.min(doorDetailsPosition.y, getHeight() - dialogHeight);

            g.setColor(new Color(80, 70, 60)); // Couleur de fond terre
            g.fillRect(x, y, dialogWidth, dialogHeight); // Dessine le fond de la fenêtre
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, dialogWidth, dialogHeight); // Dessine le bord de la fenêtre

            // Header
            g.setColor(new Color(140, 110, 100)); // Couleur brun-rougeâtre pour le header
            g.fillRect(x, y, dialogWidth, 30); // Dessine le fond du header
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(doorDetails + " bloquée", x + 10, y + 20);

            // Ajout du texte descriptif pour les outils requis
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Temps de déblocage: " + unlockingTime + " sec.", x + 10, y + 50);
            g.drawString("Outils requis pour débloquer la porte:", x + 10, y + 70);

            // Section pour les outils requis avec images
            int startY = y + 90;
            for (int i = 0; i < requiredTools.size(); i++) {
                Image pickAxeImg = imageLoader.getAxeIcon(requiredTools.get(i));
                g.setColor(new Color(200, 180, 160)); // Couleur claire pour le fond des images
                // Dessine l'image de l'outil
                g.fillRect(x + 10, startY + i * 50, 30, 30); // Espace pour l'image de l'outil
                g.drawImage(pickAxeImg, x + 10, startY + i * 50, 30, 30, null);
                g.setColor(Color.WHITE);
                g.drawRect(x + 10, startY + i * 50, 30, 30); // Bord de l'image
                g.drawString(requiredTools.get(i), x + 60, startY + i * 50 + 20); // Nom de l'outil à côté
            }
        }

        // ajouter un message dans le coin inferieur droit pour le menu
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Appuyer sur S pour le shop",

                getWidth() - 250, getHeight() - 20);
    }

    // Charge l'image du mineur en fonction de la frame et de l'état
    private Image loadMinerImage(ImageLoader imageLoader, String state) {
        // getMinerImage construit le string souhaité pour l'image du mineur
        return imageLoader.getMinerImage(state, spriteNum);
    }

    // Charge l'image du voleur en fonction de la frame et de l'état
    private Image loadVoleurImage(ImageLoader imageLoader, String state) {
        // getVoleurImage construit le string souhaité pour l'image du voleur
        return imageLoader.getVoleurImage(state, spriteNumVoleur);
    }

    // Sélectionnez l'image de la tuile en fonction du type
    private Image selectTileImageBasedOnType(int tileType, ImageLoader imageLoader, int x, int y,
            int segmentSizeWidth,
            int segmentSizeHeight) {
        return imageLoader.getTileImage(tileType);
    }

    // Récupère l'image du minerai en fonction du type + state
    private Image getMineralImage(String ore, String state, ImageLoader imageLoader) {
        return imageLoader.getMineralImage(ore, state);
    }

    // Met a jour les keyFrame pour animer les images
    private void updateFrame() {
        spriteCounter++; // Incrémenter le compteur de frames
        if (spriteCounter % 8 == 0) { // Changer de frame toutes les 8 frames
            {
                spriteNum++;
                spriteNumVoleur++;
                if (spriteNum > 8) {
                    spriteNum = 1;
                }
                if (spriteNumVoleur > 6) {
                    spriteNumVoleur = 1;
                }
            }
        }

    }

    // TODO : -low priority- faire un design avec g2d pour le cercle de progression
    // & la quantité miné
    // Dessine un cercle de progression pour les minerais en cours de minage (et sur
    // la tuile courante)
    private void drawMiningCircle(Graphics g, int x, int y, int width, int height, int progress) {
        // Couleur du cercle de progression
        g.setColor(Color.BLUE);
        int startAngle = 90; // angle de départ

        int arcAngle = -(int) (360 * (progress / 100.0)); // met a jour le fill par rapport à la varible de
                                                          // progression
        g.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    // Dessine un +<quantité> pour les minerais quand le minerais rapporte des
    // ressources
    private void drawMinedQuantity(Graphics g, int x, int y, int width, int height, int quantity,
            Image icon) {
        // Taille de l'icône
        int iconWidth = 30;
        int iconHeight = 30;

        // Définir la couleur et la police pour le texte
        g.setFont(new Font("Arial", Font.BOLD, 14)); // Taille de police ajustée pour mieux s'adapter
        int textWidth = g.getFontMetrics().stringWidth("+" + quantity);
        int totalWidth = iconWidth + 5 + textWidth; // Calcul de la largeur totale du texte et de l'icône

        // Position Y ajustée pour centrer verticalement le texte par rapport à l'icône
        int textY = y + iconHeight / 2 + g.getFontMetrics().getAscent() / 2 - g.getFontMetrics().getDescent();
        int iconY = y;

        // Dessin d'un fond semi-transparent
        g.setColor(new Color(0, 0, 0, 123)); // Noir semi-transparent
        g.fillRect(x, iconY - iconHeight / 2, totalWidth + 10, iconHeight + 10); // Dessiner le fond plus grand que
                                                                                 // l'icône et le texte

        // Afficher l'image à la position donnée
        g.drawImage(icon, x + 5, iconY - iconHeight / 2, iconWidth, iconHeight, null);

        // Position X du texte ajustée pour l'espacement
        int textX = x + iconWidth + 10;

        // Dessiner le texte avec la quantité minée à côté de l'icône
        g.setColor(Color.WHITE); // Texte en blanc pour contraste
        g.drawString("+" + quantity, textX, textY);
    }

    public void drawUnlockProgress(Graphics g, int x, int y, int width, int remainingTime, int totalTime) {
        int height = 10;
        int filledWidth = (int) ((double) remainingTime / totalTime * width);
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);
        g.setColor(Color.GREEN);
        g.fillRect(x, y, filledWidth, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    public void drawNotificationMessage(Graphics g, String message) {
        // Configure le style du texte
        g.setColor(new Color(180, 50, 50)); // Une teinte rouge plus foncée pour l'alerte
        g.setFont(new Font("Arial", Font.BOLD, 15));

        // Calcule la position et la taille du texte
        int messageWidth = g.getFontMetrics().stringWidth(notificationMessage) + 20; // 20 pixels de padding
                                                                                     // horizontal
        int messageHeight = g.getFontMetrics().getHeight() + 10; // 10 pixels de padding vertical
        int messageX = getWidth() / 2 - messageWidth / 2;
        int messageY = getHeight() - 100; // 100px du bas de la tuile

        // Dessine un fond pour le message
        g.setColor(new Color(80, 70, 60)); // Couleur de fond terre pour le message
        g.fillRect(messageX, messageY - messageHeight + g.getFontMetrics().getAscent(), messageWidth,
                messageHeight);

        // Dessiner le texte de l'alerte
        g.setColor(Color.WHITE); // Blanc pour le texte
        g.drawString(notificationMessage, messageX + 10, messageY + 5);

        // Dessiner une bordure autour du fond
        g.setColor(new Color(140, 110, 100)); // Couleur pour la bordure
        g.drawRect(messageX, messageY - messageHeight + g.getFontMetrics().getAscent(), messageWidth,
                messageHeight);
    }

    // Ajuster dynamiquement la taille des images de minerai en fonction de l'état
    private Dimension getAdjustedSizeForState(String state, int segmentSizeWidth, int segmentSizeHeight) {
        switch (state) {
            case "FULL":
                return new Dimension(segmentSizeWidth, segmentSizeHeight);
            case "HALF":
                return new Dimension(segmentSizeWidth / 2, segmentSizeHeight / 2);
            case "EMPTY":
                return new Dimension(segmentSizeWidth / 4, segmentSizeHeight / 4);
            default:
                return new Dimension(segmentSizeWidth, segmentSizeHeight); // Taille par défaut si l'état n'est pas
                                                                           // reconnu
        }
    }

    private void drawMinerStorage(Graphics g, MinerRenderInfo minerInfo) {
        int dialogWidth = 300;
        int dialogHeight = 100;
        int x = 0; // Position x pour le coin inférieur gauche
        int y = getHeight() - dialogHeight - 10; // Position y pour le coin inférieur gauche

        // Fenêtre d'information générale
        g.setColor(new Color(80, 70, 60)); // Couleur de fond terre
        g.fillRect(x, y, dialogWidth, dialogHeight); // Dessine le fond de la fenêtre
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, dialogWidth, dialogHeight); // Dessine le bord de la fenêtre

        // Header avec "Mineur"
        g.setColor(new Color(140, 110, 100)); // Couleur pour le header
        g.fillRect(x, y, dialogWidth, 30); // Dessine le fond du header
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Mineur", x + 10, y + 20);

        // Corps: Description de l'item
        String itemName = minerInfo.inventory.get(minerInfo.inventory.size() - 1); // Dernier élément
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Meilleur item de l'inventaire:", x + 10, y + 45);

        // Image de l'outil
        Image pickAxeImg = imageLoader.getAxeIcon(itemName);
        g.setColor(new Color(200, 180, 160)); // Couleur claire pour le fond des images
        g.fillRect(x + 10, y + 55, 30, 30); // Espace pour l'image de l'outil
        g.drawImage(pickAxeImg, x + 10, y + 55, 30, 30, null);
        g.setColor(Color.WHITE);
        g.drawRect(x + 10, y + 55, 30, 30); // Bord de l'image

        // Nom de l'outil à côté de l'image
        g.drawString(itemName, x + 50, y + 75);

    }

    // EVENTS ANSWERS
    // Pour éviter de complexifier davantage l'application, quand le controller
    // capte un evenement il
    // transmet directement les informations à la vue, sans passer par un observer.

    // Affiche les détails du minerai lorsqu'il est clické
    public void showOreDetailsDialog(Point position, String type, String state, int qty, List<String> requiredTools,
            int timeRemaining) {
        this.showOreDetails = true;
        this.oreDetailsPosition = position;
        this.oreType = type;
        this.oreState = state;
        this.oreQuantity = qty;
        this.requiredTools = requiredTools;
        this.timeRemaining = timeRemaining;
        repaint();
    }

    public void showDoorDetailsDialog(String direction, List<String> requiredTools, Point position,
            int unlockingTime) {
        this.showDoorDetails = true;
        this.doorDetails = "Porte " + direction;
        this.requiredTools = requiredTools;
        this.doorDetailsPosition = position;
        this.unlockingTime = unlockingTime;
        repaint();
    }

    public void setShowOreDetails(boolean showOreDetails) {
        this.showOreDetails = showOreDetails;
    }

    public void setShowDoorDetails(boolean showDoorDetails) {
        this.showDoorDetails = showDoorDetails;
    }

    // afficher un message d'alerte
    public void setNotificationMessage(String message, long displayDurationMillis) {
        this.notificationMessage = message;
        this.messageDisplayTime = System.currentTimeMillis() + displayDurationMillis;
        repaint();
    }

    // OBSERVER
    @Override
    public void onTileChange(MapDTO mapData) {
        this.tilesType = mapData.getTilesType(); // Matrice de type de tuile à partir de MapData
        this.currentTile = mapData.getCurrentTile(); // Mettre à jour la tuile actuelle à partir de MapData

        // Initialiser une nouvelle map temporaire pour stocker les mineurs sur la tuile
        // courante
        Map<Integer, MinerRenderInfo> updatedMinerInfoMap = new HashMap<>();

        // Itérer seulement sur les mineurs qui sont censés être sur la tuile courante
        Map<Integer, Point> minersFromDTO = mapData.getMiners();
        minersFromDTO.forEach((minerId, position) -> {
            // Assumer que tous les mineurs fournis par mapData sont sur la tuile courante
            String state = "IDLE";

            // Mettre à jour ou ajouter les mineurs à la nouvelle map
            updatedMinerInfoMap.put(minerId, new MinerRenderInfo(position, state, null));
        });

        // Même procédé pour les voleurs
        this.minerRenderInfoMap = updatedMinerInfoMap;

        Map<Integer, VoleurRenderInfo> updatedVoleurInfoMap = new HashMap<>();
        Map<Integer, Point> voleursFromDTO = mapData.getVoleurs();
        voleursFromDTO.forEach((voleurId, position) -> {
            String state = "IDLE";

            updatedVoleurInfoMap.put(voleurId, new VoleurRenderInfo(position, state));
        });

        this.voleurRenderInfoMap = updatedVoleurInfoMap;

    }

    @Override
    public void onOresChange(OresDTO oresData) {
        // Mise à jour de la vue avec les nouvelles positions des minerais
        this.oresInfo = oresData.getMineralPositions(); // Récupère les nouveaux minerais notifié pour la tuile
                                                        // courante

        oreIdToPosition.clear();
        for (Map.Entry<Point, OreInfo> entry : oresInfo.entrySet()) {
            Point position = entry.getKey();
            OreInfo oreInfo = entry.getValue();
            oreIdToPosition.put(oreInfo.getId(), position);
        }
    }

    // Debut du minage
    @Override
    public void onMiningStart(int oreId, MineralDetailsDTO mineralDTO) {
        miningProgress.put(oreId, mineralDTO); // Commencer avec une progression de 0%

    }

    // Progression du minage
    @Override
    public void onMiningProgress(int oreId, MineralDetailsDTO mineralDTO, MinerDTO minerDTO) {
        // Mettre à jour directement la progression pour l'ID spécifique
        miningProgress.put(oreId, mineralDTO);

        // Mettre à jour l'état du minerai spécifique si présent dans oresInfo
        Point position = mineralDTO.getOreInfo().getPosition(); // Position du minerai mise à jour
        String newState = mineralDTO.getOreInfo().getState(); // Nouvel état du minerai
        if (oresInfo.containsKey(position) && oresInfo.get(position).getId() == oreId) {
            oresInfo.get(position).setState(newState);
        }

        // Mise à jour de l'état des mineurs en utilisant minerRenderInfoMap
        minerDTO.getState().forEach((id, state) -> {
            MinerRenderInfo minerInfo = minerRenderInfoMap.get(id);
            if (minerInfo != null) {
                minerInfo.state = state;
            }
        });

    }

    @Override
    public void onMiningProgress(int oreId, MineralDetailsDTO oresData, VoleurDTO voleurData) {
        // Mettre à jour directement la progression pour l'ID spécifique
        miningProgress.put(oreId, oresData);

        // Mettre à jour l'état du minerai spécifique si présent dans oresInfo
        Point position = oresData.getOreInfo().getPosition(); // Position du minerai mise à jour
        String newState = oresData.getOreInfo().getState(); // Nouvel état du minerai
        if (oresInfo.containsKey(position) && oresInfo.get(position).getId() == oreId) {
            oresInfo.get(position).setState(newState);
        }

        // Mise à jour de l'état des voleurs en utilisant voleurRenderInfoMap
        voleurData.getState().forEach((id, state) -> {
            VoleurRenderInfo voleurInfo = voleurRenderInfoMap.get(id);
            if (voleurInfo != null) {
                voleurInfo.state = state;
            }
        });

    }

    // Fin du minage
    @Override
    public void onMiningComplete(int oreId, MineralDetailsDTO mineralDTO, boolean isRegen, MinerDTO minerDTO) {
        // Supprimer le minerai de la liste de progression si le minage est complet et
        // non en phase de régénération
        if (!isRegen) {
            miningProgress.remove(oreId);
        } else {
            // Si c'est une régénération, mettre à jour l'état du minerai visuellement
            Point position = mineralDTO.getOreInfo().getPosition();
            String newState = mineralDTO.getOreInfo().getState();
            if (oresInfo.containsKey(position) && oresInfo.get(position).getId() == oreId) {
                oresInfo.get(position).setState(newState);
            }
        }

        // Mise à jour de l'état des mineurs en utilisant minerRenderInfoMap
        minerDTO.getState().forEach((id, state) -> {
            MinerRenderInfo minerInfo = minerRenderInfoMap.get(id);
            if (minerInfo != null) {
                minerInfo.state = state;
            }
        });

    }

    @Override
    public void onMiningComplete(int oreId, MineralDetailsDTO mineralDTO, boolean isRegen, VoleurDTO voleurDTO) {
        // Supprimer le minerai de la liste de progression si le minage est complet et
        // non en phase de régénération
        if (!isRegen) {
            miningProgress.remove(oreId);
        } else {
            // Si c'est une régénération, mettre à jour l'état du minerai visuellement
            Point position = mineralDTO.getOreInfo().getPosition();
            String newState = mineralDTO.getOreInfo().getState();
            if (oresInfo.containsKey(position) && oresInfo.get(position).getId() == oreId) {
                oresInfo.get(position).setState(newState);
            }
        }

        // Mise à jour de l'état des voleurs en utilisant voleurRenderInfoMap
        voleurDTO.getState().forEach((id, state) -> {
            VoleurRenderInfo voleurInfo = voleurRenderInfoMap.get(id);
            if (voleurInfo != null) {
                voleurInfo.state = state;
            }
        });
    }

    // Debloquer les portes
    @Override
    public void onUnlockStart(DoorDetailsDTO doorData, MinerDTO minerData) {
        // Vérifier que le mineur est sur la tuile courante
        if (minerData.getPositionMap().equals(currentTile)) {
            setMinerState(minerData.getId(), minerData.getState().get(minerData.getId()), null);
        }
        // Ajouter la porte à la progression si elle n'existe pas déjà
        doorsProgress.computeIfAbsent(doorData.getTilePosition(), k -> new HashMap<>())
                .putIfAbsent(doorData.getId(), doorData);
    }

    @Override
    public void onUnlockProgress(DoorDetailsDTO doorData, MinerDTO minerData) {
        // Mettre à jour la progression de la porte
        doorsProgress.getOrDefault(doorData.getTilePosition(), new HashMap<>())
                .replace(doorData.getId(), doorData);
        // Mettre à jour l'état du mineur si présent sur la tuile courante
        if (currentTile.equals(doorData.getTilePosition())) {
            setMinerState(minerData.getId(), minerData.getState().get(minerData.getId()), null);
        }
    }

    @Override
    public void onUnlockCompleted(DoorDetailsDTO doorData, MinerDTO minerData) {
        // Retirer la porte de la progression une fois débloquée
        Map<Integer, DoorDetailsDTO> doorsAtPosition = doorsProgress.get(doorData.getTilePosition());
        if (doorsAtPosition != null) {
            doorsAtPosition.remove(doorData.getId());
            if (doorsAtPosition.isEmpty()) {
                doorsProgress.remove(doorData.getTilePosition()); // Retirer l'entrée de la map si aucune porte
                                                                  // n'est en
                                                                  // progression
            }
        }
    }

    // Le mineur se deplace
    @Override
    public void onMinerMove(int id, MinerDTO minerData) {
        // Vérifier si le mineur doit être visible sur la tuile courante
        if (minerData.getPositionMap().equals(currentTile)) {
            // Récupérer l'information de rendu du mineur ou créer une nouvelle entrée si
            // nécessaire
            MinerRenderInfo minerInfo = minerRenderInfoMap.get(id);
            if (minerInfo == null) {
                // Si le mineur n'existe pas encore dans minerRenderInfoMap, l'ajouter
                minerInfo = new MinerRenderInfo(minerData.getPositionPixel(), "IDLE", null);
                minerRenderInfoMap.put(id, minerInfo);
            } else {
                // Mettre à jour la position et l'état du mineur
                minerInfo.position = minerData.getPositionPixel();
            }

            // Mettre à jour l'état du mineur directement dans minerRenderInfoMap
            String newState = minerData.getState().get(id);
            if (newState != null) { // S'assurer que l'état est présent
                minerInfo.state = newState;
            }
        } else {
            // Si le mineur ne doit pas être visible sur la tuile courante, le retirer de
            // minerRenderInfoMap
            minerRenderInfoMap.remove(id);
        }

    }

    // Le mineur se deplace
    @Override
    public void onVoleurMove(int id, VoleurDTO voleurData) {
        // Vérifier si le voleur doit être visible sur la tuile courante
        if (voleurData.getPositionMap().equals(currentTile)) {
            // Récupérer l'information de rendu du voleur ou créer une nouvelle entrée si
            // nécessaire
            VoleurRenderInfo voleurInfo = voleurRenderInfoMap.get(id);
            if (voleurInfo == null) {
                // Si le voleur n'existe pas encore dans voleurRenderInfoMap, l'ajouter
                voleurInfo = new VoleurRenderInfo(voleurData.getPositionPixel(), "IDLE");
                voleurRenderInfoMap.put(id, voleurInfo);
            } else {
                // Mettre à jour la position et l'état du mineur
                voleurInfo.position = voleurData.getPositionPixel();
            }

            // Mettre à jour l'état du voleur directement dans voleurRenderInfoMap
            String newState = voleurData.getState().get(id);
            if (newState != null) { // S'assurer que l'état est présent
                voleurInfo.state = newState;
            }
        } else {
            // Si le voleur ne doit pas être visible sur la tuile courante, le retirer de
            // voleurRenderInfoMap
            voleurRenderInfoMap.remove(id);
        }

    }

    // Le mineur a changé de tuile
    @Override
    public void onMinerTravel(int id, Point positionMap, Point positionPixel) {
        // Obtenir les informations de rendu du mineur
        MinerRenderInfo minerInfo = minerRenderInfoMap.get(id);

        // Vérifier si le mineur se déplace vers la tuile courante ou en sort
        if (positionMap.equals(currentTile)) {
            // Si le mineur entre sur la tuile courante ou se déplace à l'intérieur de
            // celle-ci
            if (minerInfo == null) {
                // Si le mineur n'existe pas encore, ajouter une nouvelle entrée
                minerInfo = new MinerRenderInfo(positionPixel, "IDLE", null);
                minerRenderInfoMap.put(id, minerInfo);
            } else {
                // Sinon, mettre à jour la position du mineur
                minerInfo.position = positionPixel;
            }
        } else {
            // Si le mineur quitte la tuile courante, le retirer de minerRenderInfoMap
            if (minerInfo != null) {
                minerRenderInfoMap.remove(id);
            }
        }

    }

    @Override
    public void onVoleurTravel(int id, Point mapPosition, Point positionPixel) {
        // Même fonctionnement que pour les mineurs
        VoleurRenderInfo voleurInfo = voleurRenderInfoMap.get(id);

        if (mapPosition.equals(currentTile)) {

            if (voleurInfo == null) {
                voleurInfo = new VoleurRenderInfo(positionPixel, "IDLE");
                voleurRenderInfoMap.put(id, voleurInfo);
            } else {
                voleurInfo.position = positionPixel;
            }
        } else {
            if (voleurInfo != null) {
                voleurRenderInfoMap.remove(id);
            }
        }

    }

    // Le minerai a été régénéré
    @Override
    public void onOreRegenerated(int oreId, MineralDetailsDTO mineralDetails) {
        // Mettre à jour la vue avec le minerai régénéré
        Point position = mineralDetails.getOreInfo().getPosition();
        String newState = mineralDetails.getOreInfo().getState();

        if (oresInfo.containsKey(position)) {
            OreInfo oreInfo = oresInfo.get(position);
            oreInfo.setState(newState); // Mise à jour de l'état
        }

    }

    // Une action d'un mineur s'est terminée
    @Override
    public void onFinishedAction(int entityId, String stateIDLE) {
        // Si la state est REMOVE, c'est un voleur capturé
        if (stateIDLE.equals("REMOVE_THIEF")) {
            voleurRenderInfoMap.remove(entityId);
        }
        // Sinon c'est un mineur ayant fini son action
        else {
            // Obtenir les informations de rendu du mineur
            MinerRenderInfo minerInfo = minerRenderInfoMap.get(entityId);

            // Vérifier si le mineur existe dans la collection
            if (minerInfo != null) {
                // Mettre à jour l'état du mineur avec l'état fourni
                setMinerState(entityId, stateIDLE, null);
            }
        }

    }

    @Override
    public void onCaptureStarted(MinerDTO minerData, VoleurDTO thiefData, int oreId) {
        // Vérifier que le mineur est sur la tuile courante
        if (minerData.getPositionMap().equals(currentTile)) {
            setMinerState(minerData.getId(), minerData.getState().get(minerData.getId()), null);
        }
        // Ajouter la porte à la progression si elle n'existe pas déjà
        catchingProgress.computeIfAbsent(thiefData.getPositionMap(), k -> new HashMap<>())
                .putIfAbsent(thiefData.getId(), thiefData);

        // Supprimer la progression de minage pour le minerai
        miningProgress.remove(oreId);

    }

    @Override
    public void onCaptureProgress(MinerDTO minerData, VoleurDTO thiefData) {
        // Mettre à jour la progression de la porte
        catchingProgress.getOrDefault(minerData.getPositionMap(), new HashMap<>())
                .replace(thiefData.getId(), thiefData);
        // Mettre à jour l'état du mineur si présent sur la tuile courante
        if (currentTile.equals(thiefData.getPositionMap())) {
            setMinerState(minerData.getId(), minerData.getState().get(minerData.getId()), null);
        }
    }

    @Override
    public void onCaptureCompleted(MinerDTO minerData, VoleurDTO thiefData, boolean isRegen, int oreId, int drop) {
        // Retirer la porte de la progression une fois débloquée
        Map<Integer, VoleurDTO> thiefAtPosition = catchingProgress.get(thiefData.getPositionMap());
        if (thiefAtPosition != null) {
            thiefAtPosition.remove(thiefData.getId());
            if (thiefAtPosition.isEmpty()) {
                doorsProgress.remove(thiefData.getPositionMap()); // Retirer l'entrée de la map si aucune porte n'est en
                                                                  // progression
            }
        }

        // Retirer la progression de minage du minerai
        if (!isRegen) {
            miningProgress.remove(oreId);
        }

        if (drop > 0) {
            this.moneyLooted = drop;
            this.lastDrawMoneyTime = System.currentTimeMillis();
        }
    }
}
