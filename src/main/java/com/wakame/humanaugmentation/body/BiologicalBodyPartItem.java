package com.wakame.humanaugmentation.body;

import net.minecraft.world.item.Item;

public final class BiologicalBodyPartItem extends Item {
    private final BodySlot bodySlot;

    public BiologicalBodyPartItem(BodySlot bodySlot, Properties properties) {
        super(properties);
        this.bodySlot = bodySlot;
    }

    public BodySlot bodySlot() {
        return bodySlot;
    }
}
