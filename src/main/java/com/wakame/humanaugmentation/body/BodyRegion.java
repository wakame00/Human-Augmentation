package com.wakame.humanaugmentation.body;

public enum BodyRegion {
    ORGANS, TORSO, SKIN, RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG;

    public boolean contains(BodySlot slot) {
        return switch (this) {
            case ORGANS -> slot == BodySlot.BRAIN || slot == BodySlot.EYES || slot == BodySlot.HEART
                    || slot == BodySlot.LUNGS || slot == BodySlot.DIGESTIVE || slot == BodySlot.BLOOD
                    || slot == BodySlot.SPECIAL;
            case TORSO -> slot == BodySlot.MUSCLES || slot == BodySlot.SKELETON
                    || slot == BodySlot.SPINE;
            case SKIN -> slot == BodySlot.SKIN;
            case RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG -> false;
        };
    }

    public Limb limb() {
        return switch (this) {
            case RIGHT_ARM -> Limb.RIGHT_ARM;
            case LEFT_ARM -> Limb.LEFT_ARM;
            case RIGHT_LEG -> Limb.RIGHT_LEG;
            case LEFT_LEG -> Limb.LEFT_LEG;
            default -> null;
        };
    }
}
