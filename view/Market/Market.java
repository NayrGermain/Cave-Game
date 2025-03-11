package view.Market;

import model.Constants;
import model.Item;
import model.characters.Miner;
import utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Market extends JPanel {
    private ArrayList<Item> items;
    private InfoItemPane itemPage;
    private ArrayList<Miner> miners;
    private JPanel mainPane;
    private JPanel minerPanel;
    private JPanel buttonPanel;
    private JButton buyMiner = new JButton("Buy a Miner");
    private ArrayList<JButton> upgradeMiner;
    private Miner currentMiner;
    private int state;
    private JFrame alertFrame;
    private ImageIcon minerIcon;
    private ImageLoader imgLoader;
    private ImageIcon[] preloadedIcons;

    /** 1 = Buy a Miner, 2 = Upgrade Miner's Item **/

    public Market(InfoItemPane imp, ArrayList<Miner> miners) {
        /* Constructeur */
        super();
        itemPage = imp;
        itemPage.setMarket(this);
        this.miners = miners;
        upgradeMiner = new ArrayList<>();
        this.setLayout(new BorderLayout());
        imgLoader = new ImageLoader();
        buttonPanel = new JPanel();

        int minerIconWidth = 20; // Définir la largeur désirée pour les icônes
        int minerIconHeight = 20; // Définir la hauteur désirée pour les icônes

        preloadIcons();
        minerIcon = new ImageIcon(
                imgLoader.getImage("IDLE1").getScaledInstance(minerIconWidth, minerIconHeight, Image.SCALE_SMOOTH));

        items = new ArrayList<>(10);
        for (int i = 0; i <= 3; i++) {
            items.add(new Item(i));
        }

        this.state = 0;
        /** Panel du Titre */
        JPanel panel = new JPanel();
        mainPane = new JPanel();
        mainPane.setBackground(new Color(93, 55, 30));
        panel.setPreferredSize(new Dimension(800 / 3, 40));
        panel.setLayout(new FlowLayout());
        JLabel label = new JLabel("Market");
        label.setFont(new Font("Arial", Font.PLAIN, 28));
        label.setForeground(Color.white);
        panel.add(label);
        panel.setBackground(new Color(19, 16, 13));
        add(panel, BorderLayout.NORTH);
        add(mainPane, BorderLayout.CENTER);

        /** Pannel de l'alert si pas assez d'argent */
        alertFrame = new JFrame("Attention");
        alertFrame.setSize(300, 200);
        alertFrame.setVisible(false);
        alertFrame.setLocation(new Point(1920 / 2 - 150, 1080 / 2 - 100));
        // alertFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // alertFrame.setLayout();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(73, 10, 10));
        JLabel alertLabel = new JLabel("Pas assez d'argent !");
        alertLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        alertLabel.setForeground(Color.white);
        mainPanel.add(alertLabel, BorderLayout.CENTER);
        JButton alertButton = new JButton("Je comprends");
        alertButton.addActionListener(e1 -> {
            alertFrame.setVisible(false);
        });
        mainPanel.add(alertButton, BorderLayout.SOUTH);
        alertFrame.add(mainPanel);

    }

    public void paint(Graphics g) {
        super.paint(g);
    }

    public void getInfo() {
        if (currentMiner == null) {
            System.out.println("Error : no miner selected");
        } else {
            System.out.println("Current Miners  -------------------");
            System.out.println("id : " + currentMiner.getId());
        }
    }

    public void changePanelFromState(int state) {

        /*
         * if(state ==this.state)
         * {
         * System.out.println("already in this state");
         * return;
         * }
         */

        if (state < 0 || state > 3)// Si on est déjà dans cet état ou que l'état n'existe pas
        {
            System.out.println("Error : state not found");
            return;
        }
        this.state = state;
        changeDisplay();

    }

    public void changeDisplay() {
        mainPane.removeAll();
        mainPane.setLayout(new BorderLayout());
        mainPane.add(loadMiner(), BorderLayout.NORTH);

        switch (state) {
            case 1: // Achat de mineurs
                mainPane.add(buyMiner, BorderLayout.CENTER);
                System.out.println("Buy a Miner");
                break;
            case 2: // Amélioration des outils des mineurs
                if (currentMiner != null) {
                    setupToolPurchase(); // Configurer l'achat des outils
                    System.out.println("Upgrade");
                }
                break;
            default:
                System.out.println("Erreur: État non défini");
                break;
        }

        mainPane.revalidate();
        mainPane.repaint();
    }

    // Mettre en cache les icônes des pioches pour éviter de les recharger à chaque
    private void preloadIcons() {
        preloadedIcons = new ImageIcon[4]; // Les 4 icônes de pioches
        for (int i = 0; i < preloadedIcons.length; i++) {
            Image img = imgLoader.getImage(getImageKey(i));
            preloadedIcons[i] = new ImageIcon(img.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        }
    }

    private void setupToolPurchase() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(93, 55, 30));

        for (int i = 0; i < items.size(); i++) {
            JButton button = new JButton("Pioche : " + i, preloadedIcons[i]);
            button.setEnabled(!alreadyHas(i)); // Active le bouton seulement si le minerai n'est pas déjà acheté

            // Ajouter un listener pour chaque bouton
            int index = i;
            button.addActionListener(e -> {
                Icon img2 = new ImageIcon(getRightImg(index).getScaledInstance(70, 70, Image.SCALE_SMOOTH));
                itemPage.getButtonClicked(items.get(index), currentMiner, img2);
            });

            button.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer horizontalement chaque bouton
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(10)); // Espacer les boutons
        }

        mainPane.add(buttonPanel, BorderLayout.CENTER);
    }

    private String getImageKey(int index) {
        switch (index) {
            case 0:
                return "WOODEN_PICKAXE";
            case 1:
                return "IRON_PICKAXE";
            case 2:
                return "SILVER_PICKAXE";
            case 3:
                return "GOLD_PICKAXE";
            default:
                return "DEFAULT_IMAGE"; // pas d'image par défaut
        }
    }

    private boolean alreadyHas(int itemId) {
        /**
         * Définit si l'object passé en paramètre doit être affiché ou non
         * car Possibilité que l'on ai un meilleur objet ou que l'on ai pas
         * l'outil du palier précèdent
         */
        // System.out.println(itemId + currentMiner.getMinerStorage());
        return (itemId == 0 && (currentMiner.getMinerStorage().contains(Constants.WOODEN_PICKAXE)))
                || (itemId == 1 && (currentMiner.getMinerStorage().contains(Constants.IRON_PICKAXE)))
                || (itemId == 2 && (currentMiner.getMinerStorage().contains(Constants.SILVER_PICKAXE)))
                || (currentMiner.getMinerStorage().contains(Constants.GOLD_PICKAXE))
                || ((itemId == 2 || itemId == 3) && (currentMiner.getMinerStorage().contains(Constants.WOODEN_PICKAXE)))
                || ((itemId == 3) && (currentMiner.getMinerStorage().contains(Constants.IRON_PICKAXE)));
    }

    public void alert() {
        alertFrame.setVisible(true);
    }

    public JPanel loadMiner() {
        /**
         * Chargement du panel de mineurs
         * Affichant donc les mineurs dans notre jeu et
         * l'inventaire du mineur sélectionné
         */
        minerPanel = new JPanel();
        minerPanel.setBackground(new Color(83, 45, 20));
        minerPanel.setPreferredSize(new Dimension(800 / 3, 200 + (miners.size() / 4 * 30)));
        for (Miner m : miners) {
            JButton b = new JButton("Miner" + m.getId(), minerIcon);
            b.addActionListener(e -> {
                System.out.println("Miner " + m.getId());
                currentMiner = m;
                getInfo();
                Icon img = new ImageIcon((getAxeIcon(currentMiner).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                JLabel l = new JLabel("Actuellement : " + currentMiner.getMinerStorage());
                l.setIcon(img);
                l.setFont(new Font("Arial", Font.PLAIN, 15));
                l.setForeground(Color.white);
                changeDisplay();
                minerPanel.add(l);

            });
            minerPanel.add(b);

        }
        return minerPanel;
    }

    public int getState() {
        return state;
    }

    public JButton getBuyMiner() {
        return buyMiner;
    }

    public ArrayList<JButton> getItemsButtons() {
        return upgradeMiner;
    }

    public InfoItemPane getItemPage() {
        return itemPage;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void addMineur(Miner mineur) {
        this.miners.add(mineur);
        this.changeDisplay();
    }

    public Miner getMiner() {
        return currentMiner;
    }

    public void resetMiner() {
        currentMiner = null;
    }

    public void changeToMinerDisplay() {
        itemPage.loadMinerStore();
    }

    public void changeToUpgradeMinerDisplay() {
        itemPage.loadUpgradeHelp();
    }

    public Image getRightImg(int x) {
        switch (x) {
            default -> {
                return imgLoader.getImage("WOODEN_PICKAXE");
            }
            case 1 -> {
                return imgLoader.getImage("IRON_PICKAXE");
            }
            case 2 -> {
                return imgLoader.getImage("SILVER_PICKAXE");
            }
            case 3 -> {
                return imgLoader.getImage("GOLD_PICKAXE");
            }
        }
    }

    public Image getAxeIcon(Miner m) {
        String k = (m.getMinerStorage());
        switch (k) {
            case "[Pioche en bois]" -> {
                return imgLoader.getImage("WOODEN_PICKAXE");
            }
            case "[Pioche en fer]" -> {
                return imgLoader.getImage("IRON_PICKAXE");
            }
            case "[Pioche en argent]" -> {
                return imgLoader.getImage("SILVER_PICKAXE");
            }
            case "[Pioche en or]" -> {
                return imgLoader.getImage("GOLD_PICKAXE");
            }

            default -> {
                return imgLoader.getImage("GOLD_PICKAXE");
            }
        }
    }

    public ArrayList<Miner> getMiners() {
        return miners;
    }

    public ImageLoader getImgLoader() {
        return imgLoader;
    }
}