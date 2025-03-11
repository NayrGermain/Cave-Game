package view.Market;

import model.Constants;
import model.Storage;

import javax.swing.*;
import java.awt.*;

public class InfosMarketPane extends JPanel {
    public InfosMarketPane() {
        /* On peux ici déclarer les informations du marché et du joueur */
        super();

        this.setBackground(new Color(85,70,56));
    }

    @Override
    public void paint(Graphics g) {
        /* Faire Ici l'interface graphique lié aux ressource du joueur ? */

        super.paint(g);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.white);
        g.drawString("InfosMarketPane", 435, 25);
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        //g.drawString("Money : " + Storage.getInstance().getMoney() + "$", 800, 25);
    }
}