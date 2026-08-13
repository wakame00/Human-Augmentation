package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.body.Limb;
import com.wakame.humanaugmentation.body.BodyRegion;

/** Pixel coordinates shared by the surgery menu and its screen. */
public final class SurgeryLayout {
    public static final int WIDTH = 300;
    public static final int HEIGHT = 244;

    public static final int PRIMARY_X = 100;
    public static final int PRIMARY_Y = 46;
    public static final int PRIMARY_SPACING = 20;

    public static final int LIMB_X = 94;
    public static final int LIMB_Y = 42;
    public static final int LIMB_SPACING = 18;

    public static final int INVENTORY_X = 69;
    public static final int INVENTORY_Y = 164;
    public static final int HOTBAR_Y = 222;

    public static int primaryX(BodySlot slot) {
        return switch (slot) {
            case BRAIN, LUNGS, MUSCLES -> 104;
            case EYES, HEART, DIGESTIVE, SKELETON, SPINE -> 146;
            case BLOOD, SPECIAL -> 184;
            case SKIN -> 146;
        };
    }

    public static int primaryY(BodySlot slot) {
        return switch (slot) {
            case BRAIN, EYES, MUSCLES, SKELETON -> 46;
            case LUNGS, HEART, BLOOD, SPINE -> 76;
            case DIGESTIVE, SPECIAL -> 108;
            case SKIN -> 76;
        };
    }

    public static int limbX(Limb limb, int cell) {
        if (limb.isVertical()) return 120 + limb.x(cell) * 18;
        int x = LIMB_X + limb.x(cell) * LIMB_SPACING;
        return limb.isExtremity(cell) ? x + 6 : x;
    }

    public static int limbY(Limb limb, int cell) {
        int y = limb.isVertical() ? 36 + limb.y(cell) * 17
                : LIMB_Y + limb.y(cell) * LIMB_SPACING;
        if (limb.isVertical() && limb.isExtremity(cell)) y += 2;
        if (!limb.isVertical() && limb.isExtremity(cell)) y--;
        return y;
    }

    public static int previewLimbX(Limb limb, int cell) {
        if (!limb.isVertical()) {
            int rightArmX = switch (cell) {
                case 0, 10 -> 90;
                case 1, 11 -> 108;
                case 4 -> 94;
                case 5 -> 112;
                case 6 -> 134;
                case 2, 12 -> 152;
                case 3, 13 -> 170;
                case 7 -> 150;
                case 8 -> 168;
                case 9 -> 190;
                default -> 90;
            };
            return limb == Limb.LEFT_ARM ? 282 - rightArmX : rightArmX;
        }
        int centeredX = switch (cell) {
            case 0, 2, 10, 11 -> 96;
            case 1, 3, 12, 13 -> 132;
            default -> 114;
        };
        return centeredX + (limb == Limb.RIGHT_LEG ? 20 : -8);
    }

    public static int previewLimbY(Limb limb, int cell) {
        if (!limb.isVertical()) {
            return switch (cell) {
                case 0, 1, 2, 3 -> 44;
                case 4, 5, 6, 7, 8, 9 -> 68;
                case 10, 11, 12, 13 -> 92;
                default -> 44;
            };
        }
        return switch (cell) {
            case 0, 1, 4 -> 36;
            case 2, 3, 5 -> 56;
            case 6 -> 77;
            case 10, 12, 7 -> 96;
            case 11, 13, 8 -> 114;
            case 9 -> 133;
            default -> 36;
        };
    }

    public static int combinedLegX(Limb limb, int cell) {
        int base = limb == Limb.LEFT_LEG ? 90 : 150;
        return base + limb.x(cell) * 18;
    }

    public static int combinedLegY(Limb limb, int cell) {
        return 38 + limb.y(cell) * 18;
    }

    public static int combinedArmX(Limb limb, int cell) {
        int base = limb == Limb.LEFT_ARM ? 90 : 150;
        int column = switch (cell) {
            case 0, 2, 10, 11 -> 0;
            case 1, 3, 12, 13 -> 2;
            default -> 1;
        };
        return base + column * 18;
    }

    public static int combinedArmY(Limb limb, int cell) {
        int row = switch (cell) {
            case 0, 1, 4 -> 0;
            case 2, 3, 5 -> 1;
            case 6 -> 2;
            case 10, 12, 7 -> 3;
            case 11, 13, 8 -> 4;
            case 9 -> 5;
            default -> 0;
        };
        return 38 + row * 18;
    }

    public static int thirdLayerBonusX(BodyRegion region, int bonus) {
        return switch (region) {
            case ORGANS -> bonus == 0 ? 104 : 184;
            case TORSO -> bonus == 0 ? 104 : 184;
            case SKIN -> bonus == 0 ? 120 : 172;
            case RIGHT_ARM, LEFT_ARM -> 166;
            case RIGHT_LEG, LEFT_LEG -> bonus == 0 ? 120 : 156;
        };
    }

    public static int thirdLayerBonusY(BodyRegion region, int bonus) {
        return switch (region) {
            case ORGANS -> bonus == 0 ? 108 : 46;
            case TORSO -> 76;
            case SKIN -> 76;
            case RIGHT_ARM, LEFT_ARM -> bonus == 0 ? 42 : 78;
            case RIGHT_LEG, LEFT_LEG -> 104;
        };
    }

    private SurgeryLayout() {}
}
