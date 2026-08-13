package com.wakame.humanaugmentation;

import com.mojang.logging.LogUtils;
import com.wakame.humanaugmentation.registry.ModAttachments;
import com.wakame.humanaugmentation.compat.CompatTarget;
import com.wakame.humanaugmentation.compat.CompatibilityManager;
import com.wakame.humanaugmentation.registry.ModBlocks;
import com.wakame.humanaugmentation.registry.ModBlockEntities;
import com.wakame.humanaugmentation.registry.ModDataComponents;
import com.wakame.humanaugmentation.registry.ModItems;
import com.wakame.humanaugmentation.registry.ModMenus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(HumanAugmentation.MOD_ID)
public final class HumanAugmentation {
    public static final String MOD_ID = "humanaugmentation";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    private static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.humanaugmentation"))
            .icon(() -> ModItems.ARTIFICIAL_HEART.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.ARTIFICIAL_HEART.get());
                output.accept(ModItems.ARTIFICIAL_LUNGS_T1.get());
                output.accept(ModItems.ARTIFICIAL_EYES_T1.get());
                output.accept(ModItems.REINFORCED_SPINE_T1.get());
                output.accept(ModItems.ARTIFICIAL_MUSCLES_T1.get());
                output.accept(ModItems.REINFORCED_SKELETON_T1.get());
                output.accept(ModItems.SYNTHETIC_SKIN_T1.get());
                output.accept(ModItems.ADAPTIVE_SKIN_T2.get());
                if (CompatibilityManager.isLoaded(CompatTarget.CREATE)) {
                    output.accept(ModItems.KINETIC_MUSCLES_T2.get());
                }
                output.accept(ModItems.NATURAL_BRAIN.get());
                output.accept(ModItems.NATURAL_EYES.get());
                output.accept(ModItems.NATURAL_HEART.get());
                output.accept(ModItems.NATURAL_LUNGS.get());
                output.accept(ModItems.NATURAL_DIGESTIVE.get());
                output.accept(ModItems.NATURAL_BLOOD.get());
                output.accept(ModItems.NATURAL_MUSCLES.get());
                output.accept(ModItems.NATURAL_SKELETON.get());
                output.accept(ModItems.NATURAL_SPINE.get());
                output.accept(ModItems.NATURAL_SKIN.get());
                output.accept(ModItems.NATURAL_SPECIAL.get());
                output.accept(ModItems.NATURAL_RIGHT_HAND.get());
                output.accept(ModItems.NATURAL_LEFT_HAND.get());
                output.accept(ModItems.NATURAL_RIGHT_FOOT.get());
                output.accept(ModItems.NATURAL_LEFT_FOOT.get());
                output.accept(ModItems.DNA_SAMPLER.get());
                output.accept(ModItems.DNA_SAMPLE.get());
                output.accept(ModItems.DNA_EXTRACTION_REAGENT.get());
                output.accept(ModItems.DNA_AMPLIFICATION_MIX.get());
                output.accept(ModItems.STERILE_SYRINGE.get());
                output.accept(ModItems.GENE_INJECTION.get());
                output.accept(ModItems.GENE_PURGE.get());
                output.accept(ModItems.AUGMENTATION_REPAIR_KIT.get());
                output.accept(ModItems.CREATIVE_SURGERY_TERMINAL.get());
                output.accept(ModItems.CREATIVE_LIMB_LAYOUT_PREVIEW.get());
                output.accept(ModItems.CREATIVE_COMBINED_LEG_PREVIEW.get());
                output.accept(ModBlocks.SURGERY_TABLE_ITEM.get());
                output.accept(ModBlocks.MEDICAL_FRAME_ITEM.get());
                output.accept(ModBlocks.SURGICAL_ARM_ITEM.get());
                output.accept(ModBlocks.COMBUSTION_POWER_INPUT_ITEM.get());
                output.accept(ModBlocks.SURGERY_TABLE_T2_ITEM.get());
                output.accept(ModBlocks.MEDICAL_FRAME_T2_ITEM.get());
                output.accept(ModBlocks.SURGICAL_ARM_T2_ITEM.get());
                output.accept(ModBlocks.PRECISION_CONTROL_MODULE_ITEM.get());
                output.accept(ModBlocks.SURGERY_TABLE_T3_ITEM.get());
                output.accept(ModBlocks.MEDICAL_FRAME_T3_ITEM.get());
                output.accept(ModBlocks.SURGICAL_ARM_T3_ITEM.get());
                output.accept(ModBlocks.LIFE_SUPPORT_MODULE_ITEM.get());
                output.accept(ModBlocks.SURGERY_TABLE_T4_ITEM.get());
                output.accept(ModBlocks.MEDICAL_FRAME_T4_ITEM.get());
                output.accept(ModBlocks.SURGICAL_ARM_T4_ITEM.get());
                output.accept(ModBlocks.NEURAL_CONTROL_MODULE_ITEM.get());
                output.accept(ModBlocks.DNA_ANALYZER_ITEM.get());
                output.accept(ModBlocks.CREATIVE_FE_SOURCE_ITEM.get());
            }).build());

    public HumanAugmentation(IEventBus modBus, ModContainer container) {
        CompatibilityManager.initialize();
        ModAttachments.register(modBus);
        ModBlocks.register(modBus);
        ModBlockEntities.register(modBus);
        ModDataComponents.register(modBus);
        ModItems.register(modBus);
        ModMenus.register(modBus);
        TABS.register(modBus);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
