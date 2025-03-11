package utils;

import javax.swing.ImageIcon;

import model.Constants;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageLoader {
        private Map<String, Image> tileImages = new HashMap<>();
        private Map<String, String> stateToImagePrefixMap = new HashMap<>();

        public ImageLoader() {
                initializeImagePrefixMappings();
                loadImages();
        }

        private void loadImages() {
                // TODO : -mid priority- load from dir + ajuster les keys
                tileImages.put("FLOOR",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/floor/floor2.png")))
                                                .getImage());

                // door images
                tileImages.put("NORTH",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/north_door.png"))).getImage());
                tileImages.put("NORTH_CLOSED_IRON",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/north_door_iron.png")))
                                                .getImage());
                tileImages.put("NORTH_CLOSED_SILVER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/north_door_silver.png")))
                                                .getImage());
                tileImages.put("NORTH_CLOSED_GOLD",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/north_door_gold.png")))
                                                .getImage());
                tileImages.put("SOUTH",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/south_door.png"))).getImage());
                tileImages.put("SOUTH_CLOSED_IRON",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/south_door_iron.png")))
                                                .getImage());
                tileImages.put("SOUTH_CLOSED_SILVER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/south_door_silver.png")))
                                                .getImage());
                tileImages.put("SOUTH_CLOSED_GOLD",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/south_door_gold.png")))
                                                .getImage());
                tileImages.put("WEST",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/west_door.png"))).getImage());
                tileImages.put("WEST_CLOSED_IRON",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/west_door_iron.png")))
                                                .getImage());
                tileImages.put("WEST_CLOSED_SILVER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/west_door_silver.png")))
                                                .getImage());
                tileImages.put("WEST_CLOSED_GOLD",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/west_door_gold.png")))
                                                .getImage());
                tileImages.put("EAST",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/east_door.png"))).getImage());
                tileImages.put("EAST_CLOSED_IRON",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/east_door_iron.png")))
                                                .getImage());
                tileImages.put("EAST_CLOSED_SILVER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/east_door_silver.png")))
                                                .getImage());
                tileImages.put("EAST_CLOSED_GOLD",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/doors/east_door_gold.png")))
                                                .getImage());

                // border images
                tileImages.put("TOP_LEFT_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/corner_tl.png")))
                                                .getImage());
                tileImages.put("TOP_RIGHT_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/corner_tr.png")))
                                                .getImage());
                tileImages.put("BOTTOM_LEFT_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/corner_bl.png")))
                                                .getImage());
                tileImages.put("BOTTOM_RIGHT_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/corner_br.png")))
                                                .getImage());
                tileImages.put("TOP_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/top_border.png")))
                                                .getImage());
                tileImages.put("BOTTOM_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/bottom_border.png")))
                                                .getImage());
                tileImages.put("LEFT_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/left_border.png")))
                                                .getImage());
                tileImages.put("RIGHT_BORDER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/borders/right_border.png")))
                                                .getImage());

                // Ores images
                tileImages.put("GOLD_FULL_HOVER",
                                new ImageIcon(Objects.requireNonNull(getClass()
                                                .getResource("../assets/ores/Yellow-green_crystal1_HOVER.png")))
                                                .getImage());
                tileImages.put("GOLD_FULL",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Yellow-green_crystal1.png")))
                                                .getImage());
                tileImages.put("GOLD_HALF",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Yellow-green_crystal3.png")))
                                                .getImage());
                tileImages.put("GOLD_EMPTY",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Yellow-green_crystal4.png")))
                                                .getImage());
                tileImages.put("SILVER_FULL_HOVER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Blue_crystal1_HOVER.png")))
                                                .getImage());
                tileImages.put("SILVER_FULL",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Blue_crystal1.png")))
                                                .getImage());
                tileImages.put("SILVER_HALF",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Blue_crystal3.png")))
                                                .getImage());
                tileImages.put("SILVER_EMPTY",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Blue_crystal4.png")))
                                                .getImage());
                tileImages.put("IRON_FULL_HOVER",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Black_crystal1_HOVER.png")))
                                                .getImage());
                tileImages.put("IRON_FULL",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Black_crystal1.png")))
                                                .getImage());
                tileImages.put("IRON_HALF",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Black_crystal3.png")))
                                                .getImage());
                tileImages.put("IRON_EMPTY",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/ores/Black_crystal4.png")))
                                                .getImage());

                // obstacles images
                tileImages.put("OBSTACLE",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/obstacles/obstacle1.png")))
                                                .getImage());

                // items images
                tileImages.put("WOODEN_PICKAXE",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/items/wooden_pickaxe.png")))
                                                .getImage());
                tileImages.put("IRON_PICKAXE",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/items/iron_pickaxe.png")))
                                                .getImage());
                tileImages.put("SILVER_PICKAXE",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/items/silver_pickaxe.png")))
                                                .getImage());
                tileImages.put("GOLD_PICKAXE",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/items/gold_pickaxe.png")))
                                                .getImage());
                tileImages.put("MONEY",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/items/coin.png")))
                                                .getImage());
                tileImages.put("HOURGLASS",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/items/hourglass.png")))
                                                .getImage());

                // miner images
                // IDLE
                tileImages.put("IDLE1",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle1.png")))
                                                .getImage());
                tileImages.put("IDLE2",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle2.png")))
                                                .getImage());
                tileImages.put("IDLE3",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle3.png")))
                                                .getImage());
                tileImages.put("IDLE4",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle4.png")))
                                                .getImage());
                tileImages.put("IDLE5",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle5.png")))
                                                .getImage());
                tileImages.put("IDLE6",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle6.png")))
                                                .getImage());
                tileImages.put("IDLE7",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle7.png")))
                                                .getImage());
                tileImages.put("IDLE8",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/idle8.png")))
                                                .getImage());

                // MINE
                tileImages.put("MINE1",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine1.png")))
                                                .getImage());
                tileImages.put("MINE2",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine2.png")))
                                                .getImage());
                tileImages.put("MINE3",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine3.png")))
                                                .getImage());
                tileImages.put("MINE4",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine4.png")))
                                                .getImage());
                tileImages.put("MINE5",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine5.png")))
                                                .getImage());
                tileImages.put("MINE6",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine6.png")))
                                                .getImage());
                tileImages.put("MINE7",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine7.png")))
                                                .getImage());
                tileImages.put("MINE8",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/mine8.png")))
                                                .getImage());

                // WALK
                tileImages.put("MOVE1",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move1.png")))
                                                .getImage());
                tileImages.put("MOVE2",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move2.png")))
                                                .getImage());
                tileImages.put("MOVE3",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move3.png")))
                                                .getImage());
                tileImages.put("MOVE4",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move4.png")))
                                                .getImage());
                tileImages.put("MOVE5",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move5.png")))
                                                .getImage());
                tileImages.put("MOVE6",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move6.png")))
                                                .getImage());
                tileImages.put("MOVE7",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move7.png")))
                                                .getImage());
                tileImages.put("MOVE8",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass().getResource("../assets/miner/move8.png")))
                                                .getImage());

                // SELECTED
                tileImages.put("SELECTED1",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected1.png"))).getImage());
                tileImages.put("SELECTED2",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected2.png"))).getImage());
                tileImages.put("SELECTED3",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected3.png"))).getImage());
                tileImages.put("SELECTED4",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected4.png"))).getImage());
                tileImages.put("SELECTED5",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected5.png"))).getImage());
                tileImages.put("SELECTED6",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected6.png"))).getImage());
                tileImages.put("SELECTED7",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected7.png"))).getImage());
                tileImages.put("SELECTED8",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/miner/selected8.png"))).getImage());

                // voleur images
                // WALK
                tileImages.put("MOVEVOLEUR1",
                                new ImageIcon(Objects.requireNonNull(
                                                getClass().getResource("../assets/voleur/moveVoleur1.png")))
                                                .getImage());
                tileImages.put("MOVEVOLEUR2",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/moveVoleur2.png")))
                                                .getImage());

                tileImages.put("MOVEVOLEUR3",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/moveVoleur3.png")))
                                                .getImage());

                tileImages.put("MOVEVOLEUR4",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/moveVoleur4.png")))
                                                .getImage());

                tileImages.put("MOVEVOLEUR5",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/moveVoleur5.png")))
                                                .getImage());
                tileImages.put("MOVEVOLEUR6",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/moveVoleur5.png")))
                                                .getImage());

                // MINE
                tileImages.put("MINEVOLEUR1",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/mineVoleur1.png")))
                                                .getImage());
                tileImages.put("MINEVOLEUR2",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/mineVoleur2.png")))
                                                .getImage());

                tileImages.put("MINEVOLEUR3",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/mineVoleur3.png")))
                                                .getImage());

                tileImages.put("MINEVOLEUR4",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/mineVoleur4.png")))
                                                .getImage());

                tileImages.put("MINEVOLEUR5",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/mineVoleur5.png")))
                                                .getImage());

                tileImages.put("MINEVOLEUR6",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/mineVoleur6.png")))
                                                .getImage());

                // IDLE

                tileImages.put("IDLEVOLEUR1",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/idleVoleur1.png")))
                                                .getImage());

                tileImages.put("IDLEVOLEUR2",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/idleVoleur2.png")))
                                                .getImage());

                tileImages.put("IDLEVOLEUR3",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/idleVoleur3.png")))
                                                .getImage());

                tileImages.put("IDLEVOLEUR4",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/idleVoleur4.png")))
                                                .getImage());

                tileImages.put("IDLEVOLEUR5",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/idleVoleur5.png")))
                                                .getImage());

                tileImages.put("IDLEVOLEUR6",
                                new ImageIcon(Objects
                                                .requireNonNull(getClass()
                                                                .getResource("../assets/voleur/idleVoleur6.png")))
                                                .getImage());
        }

        // retourne l'image correspondant à la clé construite
        public Image getImage(String key) {
                Image image = tileImages.get(key);
                if (image == null) {
                        System.err.println("Image not found for key: " + key);
                }
                return image;
        }

        public Image getAxeIcon(String type) {
                switch (type) {
                        case Constants.WOODEN_PICKAXE:
                                return getImage("WOODEN_PICKAXE");
                        case Constants.IRON_PICKAXE:
                                return getImage("IRON_PICKAXE");
                        case Constants.SILVER_PICKAXE:
                                return getImage("SILVER_PICKAXE");
                        case Constants.GOLD_PICKAXE:
                                return getImage("GOLD_PICKAXE");
                        default:
                                return null;
                }
        }

        public Image getTileImage(int tileType) {
                switch (tileType) {
                        case Constants.TOP_BORDER:
                                return getImage("TOP_BORDER");
                        case Constants.BOTTOM_BORDER:
                                return getImage("BOTTOM_BORDER");
                        case Constants.LEFT_BORDER:
                                return getImage("LEFT_BORDER");
                        case Constants.RIGHT_BORDER:
                                return getImage("RIGHT_BORDER");
                        case Constants.TOP_LEFT_BORDER:
                                return getImage("TOP_LEFT_BORDER");
                        case Constants.TOP_RIGHT_BORDER:
                                return getImage("TOP_RIGHT_BORDER");
                        case Constants.BOTTOM_LEFT_BORDER:
                                return getImage("BOTTOM_LEFT_BORDER");
                        case Constants.BOTTOM_RIGHT_BORDER:
                                return getImage("BOTTOM_RIGHT_BORDER");
                        case Constants.FLOOR:
                                return getImage("FLOOR");
                        case Constants.OBSTACLE:
                                return getImage("OBSTACLE");
                        case Constants.NORTH_OPENING:
                                return getImage("NORTH");
                        case Constants.SOUTH_OPENING:
                                return getImage("SOUTH");
                        case Constants.EAST_OPENING:
                                return getImage("EAST");
                        case Constants.WEST_OPENING:
                                return getImage("WEST");
                        case Constants.START_ASSET:
                                // return imageLoader.getImage("START_ASSET");
                                return null; // TODO FIND ASSET
                        case Constants.EXIT_ASSET:
                                // return imageLoader.getImage("EXIT_ASSET");
                                return null; // TODO FIND ASSET
                        case Constants.NORTH_DOOR_CLOSED_IRON:
                                return getImage("NORTH_CLOSED_IRON");
                        case Constants.NORTH_DOOR_CLOSED_SILVER:
                                return getImage("NORTH_CLOSED_SILVER");
                        case Constants.NORTH_DOOR_CLOSED_GOLD:
                                return getImage("NORTH_CLOSED_GOLD");
                        case Constants.SOUTH_DOOR_CLOSED_IRON:
                                return getImage("SOUTH_CLOSED_IRON");
                        case Constants.SOUTH_DOOR_CLOSED_SILVER:
                                return getImage("SOUTH_CLOSED_SILVER");
                        case Constants.SOUTH_DOOR_CLOSED_GOLD:
                                return getImage("SOUTH_CLOSED_GOLD");
                        case Constants.EAST_DOOR_CLOSED_IRON:
                                return getImage("EAST_CLOSED_IRON");
                        case Constants.EAST_DOOR_CLOSED_SILVER:
                                return getImage("EAST_CLOSED_SILVER");
                        case Constants.EAST_DOOR_CLOSED_GOLD:
                                return getImage("EAST_CLOSED_GOLD");
                        case Constants.WEST_DOOR_CLOSED_IRON:
                                return getImage("WEST_CLOSED_IRON");
                        case Constants.WEST_DOOR_CLOSED_SILVER:
                                return getImage("WEST_CLOSED_SILVER");
                        case Constants.WEST_DOOR_CLOSED_GOLD:
                                return getImage("WEST_CLOSED_GOLD");
                        default:
                                return null; // Retourne null si le type de tuile n'est pas reconnu
                }
        }

        public Image getMineralImage(String type, String state) {
                String key = type.toUpperCase() + "_" + state; // Construit la clé, ex : "GOLD_FULL"
                return getImage(key);
        }

        // plusieurs action implique la même Key, on les map
        private void initializeImagePrefixMappings() {
                stateToImagePrefixMap.put("IDLE", "IDLE");
                stateToImagePrefixMap.put("TO_MINING", "MOVE");
                stateToImagePrefixMap.put("WALKING", "MOVE");
                stateToImagePrefixMap.put("TO_NEXT_TILE", "MOVE");
                stateToImagePrefixMap.put("MOVING", "MOVE");
                stateToImagePrefixMap.put("MINING", "MINE");
                stateToImagePrefixMap.put("SELECTED", "SELECTED");
                stateToImagePrefixMap.put("TO_BLOCKED_DOOR", "MOVE");
                stateToImagePrefixMap.put("UNLOCKING_DOOR", "MINE");
                stateToImagePrefixMap.put("TO_ENEMY", "MOVE");
        }

        // Récupéré l'image mappé à la state et au numéro de sprite
        public Image getMinerImage(String state, int spriteNum) {
                // Utilisez le mappage pour obtenir le préfixe d'image correct
                String imagePrefix = stateToImagePrefixMap.getOrDefault(state, "IDLE"); // Utilisez "IDLE" comme
                                                                                        // state par défaut
                String imageName = imagePrefix + spriteNum; // Construit le nom de l'image, ex: IDLE1, MOVE2, etc.
                return getImage(imageName);
        }

        // Récupéré l'image mappé à la state et au numéro de sprite
        public Image getVoleurImage(String state, int spriteNum) {
                // Utilisez le mappage pour obtenir le préfixe d'image correct
                String imagePrefix = stateToImagePrefixMap.getOrDefault(state, "IDLE"); // Utilisez "IDLE" comme
                // state par défaut
                String imageName = imagePrefix + "VOLEUR" + spriteNum; // Construit le nom de l'image, ex: IDLE1, MOVE2,
                                                                       // etc.
                return getImage(imageName);
        }

}
