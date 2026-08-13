package com.wakame.humanaugmentation.registry;

import com.wakame.humanaugmentation.HumanAugmentation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, HumanAugmentation.MOD_ID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> DNA_SOURCE = COMPONENTS.registerComponentType(
            "dna_source", builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DNA_STAGE = COMPONENTS.registerComponentType(
            "dna_stage", builder -> builder.persistent(com.mojang.serialization.Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));
    private ModDataComponents() {}
    public static void register(IEventBus bus) { COMPONENTS.register(bus); }
}
