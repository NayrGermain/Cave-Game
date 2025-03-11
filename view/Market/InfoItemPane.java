package view.Market;

import controller.DecreaseMoney;
import model.Constants;
import model.Item;
import model.Storage;
import model.characters.Miner;
import utils.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class InfoItemPane extends JPanel {

    public DecreaseMoney decThread;
    public Item currentItemDiplayed;
    private GridBagConstraints gbc;
    public InfosMarketPane imp;
    public JButton b; // Pour acheter
    private JPanel title;
    private JPanel contents;
    public String name;
    private Miner currentMiner;
    public Market market;

    private Storage storage;
    private Icon currentPickaxeDisplayed;
    private ImageIcon ironIcon,silverIcon,goldIcon,moneyIcon;
    private ImageLoader imgLoader;

    public InfoItemPane(Color c, InfosMarketPane imp) {
        this.setBackground(c);
        this.imp = imp;
        storage = Storage.getInstance();
        currentItemDiplayed = null;

        this.setLayout(new BorderLayout());
        /**Panel du Titre */
        title = new JPanel();
        title.setBackground(new Color(46, 29, 11));
        title.setPreferredSize(new Dimension(800 / 3, 40));
        JLabel label = new JLabel("Achats");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setForeground(Color.white);
        title.add(label);

        /** Panel du Contenu */

        contents = new JPanel();
        contents.setBackground(new Color(108, 68, 26));
        contents.setPreferredSize(new Dimension(800 / 3, 200));
       contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTH;

        this.add(title, BorderLayout.NORTH);
        this.add(contents, BorderLayout.CENTER);

        imgLoader = new ImageLoader();
        int iconWidth = 30;
        int iconHeight = 30;

        ironIcon = new ImageIcon(
                imgLoader.getImage("IRON_HALF").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        silverIcon = new ImageIcon(
                imgLoader.getImage("SILVER_HALF").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        goldIcon = new ImageIcon(
                imgLoader.getImage("GOLD_HALF").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
        moneyIcon = new ImageIcon(
                imgLoader.getImage("MONEY").getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));



        changeItem(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public void changeItem(Item it) {
        /** Fonction qui détermine et qui ajoute a la fenetre les différents contenus
         * et notamment les informations relatives a l'item qui a donc possiblement changé */

        contents.removeAll(); // On clear le panel pour le recharger avec les nouvelles informations
        currentItemDiplayed = it;
        b = new JButton("acheter");
        b.addActionListener(e -> {
            try {
                if (checkEnoughRessources()){//(storage.getMoney() >= getPrice()) {


                    System.out.println("Mineur " + currentMiner.getId() + " is Buying :" + currentItemDiplayed.nom);
                    currentMiner.setStorage(getName(currentItemDiplayed.nom));
                    int k = 0;
                    for(int i : getPrice())
                    {
                        System.out.println(i);
                        DecreaseMoney thread = new DecreaseMoney(k);
                        thread.initiate(i);
                        thread.start();
                        k++;
                    }

                    market.resetMiner();
                    market.changePanelFromState(0);
                    market.repaint();
                    this.loadUpgradeDone();

                } else {
                    System.out.println("Pas assez d'agent #money");
                    market.alert();
                }
            } catch (NullPointerException exception) {
                contents.removeAll();
                JLabel l = new JLabel("Pas d'item ");
                l.setBounds(50, 50, 100, 100);
            }

        });

        if (currentItemDiplayed == null) { // Si aucun item n'est selectionné
            b.setVisible(false);
            contents.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0)); // Ajoute une marge gauche de 80 pixels
            JLabel l = new JLabel("Aucun item selectionné");
            l.setForeground(Color.white);
            l.setBounds(25, 50, 100, 100);
            l.setBackground(Color.RED);
            contents.add(l);

        } else {
            //contents.setLayout(new GridLayout(4,1));
            contents.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
            contents.setLayout(new FlowLayout());
            b.setVisible(true);// Si un item est selectionné
            JLabel l = new JLabel("Item selectionné : " + name);
            l.setIcon(currentPickaxeDisplayed);
            l.setBounds(50, 50, 100, 100);
            l.setForeground(Color.white);
            int[] tab = getPrice();
            JLabel l1 = new JLabel("Prix de l'item : ");//+2
            l1.setBounds(50, 75, 100, 100);
            l1.setForeground(Color.white);
            //contents.add(l1);

            //JPanel neededRessource = new JPanel();
            //neededRessource.setLayout(new FlowLayout());//new BoxLayout(neededRessource,BoxLayout.Y_AXIS));
            //neededRessource.setBackground(Color.BLACK);
            JLabel lt1 = new JLabel(String.valueOf(tab[0]));lt1.setIcon(ironIcon);
            JLabel lt2 = new JLabel(String.valueOf(tab[1]));lt2.setIcon(silverIcon);
            JLabel lt3 = new JLabel(String.valueOf(tab[2]));lt3.setIcon(goldIcon);
            JLabel lt4 = new JLabel(String.valueOf(tab[3]));lt4.setIcon(moneyIcon);

            contents.add(l1);
            contents.add(lt1);
            contents.add(lt2);
            contents.add(lt3);
            contents.add(lt4);
            /*neededRessource.add(l1);
            neededRessource.add(lt1);
            neededRessource.add(lt2);
            neededRessource.add(lt3);
            neededRessource.add(lt4);*/
            //contents.add(neededRessource,BorderLayout.NORTH);

            b.setText("Acheter Item " + name);
            b.setSize(200, 60);
            b.setVisible(true);
            contents.add(l);
        }
        contents.add(b);
        // b.setVisible(true);
    }

    public void loadMinerStore()
    {
        /** Contenu chargé concernant le mineur afin de guider l'user */
        contents.removeAll();
        contents.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contents.setLayout(new FlowLayout());// Ajoute une marge gauche de 10 pixels
        JLabel l = new JLabel("Achat d'un mineur");
        l.setForeground(Color.white);
        l.setFont(new Font("Arial", Font.PLAIN, 18));

        ImageIcon imag = new ImageIcon(
                imgLoader.getImage("IDLE1").getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel img = new JLabel(imag);

        JLabel l2 = new JLabel("Coût de l'achat : " + getMinerPrice());
        l2.setForeground(Color.white);
        l2.setFont(new Font("Arial", Font.PLAIN, 18));
        contents.add(Box.createVerticalStrut(180));

        contents.add(l);
        contents.add(Box.createVerticalStrut(10));
        contents.add(img);
        contents.add(Box.createVerticalStrut(30));
        contents.add(l2);


    }

    public void loadUpgradeHelp()
    {
        /** Contenu affiché afin d'aider l'utilisateur */
        contents.removeAll();
        contents.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Ajoute une marge gauche de 10 pixels
        contents.add(Box.createVerticalStrut(180));
        JLabel l1 = new JLabel("Veuillez Sélectionner un mineur");
        JLabel l2 = new JLabel("ainsi que son outil a déverouiller");
        l1.setFont(new Font("Arial", Font.PLAIN, 20));
        l1.setForeground(Color.white);
        l2.setFont(new Font("Arial", Font.PLAIN, 20));
        l2.setForeground(Color.white);
        contents.add(l1);
        contents.add(l2);
        this.repaint();
    }

    public void loadUpgradeDone()
    {
        /** Contenu affiché afin d'aider l'utilisateur */
        contents.removeAll();
        JLabel l = new JLabel("Bravo, Achats Effectués");
        l.setFont(new Font("Arial", Font.PLAIN, 22));
        l.setForeground(Color.GRAY);
        contents.add(l);
        this.repaint();
    }

    public void getButtonClicked(Item  it, Miner min, Icon img) {
        /** A coder **/
        currentItemDiplayed = it;
        currentMiner = min;
        currentPickaxeDisplayed = img;
        name = getName(currentItemDiplayed.nom);
        // System.out.println("changement");
        changeItem(currentItemDiplayed);

    }

    public String getName(int nb) {
        switch (nb) {
            case 0:
                return "Pioche en pierre";
            case 1:
                return "Pioche en fer";
            case 2:
                return "Pioche en argent";
            case 3:
                return "Pioche en or";
            default:
                return "Pioche en pierre";
        }
    }

    public int[] getPrice() {
        /** Fonction déterminant le prix de chaque article
         * Cette fonction renvoie le résultat de sorte :
         * [quantité_de_fer , quantité_de_argent, quantité_de_or, quantité_de_money]
         * */

        /**storage.addStorage("iron", 100);
        storage.addStorage("silver", 100);
        storage.addStorage("gold", 100);
        storage.addStorage("money", 100);*/

        switch (currentItemDiplayed.nom) {
            case 0:
                return new int[]{0,0,0,1};
            case 1:
                return new int[]{10,0,0,10};
            case 2:
                return new int[]{10,20,0,25};
            case 3:
                return new int[]{0,25,20,35};
            default:
                return new int[]{0,0,0,0};
        }
    }

    private boolean checkEnoughRessources()
    {
        int[] ressourcesNeeded = getPrice();
        return (ressourcesNeeded[0] <= storage.getIron()&&
                ressourcesNeeded[1] <= storage.getSilver()&&
                ressourcesNeeded[2] <= storage.getGold()&&
                ressourcesNeeded[3] <= storage.getMoney());
    }


    public int getMinerPrice()
    {
        switch (market.getMiners().size())
        {
            case 1,2,3 : return 30;
            case 4,5,6 : return 70;
            case 7 : return 85;
            case 8 : return 95;
            default: return market.getMiners().size()*17;
        }
    }

    public void setMarket(Market market) {
        this.market = market;
    }
}