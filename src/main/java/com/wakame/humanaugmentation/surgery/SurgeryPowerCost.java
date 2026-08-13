package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.augmentation.AugmentationItem;
import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.body.Limb;

public final class SurgeryPowerCost {
    public static int installation(AugmentationItem part, BodySlot target) {
        double preferred = difficulty(part.preferredSlot());
        double targetDifficulty = difficulty(target);
        boolean correct = part.preferredSlot() == target;
        double multiplier = correct ? targetDifficulty : Math.max(preferred, targetDifficulty) * mismatch(part.preferredSlot(), target);
        return round(base(part.tier()) * multiplier);
    }

    public static int installation(AugmentationItem part, Limb limb, int cell) {
        BodySlot target = limb.isExtremity(cell) ? BodySlot.MUSCLES : limb.tissueAt(cell);
        double targetDifficulty = limb.isExtremity(cell) ? 0.8 : difficulty(target);
        double preferred = difficulty(part.preferredSlot());
        boolean correct = !limb.isExtremity(cell) && part.preferredSlot() == target;
        double mismatch = correct ? 1.0 : extremeMismatch(part.preferredSlot(), limb, cell);
        return round(base(part.tier()) * (correct ? targetDifficulty : Math.max(preferred, targetDifficulty) * mismatch));
    }

    public static int base(int tier) {
        return switch (tier) {
            case 1 -> 400;
            case 2 -> 2_000;
            case 3 -> 10_000;
            default -> 40_000;
        };
    }

    public static double difficulty(BodySlot slot) {
        return switch (slot) {
            case SKIN, MUSCLES -> 0.5;
            case SKELETON -> 0.6;
            case EYES -> 0.75;
            case DIGESTIVE -> 1.0;
            case LUNGS -> 1.25;
            case HEART, BLOOD -> 1.5;
            case SPINE -> 1.75;
            case BRAIN -> 2.0;
            case SPECIAL -> 2.5;
        };
    }

    private static double mismatch(BodySlot preferred, BodySlot target) {
        if (preferred.isOrgan() && target.isOrgan()) return 1.25;
        if (!preferred.isOrgan() && !target.isOrgan()) return 1.25;
        return 1.5;
    }

    private static double extremeMismatch(BodySlot preferred, Limb limb, int cell) {
        if (limb.isExtremity(cell)) return 2.0;
        return preferred == limb.tissueAt(cell) ? 1.0 : preferred.isOrgan() ? 2.0 : 1.5;
    }

    private static int round(double value) {
        return Math.max(1, (int)Math.ceil(value / 10.0) * 10);
    }

    private SurgeryPowerCost() {}
}
