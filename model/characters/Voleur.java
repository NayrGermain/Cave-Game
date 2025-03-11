package model.characters;

import model.Constants;
import model.Tile;
import model.DTO.VoleurDTO;
import model.Items.OreInstance;
import model.observers.VoleurPositionObserver;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Voleur extends Entity {

    // VARIABLES
    private Rectangle hitbox; // Hitbox pour la détection de collision
    private EtatVoleur state; // Etat actuel du mineur
    private List<OreInstance> oresToMine; // Liste des minerais à miner dans la tuile

    private List<Tile> ancienneTuile; // Liste des tuiles déjà visitées

    private OreInstance targetMineral; // Minerai actuellement ciblé par le mineur
    private int maxCaptureTime;
    private int captureTime;

    private enum EtatVoleur {
        IDLE,
        WALKING,
        MINING,
        TO_MINING,
        WAITING, // Dans le cas ou le voleur est en attente après avoir miné
        TO_NEXT_TILE,
        TRAVEL,
        READY_TO_MINE,
        CAPTURING,
        TO_REMOVE,
    }

    private int segmentWidth;
    private int segmentHeight;

    private List<VoleurPositionObserver> observers = new ArrayList<>();

    private List<String> voleurStorage; // Inventaire du voleur (dans le cas ou lorsqu'il "meurt" il relâche son
                                        // inventaire

    // CONSTRUCTOR
    public Voleur(Point positionMap, int moveSpeed, double miningSpeed, List<OreInstance> oresToMine,
            int maxCaptureTime) {
        super(positionMap, moveSpeed, miningSpeed);
        this.state = EtatVoleur.IDLE;
        this.hitbox = new Rectangle();
        this.oresToMine = oresToMine;
        this.positionPixel = new Point(Constants.FRAME_WIDTH / 2 - 50, Constants.FRAME_HEIGHT / 2 - 50);
        this.maxCaptureTime = maxCaptureTime;
        this.captureTime = maxCaptureTime;

        this.voleurStorage = new ArrayList<>();
        this.voleurStorage.add(Constants.WOODEN_PICKAXE); // Ajoutez une pioche en bois par défaut

        this.ancienneTuile = new ArrayList<>();
    }

    // METHODS
    // Méthode pour mettre à jour la hitbox du voleur
    public void updateHitbox(int segmentWidth, int segmentHeight) {
        // Dimensions de la hitbox du voleur
        int hitboxWidth = 50;
        int hitboxHeight = 50;

        // Calcul de la position de départ de la hitbox
        // Centre la hitbox sur la position du voleur en ajustant par la moitié de la
        // largeur/hauteur de la hitbox
        int hitboxX = this.positionPixel.x - (hitboxWidth / 2);
        int hitboxY = this.positionPixel.y - (hitboxHeight / 2);

        // Mettre à jour la hitbox avec ces nouvelles valeurs
        this.hitbox.setBounds(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    @Override
    // Méthode pour déplacer le voleur vers une position donnée
    public void deplacerVers(Point destination) {

        Point destinationPixel = new Point((destination.x * Constants.FRAME_WIDTH) / 9 + 20,
                (destination.y * Constants.FRAME_HEIGHT) / 9 + 20);

        // Calculez le vecteur de déplacement
        double dx = destinationPixel.x - positionPixel.x;
        double dy = destinationPixel.y - positionPixel.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double vx = (dx / distance) * moveSpeed;
        double vy = (dy / distance) * moveSpeed;

        // Appliquez le déplacement
        positionPixel.x += vx;
        positionPixel.y += vy;

        // Mise à jour de la position de la tuile si nécessaire, en fonction de la
        // grille
        updateHitbox(segmentWidth, segmentHeight);

        if (positionPixel.equals(destinationPixel)
                || destinationPixel.distance(positionPixel) <= Constants.MOVE_THRESHOLD) {

            // Déterminer la prochaine action basé sur l'action qui a initié le déplacement
            if (state.equals(EtatVoleur.TO_MINING)) {
                state = EtatVoleur.READY_TO_MINE; // Miner
            } else if (state.equals(EtatVoleur.TO_NEXT_TILE)) {
                state = EtatVoleur.TRAVEL; // changer de tuile
            }
        }
        notifyObservers();
    }

    // mehode qui calcule et retourne le mineral le plus proche du voleur
    public OreInstance getClosestMineral() {
        OreInstance closestMineral = null;
        double closestDistance = Double.MAX_VALUE;

        Point voleurPosition = getPositionPixel();

        // Parcourir tous les minerais sur la tuile
        for (OreInstance ore : oresToMine) {
            if (containsAny(ore.getOre().getRequirement())) {
                // Vérifier si le minerai est disponible
                if (!ore.getOre().getStatus().equals("AVAILABLE") || ore.isTargeted() == "MINEUR")
                    continue;

                // Calculer la position du minerai
                Point mineralPosition = new Point((ore.getPosition().x * Constants.FRAME_WIDTH) / 9,
                        (ore.getPosition().y * Constants.FRAME_HEIGHT) / 9);

                // Calculer la distance entre le voleur et ce minerai
                double distance = Math.sqrt(Math.pow(voleurPosition.x - mineralPosition.x, 2)
                        + Math.pow(voleurPosition.y - mineralPosition.y, 2));

                // Si cette distance est la plus petite trouvée jusqu'à présent, mise à jour du
                // minerai le plus proche
                if (distance < closestDistance) {
                    closestMineral = ore;
                    closestDistance = distance;
                }
            }
        }

        if (closestMineral != null) {
            closestMineral.setTargeted("VOLEUR");
        }
        targetMineral = closestMineral;
        return closestMineral;
    }

    // Méthode pour vérifier si l'inventaire contient au moins un outil requis
    public boolean containsAny(List<String> requiredTools) {
        for (String requiredTool : requiredTools) {
            if (this.voleurStorage.contains(requiredTool)) {
                return true; // Retourne vrai dès qu'un outil requis est trouvé
            }
        }
        return false; // Aucun outil requis n'est trouvé
    }

    // methode qui ajoute une tuile à la liste des tuiles déjà visitées
    public boolean addAncienneTuile(Tile tile) {
        if (ancienneTuile.contains(tile)) {
            return false;
        } else {
            if (ancienneTuile.size() == 2) {
                ancienneTuile.remove(0);
            }
            ancienneTuile.add(tile);
            return true;
        }
    }

    // Méthode pour capturer un mineur
    public int inCapture() {
        if (captureTime > 0) {
            captureTime--;
        } else {
            setState("TO_REMOVE");
            return Constants.GOLD_QUANTITY_THIEF;
        }
        return 0;
    }

    // OBSERVERS
    public void addObserver(VoleurPositionObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        VoleurDTO voleurData = buildVoleurDTO();
        for (VoleurPositionObserver observer : observers) {
            observer.onVoleurMove(id, voleurData); // Notifiez avec la position en pixels
        }
    }

    private VoleurDTO buildVoleurDTO() {
        Map<Integer, String> states = new HashMap<>();
        states.put(id, state.toString());
        return new VoleurDTO(id, positionMap, positionPixel, states, maxCaptureTime, captureTime);
    }

    public int getId() {
        return id;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public Point getPositionMap() {
        return positionMap;
    }

    public int getMaxCaptureTime() {
        return maxCaptureTime;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public void setPositionMap(Point positionMap, String direction) {
        this.positionMap = positionMap;

        // Ajuster la position pixel du mineur sur la tuile suivante
        switch (direction.toUpperCase()) {
            case "NORTH":
                // Positionner le mineur au sud de la tuile
                setPositionPixel(new Point((Constants.FRAME_WIDTH / 2), Constants.FRAME_HEIGHT - Constants.MARGIN));
                break;
            case "SOUTH":
                // Positionner le mineur au nord de la tuile
                setPositionPixel(new Point((Constants.FRAME_WIDTH / 2), Constants.MARGIN));
                break;
            case "EAST":
                // Positionner le mineur à l'ouest de la tuile
                setPositionPixel(new Point(Constants.MARGIN, Constants.FRAME_HEIGHT / 2));
                break;
            case "WEST":
                // Positionner le mineur à l'est de la tuile
                setPositionPixel(new Point(Constants.FRAME_WIDTH - Constants.MARGIN, Constants.FRAME_HEIGHT / 2));
                break;
        }
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Point getPositionPixel() {
        return positionPixel;
    }

    public Point setPositionPixel(Point positionPixel) {
        return this.positionPixel = positionPixel;
    }

    public String getState() {
        return state.toString();
    }

    public void setState(String state) {
        this.state = EtatVoleur.valueOf(state);
    }

    public List<OreInstance> getOresToMine() {
        return oresToMine;
    }

    public void setOresToMine(List<OreInstance> oresToMine) {
        this.oresToMine = oresToMine;
    }

    public OreInstance getTargetMineral() {
        return targetMineral;
    }

    // methode qui donne le minerai le plus proche du voleur
    public void setClosestMineral() {
        targetMineral = getClosestMineral();
    }

    public void setTargetMineral(OreInstance targetMineral) {
        this.targetMineral = targetMineral;
    }

}
