package view.Market;

import model.characters.Miner;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MarketPanel extends JPanel {
    private JPanel panel1, panel2, panel3;

    private ArrayList<Miner> miners;

    private Market i;

    public MarketPanel() {
        setSize(800, 600);
        miners = new ArrayList<>();

        panel1 = new InfosMarketPane();

        panel1.setPreferredSize(new Dimension(800, 50));

        panel3 = new InfoItemPane(new Color(128, 128, 128), (InfosMarketPane) panel1);

        i = new Market((InfoItemPane) panel3, miners);
        setLocation(600, 0);

        panel2 = new ResearchPane(i);

        setLayout(new BorderLayout());

        add(panel1, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1, 3)); // GridLayout pour les panels 2 et 3

        contentPanel.add(panel2);
        contentPanel.add(i);
        contentPanel.add(panel3);

        add(contentPanel, BorderLayout.CENTER);

    }

    public Market getMarket() {
        return i;
    }

    public void addMineur(Miner mineur) {
        this.miners.add(mineur);
    }
}