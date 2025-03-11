package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.*;
import model.observers.StorageObserver;
import utils.ImageLoader;

public class Northbar extends JPanel implements StorageObserver {
    // VARIABLES
    private JLabel timeLabel;
    private JLabel ironLabel;
    private JLabel goldLabel;
    private JLabel silverLabel;
    private JLabel moneyLabel; // Label pour l'argent
    private JLabel nbMinerLabel; // Label pour le nombre de mineurs

    // CONSTRUCTOR
    public Northbar() {
        initUI();
    }

    // METHODS
    private void initUI() {
        setLayout(new BorderLayout());
        ImageLoader imgLoader = new ImageLoader();

        int iconWidth = 40; // Définir la largeur désirée pour les icônes
        int iconHeight = 40; // Définir la hauteur désirée pour les icônes

        // Utilitaire pour redimensionner les images
        ImageIcon hourglassIcon = new ImageIcon(
                imgLoader.getImage("HOURGLASS").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        ImageIcon minerIcon = new ImageIcon(
                imgLoader.getImage("IDLE1").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        ImageIcon ironIcon = new ImageIcon(
                imgLoader.getImage("IRON_HALF").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        ImageIcon silverIcon = new ImageIcon(
                imgLoader.getImage("SILVER_HALF").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        ImageIcon goldIcon = new ImageIcon(
                imgLoader.getImage("GOLD_HALF").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        ImageIcon moneyIcon = new ImageIcon(
                imgLoader.getImage("MONEY").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));

        // LEFT panel
        timeLabel = new JLabel("00:00", hourglassIcon, JLabel.LEFT);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(timeLabel);
        add(leftPanel, BorderLayout.WEST);

        // CENTER panel
        nbMinerLabel = new JLabel("2", minerIcon, JLabel.LEFT);
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(nbMinerLabel);
        add(centerPanel, BorderLayout.CENTER);

        // RIGHT panel avec les ressources et l'argent
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ironLabel = new JLabel("0", ironIcon, JLabel.LEFT);
        silverLabel = new JLabel("0", silverIcon, JLabel.LEFT);
        goldLabel = new JLabel("0", goldIcon, JLabel.LEFT);
        moneyLabel = new JLabel("0", moneyIcon, JLabel.LEFT);

        rightPanel.add(ironLabel);
        rightPanel.add(silverLabel);
        rightPanel.add(goldLabel);
        rightPanel.add(moneyLabel);

        add(rightPanel, BorderLayout.EAST);
    }

    // Méthode pour mettre à jour le temps affiché
    public void setTime(String time) {
        timeLabel.setText(" " + time);
    }

    // Méthode pour mettre à jour le nombre de mineurs
    public void setNbMiner(int nbMiner) {
        nbMinerLabel.setText(" " + nbMiner);
    }

    // Méthode pour mettre à jour l'argent
    public void setMoney(int money) {
        moneyLabel.setText(" " + money);
    }

    // Methode pour mettre a jour le nombre de mineur
    public void setMinerCount(int nbMiner) {
        nbMinerLabel.setText(" " + nbMiner);
    }

    // OBSERVERS
    @Override
    public void onStorageChange(String type, int amount) {
        // Mettre à jour les labels selon le type de ressource qui a changé
        switch (type.toLowerCase()) {
            case "iron":
                ironLabel.setText(String.valueOf(amount));
                break;
            case "gold":
                goldLabel.setText(String.valueOf(amount));
                break;
            case "silver":
                silverLabel.setText(String.valueOf(amount));
                break;
            case "money": 
                setMoney(amount);
                break;
        }
    }
}
