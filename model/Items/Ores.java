package model.Items;

import java.util.List;
import java.util.Random;

// Classe mère
public abstract class Ores {
    // VARIABLES
    protected int nextId = 0; // identifiant unique
    protected int id;
    protected String type;
    protected OreState state; // etat visuel du minerais
    protected OreStatus status; // etat d'interaction du minerais
    protected int harvestingTime; // indique ou on en est dans le minage
    protected int maxHarvestTime; // garde une trace de la durée max de minage
    protected List<String> requirement; // tier d'objet nécéssaire pour miner le minerais
    protected int quantity; // Quantité aléatoire contenu dans le minerais
    protected static final Random rand = new Random();
    protected int quantityReturned; // Quantité actuelle retournée par le minage

    enum OreState {
        FULL,
        HALF,
        EMPTY
    }

    enum OreStatus {
        AVAILABLE,
        MINING,
        RESPAWN, // En cours de régénération
        RESPAWNED // Pret a etre de nouveau mis a disposition (laisse le temps de notifié la vue)
    }

    // CONSTRUCTOR
    public Ores(String type, OreState state, int harvestingTime, int maxHarvestTime, List<String> requirement,
            int quantity,
            OreStatus status, int quantityReturned) {
        this.id = ++nextId;
        this.type = type;
        this.state = state;
        this.harvestingTime = harvestingTime;
        this.maxHarvestTime = maxHarvestTime;
        this.requirement = requirement;
        this.quantity = quantity;
        this.status = status;
        this.quantityReturned = quantityReturned;
    }

    // GETTERS
    public String getState() {
        return state.toString();
    }

    public String getStatus() {
        return status.toString();
    }

    public int getHarvestingTime() {
        return harvestingTime;
    }

    public int getMaxHarvestTime() {
        return maxHarvestTime;
    }

    public List<String> getRequirement() {
        return requirement;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setStatus(String status) {
        this.status = OreStatus.valueOf(status);
    }

    public void setState(String state) {
        this.state = OreState.valueOf(state);
    }

    // METHODES
    // Logique de minage
    protected abstract int getRegenerateQuantity(); // Méthode abstraite pour la régénération de la quantité de minerais

    public int mine() {

        if (this.harvestingTime > 0) {
            this.harvestingTime -= 1;

            // À la moitié du temps de minage, on retourne la moitié de la quantité
            if (this.harvestingTime == this.maxHarvestTime / 2) {
                this.state = OreState.HALF;
                int quantityToReturnNow = this.quantity / 2;
                this.quantityReturned += quantityToReturnNow; // On garde une trace de ce qui a été retourné
                return quantityToReturnNow;
            } else {
                // En dehors de la moitié et la totalité du temps de minage, on retourne 0
                return 0;
            }
        } else {
            // Une fois le minage terminé, on passe à l'état EMPTY et on planifie le respawn
            this.state = OreState.EMPTY;
            this.status = OreStatus.RESPAWN;

            // Retourner la quantité restante
            return quantityReturned;
        }
    }

    public void voleurMine() {
        if (this.harvestingTime > 0) {
            this.harvestingTime -= 1;

            // À la moitié du temps de minage, on retourne la moitié de la quantité
            if (this.harvestingTime == this.maxHarvestTime / 2) {
                this.state = OreState.HALF;
                this.quantityReturned += this.quantity / 2; 
            }
        } else {
            // Une fois le minage terminé, on passe à l'état EMPTY et on planifie le respawn
            this.state = OreState.EMPTY;
            this.status = OreStatus.RESPAWN;
        }
    }

    // A la fin du temps de respawn on restaure le minerais
    public boolean regenerateOre() {
        if (status != OreStatus.RESPAWN) {
            return false;
        }

        // Si le minerais n'as pas retrouvé sont temps d'harvest intial, on incrémente
        // le temps de respawn
        if (harvestingTime < maxHarvestTime) {
            harvestingTime++;
            return false;
        }
        // Sinon on le rend disponible
        else {
            state = OreState.FULL;
            status = OreStatus.RESPAWNED;
            quantity = getRegenerateQuantity(); // Reset la quantité de manière aléatoire selon la logique défini dans
                                                // les
            // classes filles
            quantityReturned = 0; // Reset la quantité retournée
            return true;
        }
    }
}