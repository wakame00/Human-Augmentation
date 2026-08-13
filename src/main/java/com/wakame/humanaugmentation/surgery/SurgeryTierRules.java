package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.body.Limb;

public final class SurgeryTierRules {
    public static int requiredTier(BodySlot slot) {
        return switch (slot) {
            case HEART, LUNGS, DIGESTIVE, BLOOD, MUSCLES, SKELETON, SKIN -> 1;
            case EYES, SPINE -> 2;
            case BRAIN -> 3;
            case SPECIAL -> 5;
        };
    }

    public static int requiredTier(Limb limb, int cell) {
        if (cell == 2 || cell == 6 || limb.isExtremity(cell)) return 1;
        if (cell == 1 || cell == 5) return 2;
        if (cell == 0 || cell == 4) return 3;
        return 4;
    }

    public static boolean unlocked(int equipmentTier, BodySlot slot) {
        return equipmentTier >= requiredTier(slot);
    }

    public static boolean unlocked(int equipmentTier, Limb limb, int cell) {
        return equipmentTier >= requiredTier(limb, cell);
    }

    private SurgeryTierRules() {}
}
