package view.Market;

import javax.swing.*;
import java.awt.*;
import model.Constants;

public class ResearchPane extends JPanel {
    Market market;
    JPanel buttonsPanel;
    JPanel namePanel;

    public ResearchPane(Market m) {
        /* On peux ici déclarer les informations du marché et du joueur */
        super();
        this.market = m;
        this.setBackground(Color.GRAY);
        this.setLayout(new BorderLayout());

        namePanel = new JPanel();
        namePanel.setPreferredSize(new Dimension(800 / 3, 40));
        namePanel.setBackground(new Color(52, 33, 13));

        JLabel label = new JLabel("Boutons de sélections");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setForeground(Color.white);
        namePanel.add(label);
        this.add(namePanel, BorderLayout.NORTH);
        buttonsPanel = new JPanel();

        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 150));
        buttonsPanel.setBackground(new Color(132,84,34));

        final int x = 1;
        JButton b = new JButton("Acheter Un Mineur ");
        b.addActionListener(e -> {
            market.changePanelFromState(x);
            market.changeToMinerDisplay();

        });
        b.setPreferredSize(new Dimension(200, 30));
        buttonsPanel.add(b);

        final int y = 2;
        JButton b1 = new JButton("Améliorer un mineur ");
        b1.addActionListener(e -> {
            market.changePanelFromState(y);
            market.changeToUpgradeMinerDisplay();
        });
        b.setPreferredSize(new Dimension(200, 30));
        buttonsPanel.add(b1);

        add(buttonsPanel);
    }

}