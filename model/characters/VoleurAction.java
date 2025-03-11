package model.characters;

import java.awt.*;

public abstract class VoleurAction {
    protected Voleur voleur;
    protected Point destination;


    // CONSTRUCTOR
    public VoleurAction(Voleur voleur, Point destination) {
        this.voleur = voleur;
        this.destination = destination;
    }

    //METHODS
    public abstract void execute(); // Execute l'action définie par la classe fille
}
