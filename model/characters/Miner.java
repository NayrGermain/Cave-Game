package model.characters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Rectangle;

import model.Constants;
import model.DTO.MinerDTO;
import model.observers.MinerPositionObserver;

public class Miner extends Entity {
    // VARIABLES
    private Rectangle hitbox; // Hitbox pour la détection de collision
    private EtatMineur state; // Etat actuel du mineur
    private int segmentWidth;
    private int segmentHeight;
    private List<String> minerStorage;

    private enum EtatMineur {
        SELECTED,
        WALKING,
        MINING,
        TO_MINING,
        IDLE,
        TO_ENEMY,
        CAPTURING,
        TO_NEXT_TILE,
        TO_BLOCKED_DOOR,
        UNLOCKING_DOOR,
        TRAVEL, // change la tuile
        REMOVE_THIEF // Etat transition pour supprimer l'entité après nettoyage de ses ref
    }

    private List<MinerPositionObserver> observers = new ArrayList<>();

    // CONSTRUCTOR
    public Miner(Point positionMap, int moveSpeed, double miningSpeed) {
        super(positionMap, moveSpeed, miningSpeed);
        this.state = EtatMineur.IDLE;
        this.hitbox = new Rectangle();
        this.positionPixel = new Point(Constants.FRAME_WIDTH / 2,
                Constants.FRAME_HEIGHT / 2);

        this.minerStorage = new ArrayList<>();
        this.minerStorage.add(Constants.WOODEN_PICKAXE); // Ajoutez une pioche en bois par défaut
        //this.minerStorage.add(Constants.GOLD_PICKAXE); // Ajoutez une pioche en bois par défaut
    }

    // METHODS
    // Méthode pour mettre à jour la hitbox du mineur
    public void updateHitbox(int segmentWidth, int segmentHeight) {
        // Dimensions de la hitbox du mineur
        int hitboxWidth = 50;
        int hitboxHeight = 50;

        // Calcul de la position de départ de la hitbox
        // Centre la hitbox sur la position du mineur en ajustant par la moitié de la
        // largeur/hauteur de la hitbox
        int hitboxX = this.positionPixel.x - (hitboxWidth / 2);
        int hitboxY = this.positionPixel.y - (hitboxHeight / 2);

        // Mettre à jour la hitbox avec ces nouvelles valeurs
        this.hitbox.setBounds(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    // Méthode pour déplacer le mineur vers une position donnée
    public void deplacerVers(Point destinationPixel) {
        // Calculez le vecteur de déplacement
        double dx = destinationPixel.x - positionPixel.x;
        double dy = destinationPixel.y - positionPixel.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Calculez les composantes x et y du vecteur de déplacement
        double vx = (dx / distance) * moveSpeed;
        double vy = (dy / distance) * moveSpeed;

        // Appliquez le déplacement
        positionPixel.x += vx;
        positionPixel.y += vy;

        // Mise à jour de la position de la tuile si nécessaire, en fonction de la
        // grille
        updateHitbox(segmentWidth, segmentHeight);
        notifyObservers();
    }

    // Méthode pour vérifier si l'inventaire contient au moins un outil requis
    public boolean containsAny(List<String> requiredTools) {
        for (String requiredTool : requiredTools) {
            if (this.minerStorage.contains(requiredTool)) {
                return true; // Retourne vrai dès qu'un outil requis est trouvé
            }
        }
        return false; // Aucun outil requis n'est trouvé
    }

    // OBSERVERS
    public void addObserver(MinerPositionObserver observer) {
        observers.add(observer);
    }

    protected void notifyObservers() {
        MinerDTO minerData = buildMinerDTO();
        for (MinerPositionObserver observer : observers) {
            observer.onMinerMove(id, minerData); // Notifiez avec la position en pixels
        }
    }

    private MinerDTO buildMinerDTO() {
        Map<Integer, String> states = new HashMap<>();
        states.put(id, state.toString());
        return new MinerDTO(id, positionMap, positionPixel, states);
    }

    public int getId() {
        return id;
    }

    public Point getPositionMap() {
        return positionMap;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public String getState() {
        return state.toString();
    }

    public Point getPositionPixel() {
        return positionPixel;
    }

    public void setState(String state) {
        this.state = EtatMineur.valueOf(state);
    }

    public void setPositionPixel(Point positionPixel) {
        this.positionPixel = positionPixel;
    }

    public String getMinerStorage() {
        return minerStorage.toString();
    }

    public List<String> getMinerStorageList() {
        return minerStorage;
    }

    public void setStorage(String name) {
        minerStorage.clear();
        minerStorage.add(name);
    }
}
