package com.wakame.humanaugmentation.augmentation;

import net.minecraft.world.item.Item;

public final class ArtificialHeartItem extends AugmentationItem {
    public static final int CAPACITY_COST = 2;
    public static final int METABOLISM_COST = 10;
    public static final double HEALTH_BONUS = 4.0;
    public static final double MISPLACED_EFFICIENCY = 0.5;

    public ArtificialHeartItem(Properties properties) {
        super(com.wakame.humanaugmentation.body.BodySlot.HEART, 1, CAPACITY_COST, METABOLISM_COST, properties);
    }
}
