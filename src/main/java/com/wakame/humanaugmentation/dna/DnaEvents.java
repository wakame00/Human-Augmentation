package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.HumanAugmentation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = HumanAugmentation.MOD_ID)
public final class DnaEvents {
    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) { event.addListener(DnaRegistry.INSTANCE); }
    private DnaEvents() {}
}
