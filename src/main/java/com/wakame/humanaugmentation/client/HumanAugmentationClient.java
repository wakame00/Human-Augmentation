package com.wakame.humanaugmentation.client;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.registry.ModMenus;
import com.wakame.humanaugmentation.registry.ModBlockEntities;
import com.wakame.humanaugmentation.surgery.SurgeryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = HumanAugmentation.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class HumanAugmentationClient {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.SURGERY.get(), SurgeryScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DNA_ANALYZER.get(), context -> new DnaAnalyzerRenderer());
    }

    private HumanAugmentationClient() {}
}
