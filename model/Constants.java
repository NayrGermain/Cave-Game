package model;

import java.awt.Point;

public class Constants {
    // DELAY
    public static final int DELAY = 16;

    // FRAME PRINCIPALE
    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 700;

    // MENU MARKET
    public static final int NB_ITEMS = 3;
    public static final int MONEY = 1000;

    // ITEMS
    public static final String WOODEN_PICKAXE = "Pioche en bois";
    public static final String IRON_PICKAXE = "Pioche en fer";
    public static final String SILVER_PICKAXE = "Pioche en argent";
    public static final String GOLD_PICKAXE = "Pioche en or";

    // MAP
    public static final int MAP_WIDTH = 14;
    public static final int MAP_HEIGHT = 14;
    public static final int EDGE_THRESHOLD = 100;
    public static final int MARGIN = 150;
    public static final int MINIMAP_WIDTH = 250;
    public static final int MINIMAP_HEIGHT = 250;

    // TILE
    public static final int LAYOUT_NUMBER_OFCUT = 9;
    public static final Point CENTER_TILE = new Point((LAYOUT_NUMBER_OFCUT / 2), (LAYOUT_NUMBER_OFCUT / 2));
    public static final int FLOOR = 1;
    public static final int OBSTACLE = 2;
    public static final int NORTH_OPENING = 4;
    public static final int SOUTH_OPENING = 5;
    public static final int EAST_OPENING = 6;
    public static final int WEST_OPENING = 7;
    public static final int TOP_BORDER = 8;
    public static final int BOTTOM_BORDER = 9;
    public static final int LEFT_BORDER = 10;
    public static final int RIGHT_BORDER = 11;
    public static final int TOP_LEFT_BORDER = 12;
    public static final int TOP_RIGHT_BORDER = 13;
    public static final int BOTTOM_LEFT_BORDER = 14;
    public static final int BOTTOM_RIGHT_BORDER = 15;
    public static final int EXIT_ASSET = 16;
    public static final int START_ASSET = 17;
    public static final int NORTH_DOOR_CLOSED_IRON = 18;
    public static final int NORTH_DOOR_CLOSED_SILVER = 19;
    public static final int NORTH_DOOR_CLOSED_GOLD = 20;
    public static final int SOUTH_DOOR_CLOSED_IRON = 21;
    public static final int SOUTH_DOOR_CLOSED_SILVER = 22;
    public static final int SOUTH_DOOR_CLOSED_GOLD = 23;
    public static final int EAST_DOOR_CLOSED_IRON = 24;
    public static final int EAST_DOOR_CLOSED_SILVER = 25;
    public static final int EAST_DOOR_CLOSED_GOLD = 26;
    public static final int WEST_DOOR_CLOSED_IRON = 27;
    public static final int WEST_DOOR_CLOSED_SILVER = 28;
    public static final int WEST_DOOR_CLOSED_GOLD = 29;

    // ENTITY
    public static final int MOVE_THRESHOLD = 5;
    public static final int MAX_ZONE_LEVEL = 3;
    public static final int MAX_THIEF = 5;
    public static final int GOLD_QUANTITY_THIEF = 30;
}
