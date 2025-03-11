package controller;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.*;
import view.MapPanel;
import view.Market.MarketPanel;
import view.MiniMapPanel;
import model.BlockedDoor;
import model.Constants;
import model.GameMap;
import model.characters.CatchThiefAction;
import model.characters.MineOpeningAction;
import model.characters.Miner;
import model.characters.MiningAction;
import model.characters.MoveMinerAction;
import model.characters.ToNextTileAction;
import model.characters.Voleur;
import model.Items.OreInstance;

import javax.swing.*;

public class EventsController extends MouseAdapter {
    // VARIABLES
    private GameMap gameMap;
    private MapPanel mapPanel;
    private MiniMapPanel miniMapPanel;
    private MarketPanel marketPanel;
    private GameController gameController;
    private TrActionManager actionManager;

    // Attributs pour gérer la taille des segments dynamiquement
    private int segmentWidth;
    private int segmentHeight;
    // Attributs pour gérer la sélection d'un mineur
    private boolean isSelected;
    private Miner selectedMiner;

    // CONSTRUCTOR
    public EventsController(GameMap gameMap, MapPanel mapPanel, GameController gameController,
            MiniMapPanel miniMapPanel, MarketPanel marketPanel) {
        this.gameMap = gameMap;
        this.mapPanel = mapPanel;
        this.gameController = gameController;
        this.actionManager = TrActionManager.getInstance();
        this.miniMapPanel = miniMapPanel;
        this.marketPanel = marketPanel;
        // Attacher les listener d'evenements
        this.mapPanel.addMouseListener(this);
        this.mapPanel.addMouseMotionListener(this);

        isSelected = false; // Aucun mineur sélectionné au départ

        // Ajuster la taille des segments pour la première fois
        adjustForPanelSize();

        // Evenement de clic sur les boutons du marché
        marketPanel.getMarket().getBuyMiner().addActionListener(e -> {
            // Générer nouveau mineur et le communiqué aux vues
            DecreaseMoney thread = new DecreaseMoney();
            int amount = marketPanel.getMarket().getItemPage().getMinerPrice();
            if (amount <= Storage.getInstance().getMoney()) {
                thread.initiate(amount);
                thread.start();
                gameController.generateNewMiner(new Point(Constants.MAP_WIDTH / 2, Constants.MAP_HEIGHT / 2), 7, 7,
                        true);
                marketPanel.getMarket().resetMiner();
                marketPanel.getMarket().changePanelFromState(0);
                marketPanel.getMarket().repaint();
                marketPanel.getMarket().getItemPage().loadUpgradeDone();

            } else {
                marketPanel.getMarket().alert();
            }

        });

        int k = 0;
        /*
         * for (JButton but : marketPanel.getMarket().getItemsButtons()) {
         * final int x = k;
         * but.setText("but n°" + k);
         * but.addActionListener(e -> {
         * marketPanel.getMarket().getInfo();
         * marketPanel.getMarket().getItemPage().getButtonClicked(marketPanel.getMarket(
         * ).getItems().get(x),
         * marketPanel.getMarket().getMiner());
         * marketPanel.getMarket().getItemPage().changeItem(new Item(x));
         * System.out.println("Selection Bouton " + x);
         * });
         * k++;
         * }
         */
    }

    // METHODS
    // Logique pour gérer le survol de la souris
    @Override
    public void mouseMoved(MouseEvent e) {
        onDoorHover(e.getPoint());
        onMineralHover(e.getPoint());
        onMinerHover(e.getPoint());
        onVoleurHover(e.getPoint());
        // Ajouter les event de survol des perso, menu etc... ici
    }

    // Logique pour gérer les clics de souris
    @Override
    public void mouseClicked(MouseEvent e) {
        // n'importe quelle click fait disparaitre les fenetre d'informations
        mapPanel.setShowOreDetails(false);
        mapPanel.setShowDoorDetails(false);

        Point clickPoint = e.getPoint();

        // Si le clic n'est pas sur un élément interactif, déplacer le mineur
        // sélectionné
        if (!handleInteractiveElements(clickPoint) && isSelected) {
            handleMoveSelectedMiner(e.getPoint());
        }
    }

    // Méthode pour determiner si le clic est sur un élément interactif
    private boolean handleInteractiveElements(Point clickPoint) {
        if (onMinerClicked(clickPoint))
            return true;
        if (onVoleurClicked(clickPoint))
            return true;
        if (onMineralClicked(clickPoint))
            return true;
        return onDoorClicked(clickPoint);
    }

    // Méthode pour gérer le déplacement du mineur sélectionné
    private void handleMoveSelectedMiner(Point destination) {
        selectedMiner.setState("WALKING");
        // Création d'un objet d'action de déplacement
        MoveMinerAction moveAction = new MoveMinerAction(selectedMiner, destination);
        actionManager.addMoveAction(selectedMiner, moveAction);
        deselectMiner();
    }

    // Vérifiez si la souris est au-dessus d'une ouverture
    private boolean mouseIsOverAnOpening(int mouseX, int mouseY, Map<String, Rectangle> openings) {
        // Parcourir les hitbox des ouvertures pour vérifier si la souris est au-dessus
        for (Rectangle opening : openings.values()) {
            if (opening.contains(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    // Logique pour gérer le survol de la souris sur les ouvertures
    private void onDoorHover(Point hoverPoint) {
        // Récupérer les hitbox de la tuile courante
        Point currentPoint = gameMap.getCurrentTile();
        Tile currentTile = gameMap.getTile(currentPoint.x, currentPoint.y);
        Map<String, Rectangle> openings = currentTile.getOpeningsRectangles(segmentWidth, segmentHeight);

        // Vérifiez si la souris est au-dessus d'une ouverture TODO : centraliser la
        // logique ici
        if (mouseIsOverAnOpening(hoverPoint.x, hoverPoint.y, openings)) {
            String direction = getHoverDirection(hoverPoint.x, hoverPoint.y, openings);
            gameController.onTileHoverOpening(currentPoint, true, direction);
        } else {
            gameController.onTileHoverOpening(currentPoint, false, null);
        }
    }

    // Déterminez la direction de l'ouverture survolée
    private String getHoverDirection(int mouseX, int mouseY, Map<String, Rectangle> openings) {
        for (Map.Entry<String, Rectangle> entry : openings.entrySet()) {
            if (entry.getValue().contains(mouseX, mouseY)) {
                return entry.getKey(); // Retourne la direction de l'ouverture
            }
        }
        return null; // Aucune ouverture survolée
    }

    // Logique pour changer de tuile lors d'un clic
    private boolean onDoorClicked(Point clickPoint) {
        // Vérifiez si le clic est sur une ouverture
        Point currentPoint = gameMap.getCurrentTile();
        Tile currentTile = gameMap.getTile(currentPoint.x, currentPoint.y);
        Map<String, Rectangle> openings = currentTile.getOpeningsRectangles(segmentWidth, segmentHeight);

        for (Map.Entry<String, Rectangle> entry : openings.entrySet()) {
            if (entry.getValue().contains(clickPoint.x, clickPoint.y)) {

                // FIN DE NIVEAU
                if (currentTile.isExit() && !currentTile.isBlocked(entry.getKey())
                        && currentTile.getExitDirection().equals(entry.getKey())) {
                    // TODO : Ajouter la logique pour changer de niveau
                    gameController.endGame();
                }
                // Si le clic est sur une ouverture, déterminez la direction et changez de tuile
                // Si un mineur est sélectionné, déplacer le mineur de tuile
                else if (isSelected) {
                    // Si la poprte est bloqué par un obstacle, ne pas laisser le mineur passer
                    if (currentTile.isBlocked(entry.getKey())) {
                        // Comparé la list de requirement du mur avec les outils du mineur
                        BlockedDoor blockedDoor = currentTile.getDoorRequirements(entry.getKey().toUpperCase());
                        boolean hasRequiredTool = selectedMiner.containsAny(blockedDoor.getRequirements());

                        if (!hasRequiredTool) {
                            // recupere seulement le premier outil nécéssaire
                            List<String> requiredTools = blockedDoor.getRequirements();
                            String minReq = requiredTools.isEmpty() ? "Aucun" : requiredTools.get(0);

                            String errorMessage = "La porte est bloquée par un obstacle. MIN REQUIS : "
                                    + minReq.toString() + ".";
                            mapPanel.setNotificationMessage(errorMessage, 2500); // Affiche le message pendant 5s
                                                                                 // secondes
                        } else {
                            // creer une action pour casser la porte
                            selectedMiner.setState("TO_BLOCKED_DOOR");
                            // Création d'un objet d'action de minage
                            MineOpeningAction mineOpeningAction = new MineOpeningAction(selectedMiner, clickPoint,
                                    blockedDoor);
                            actionManager.addUnlockingAction(selectedMiner, mineOpeningAction);
                            mineOpeningAction.attachObserver(mapPanel); // notifier la nouvelle position du mineur sur
                                                                        // la
                                                                        // map
                        }
                    }
                    // Porte non bloqué on change le mineur de tuile
                    else {
                        selectedMiner.setState("TO_NEXT_TILE");
                        Point newPosition = gameController.calculateNewTileBasedOnDirection(entry.getKey());
                        if (gameController.isTileChangeValid(newPosition)) {
                            // Création d'un objet d'action de changement de tuile
                            ToNextTileAction toNextTileAction = new ToNextTileAction(selectedMiner, clickPoint,
                                    newPosition, entry.getKey());
                            actionManager.addNextTileAction(selectedMiner, toNextTileAction);
                            toNextTileAction.attachObserver(mapPanel); // notifier la nouvelle position du mineur sur la
                                                                       // map
                            toNextTileAction.attachObserver(miniMapPanel); // notifier la nouvelle position du mineur
                                                                           // sur la
                                                                           // minimap
                        } else {
                            selectedMiner.setState("IDLE");
                        }
                    }
                    deselectMiner();
                } else {
                    // Si aucun mineur n'est selectionné et que la porte est ouverte, changer de
                    // tuile
                    if (!currentTile.isBlocked(entry.getKey())) {
                        gameController.onTileClicked(currentPoint, entry.getKey());
                        return true; // Sortir de la boucle après avoir traité le clic
                    }
                    // Sinon indiqué à l'utilisateur que la porte est bloquée
                    else {
                        // créer un fenetre d'interface avec la direction et les outils
                        // nécéssaires
                        mapPanel.showDoorDetailsDialog(entry.getKey(),
                                currentTile.getDoorRequirements(entry.getKey()).getRequirements(),
                                clickPoint,
                                currentTile.getDoorMaxDuration(entry.getKey()));
                    }
                }
                return true;
            }
        }
        return false; // Aucune ouverture cliquée
    }

    // Logique pour gérer le survol de la souris sur les minerais
    private void onMineralHover(Point hoverPoint) {
        // Récupérer les hitbox de la tuile courante
        Point currentPoint = gameMap.getCurrentTile();
        Tile currentTile = gameMap.getTile(currentPoint.x, currentPoint.y);
        Map<OreInstance, Rectangle> mineralHitbox = currentTile.getMineralRectangles(segmentWidth, segmentHeight);

        // Parcourir les hitbox des minerais pour vérifier si la souris est au-dessus
        for (Map.Entry<OreInstance, Rectangle> entry : mineralHitbox.entrySet()) {
            if (entry.getValue().contains(hoverPoint)) {
                // Minerai survolé, enregistrer les informations pour la notification
                OreInstance hoveredOreInfo = entry.getKey();
                gameController.onTileHoverOre(currentPoint, true, hoveredOreInfo.getOre().getId(),
                        hoveredOreInfo.getOre().getType());
                break; // Sortir de la boucle après avoir trouvé un minerai survolé
            } else {
                // Aucun minerai survolé, informez le contrôleur
                gameController.onTileHoverOre(currentPoint, false, -1, null);
            }
        }
    }

    // Logique pour gérer le clic sur les minerais
    private boolean onMineralClicked(Point clickPoint) {
        // Récupérer les hitbox de la tuile courante
        Point currentPoint = gameMap.getCurrentTile();
        Tile currentTile = gameMap.getTile(currentPoint.x, currentPoint.y);
        Map<OreInstance, Rectangle> mineralHitbox = currentTile.getMineralRectangles(segmentWidth, segmentHeight);

        // Parcourir les hitbox des minerais pour vérifier si le clic est sur un minerai
        for (Map.Entry<OreInstance, Rectangle> entry : mineralHitbox.entrySet()) {
            OreInstance currentOreInstance = entry.getKey();

            if (entry.getValue().contains(clickPoint)) {
                // Si un mineur est sélectionné & le minerai est dispo, déplacer le mineur vers
                // le minerai
                if (isSelected) {
                    // Il faut vérifier qu'un voleur ne soit pas en cours de minage
                    if (currentOreInstance.isTargeted() == "VOLEUR") {
                        String errorMessage = "Le minerai est actuellement ciblé par un voleur. Capturez le !";
                        mapPanel.setNotificationMessage(errorMessage, 2500); // Affiche le message pendant 5 secondes
                        deselectMiner();
                        return true;
                    }

                    // Vérifier que le mineur dispose de l'outils nécéssaire au minage
                    boolean hasRequiredTool = selectedMiner.containsAny(currentOreInstance.getOre().getRequirement());

                    // Le mineur dispose de l'outils : miner
                    if (hasRequiredTool) {
                        selectedMiner.setState("TO_MINING");
                        // Création d'un objet d'action de minage
                        MiningAction miningAction = new MiningAction(selectedMiner, clickPoint, currentOreInstance);
                        currentOreInstance.setTargeted("MINEUR"); // Empeche les voleur de target le minerai
                        // Abonnez le contrôleur aux événements de clic et de survol de la souris
                        miningAction.attachObserver(mapPanel); // Abonnez le thread de minage aux changements dans la
                                                               // gestion des minerais
                        actionManager.addMiningAction(selectedMiner, miningAction);
                    }
                    // Le mineur ne dispose pas de l'outils nécéssaire : informer l'utilisateur
                    else {
                        // recupere seulement le premier outil nécéssaire
                        List<String> requiredTools = currentOreInstance.getOre().getRequirement();
                        String minReq = requiredTools.isEmpty() ? "Aucun" : requiredTools.get(0);

                        String errorMessage = "Le mineur ne dispose pas de l'outil nécessaire pour miner ce minerai. " +
                                "MIN. REQUIS : " + minReq.toString();
                        mapPanel.setNotificationMessage(errorMessage, 2500); // Affiche le message pendant 5 secondes
                        // deselectionner le mineur visuellement
                        mapPanel.setMinerState(selectedMiner.getId(), "IDLE", null);
                    }
                    deselectMiner();
                } else {
                    // TODO : choisir des information pertinentes à afficher dans la fenetre
                    // d'informations

                    // Afficher les details du minerais dans une fenetre d'informations
                    Point positionDialog = calculateDialogPosition(clickPoint, mapPanel.getSize(),
                            new Dimension(200, 200)); // calculer la position de la fenêtre de dialogue
                    mapPanel.showOreDetailsDialog(
                            positionDialog,
                            currentOreInstance.getOre().getType(),
                            currentOreInstance.getOre().getState(),
                            currentOreInstance.getOre().getQuantity(),
                            currentOreInstance.getOre().getRequirement(),
                            currentOreInstance.getOre().getHarvestingTime());
                    return true; // Sortir de la boucle après avoir traité le clic
                }
            }
        }
        return false; // Aucun minerai cliqué
    }

    // Logique pour gérer le survol de la souris sur le mineur
    private void onMinerHover(Point hoverPoint) {
        for (Map.Entry<Integer, Miner> entry : gameMap.getMineurs().entrySet()) {
            Miner mineur = entry.getValue();
            // On ne veux pas considérer les hitbox des mineurs sur des tuiles differentes
            if (mineur.getPositionMap().equals(gameMap.getCurrentTile())) {
                mineur.updateHitbox(segmentWidth, segmentHeight); // Mettre à jour la hitbox du mineur
                if (mineur.getHitbox().contains(hoverPoint)) {
                    // Le mineur est survolé
                    gameController.onMinerHover(entry.getKey(), true);
                    return;
                }
            }
        }
        // Aucun mineur survolé
        gameController.onMinerHover(-1, false);
    }

    // Logique pour gérer le clic sur le mineur
    private boolean onMinerClicked(Point clickPoint) {
        for (Map.Entry<Integer, Miner> entry : gameMap.getMineurs().entrySet()) {
            Miner mineur = entry.getValue();
            // On ne veux pas considérer les hitbox des mineurs sur des tuiles différentes
            if (mineur.getPositionMap().equals(gameMap.getCurrentTile())) {
                mineur.updateHitbox(segmentWidth, segmentHeight); // Mettre à jour la hitbox du mineur
                if (mineur.getHitbox().contains(clickPoint)) {
                    if (isSelected && selectedMiner != null) {
                        // Désélectionne le mineur précédemment sélectionné si un nouveau mineur est
                        // cliqué
                        if (selectedMiner.getId() != mineur.getId()) {
                            deselectMiner();
                        }
                        isSelected = false; // Prépare pour une potentielle nouvelle sélection
                        selectedMiner = null;
                    }

                    // Vérifie ensuite si le mineur cliqué doit être sélectionné
                    if (!isSelected || selectedMiner == null) {
                        isSelected = true;
                        selectedMiner = mineur;
                        selectedMiner.setState("SELECTED");
                        System.out.println("Mineur cliqué: " + mineur.getMinerStorageList());
                        mapPanel.setMinerState(mineur.getId(), "SELECTED", mineur.getMinerStorageList());
                    }
                    return true;
                }
            }
        }
        return false; // Aucun mineur cliqué
    }

    public void onVoleurHover(Point hoverPoint) {
        for (Map.Entry<Integer, Voleur> entry : gameMap.getVoleurs().entrySet()) {
            Voleur voleur = entry.getValue();
            // On ne veux pas considérer les hitbox des voleurs sur des tuiles differentes
            if (voleur.getPositionMap().equals(gameMap.getCurrentTile())) {
                voleur.updateHitbox(segmentWidth, segmentHeight); // Mettre à jour la hitbox du voleur
                if (voleur.getHitbox().contains(hoverPoint)) {
                    // Le voleur est survolé
                    gameController.onVoleurHover(entry.getKey(), true);
                    return;
                }
            }
        }
    }

    public boolean onVoleurClicked(Point clickPoint) {
        for (Map.Entry<Integer, Voleur> entry : gameMap.getVoleurs().entrySet()) {
            Voleur voleur = entry.getValue();
            // On ne veux pas considérer les hitbox des voleurs sur des tuiles differentes
            if (voleur.getPositionMap().equals(gameMap.getCurrentTile())) {
                voleur.updateHitbox(segmentWidth, segmentHeight); // Mettre à jour la hitbox du voleur
                if (voleur.getHitbox().contains(clickPoint)) {
                    if (isSelected) {
                        selectedMiner.setState("TO_ENEMY");
                        // Création d'un objet d'action de capture
                        CatchThiefAction catchThiefAction = new CatchThiefAction(selectedMiner, clickPoint, voleur,
                                gameMap);
                        actionManager.addCatchThiefAction(selectedMiner, catchThiefAction);

                        catchThiefAction.attachObserver(mapPanel); // notifier la nouvelle position du mineur sur la map

                        deselectMiner();
                        return true;
                    } else {
                        System.out.println("Informations...");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Ajuster la taille des segments en fonction de la taille du panneau
    private void adjustForPanelSize() {
        Dimension panelSize = mapPanel.getSize();

        segmentWidth = panelSize.width / Constants.LAYOUT_NUMBER_OFCUT;
        segmentHeight = panelSize.height / Constants.LAYOUT_NUMBER_OFCUT;
    }

    // Deselectionner le mineur
    private void deselectMiner() {
        if (selectedMiner != null) {
            mapPanel.setMinerState(selectedMiner.getId(), "IDLE", null);
            isSelected = false;
            selectedMiner = null;
        }
    }

    // Méthode pour calculer la position de la fenêtre de dialogue
    private Point calculateDialogPosition(Point clickPoint, Dimension panelSize, Dimension dialogSize) {
        int x = clickPoint.x - dialogSize.width / 2;
        int y = clickPoint.y - dialogSize.height / 2;

        // Ajustement pour s'assurer que la fenêtre reste à l'intérieur du panel
        x = Math.max(x, 0);
        x = Math.min(x, panelSize.width - dialogSize.width);
        y = Math.max(y, 0);
        y = Math.min(y, panelSize.height - dialogSize.height);

        return new Point(x, y);
    }
}
