package com.wakame.humanaugmentation.registry;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.surgery.CombustionPowerInputBlockEntity;
import com.wakame.humanaugmentation.dna.DnaAnalyzerBlockEntity;
import com.wakame.humanaugmentation.dna.CreativeFeSourceBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HumanAugmentation.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombustionPowerInputBlockEntity>> COMBUSTION_POWER_INPUT =
            TYPES.register("combustion_power_input", () -> BlockEntityType.Builder.of(
                    CombustionPowerInputBlockEntity::new, ModBlocks.COMBUSTION_POWER_INPUT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DnaAnalyzerBlockEntity>> DNA_ANALYZER =
            TYPES.register("dna_analyzer", () -> BlockEntityType.Builder.of(
                    DnaAnalyzerBlockEntity::new, ModBlocks.DNA_ANALYZER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeFeSourceBlockEntity>> CREATIVE_FE_SOURCE =
            TYPES.register("creative_fe_source", () -> BlockEntityType.Builder.of(
                    CreativeFeSourceBlockEntity::new, ModBlocks.CREATIVE_FE_SOURCE.get()).build(null));

    private ModBlockEntities() {}

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        bus.addListener(ModBlockEntities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, COMBUSTION_POWER_INPUT.get(),
                (input, side) -> input.inventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DNA_ANALYZER.get(),
                (analyzer, side) -> analyzer.inventory());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DNA_ANALYZER.get(),
                (analyzer, side) -> analyzer.energy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, CREATIVE_FE_SOURCE.get(),
                (source, side) -> source.energy());
    }
}
