package com.wakame.humanaugmentation.body;

public enum Limb {
    RIGHT_ARM(false), LEFT_ARM(false), RIGHT_LEG(true), LEFT_LEG(true);

    public static final int SLOT_COUNT = 14;
    private final boolean vertical;

    Limb(boolean vertical) {
        this.vertical = vertical;
    }

    public String slotKey(int index) {
        return "limb_" + name().toLowerCase(java.util.Locale.ROOT) + "_" + index;
    }

    public BodySlot tissueAt(int index) {
        return index >= 4 && index <= 8 ? BodySlot.SKELETON : BodySlot.MUSCLES;
    }

    public boolean isExtremity(int index) { return index == 9; }
    public boolean isVertical() { return vertical; }
    public String naturalExtremityPath() {
        return switch (this) {
            case RIGHT_ARM -> "natural_right_hand";
            case LEFT_ARM -> "natural_left_hand";
            case RIGHT_LEG -> "natural_right_foot";
            case LEFT_LEG -> "natural_left_foot";
        };
    }
    public int columns() { return vertical ? 3 : 6; }
    public int rows() { return vertical ? 6 : 3; }

    public int x(int index) {
        int x;
        if (index < 4) x = index;
        else if (index < 9) x = index - 4;
        else if (index == 9) x = 5;
        else x = index - 10;
        if (!vertical) return x;
        int row = yHorizontal(index);
        return switch (row) { case 0 -> 0; case 1 -> 1; default -> 2; };
    }

    public int y(int index) {
        if (!vertical) return yHorizontal(index);
        if (index < 4) return index;
        if (index < 9) return index - 4;
        if (index == 9) return 5;
        return index - 10;
    }

    private static int yHorizontal(int index) {
        if (index < 4) return 0;
        if (index <= 9) return 1;
        return 2;
    }
}
