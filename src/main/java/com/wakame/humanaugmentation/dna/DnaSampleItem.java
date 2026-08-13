package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.registry.ModDataComponents;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class DnaSampleItem extends Item {
    public DnaSampleItem(Properties properties) { super(properties); }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation source = stack.get(ModDataComponents.DNA_SOURCE);
        int stage = stack.getOrDefault(ModDataComponents.DNA_STAGE, 0);
        if (source != null) {
            Component entityName = BuiltInRegistries.ENTITY_TYPE.getOptional(source)
                    .map(type -> Component.translatableWithFallback(type.getDescriptionId(), readableFallback(source)))
                    .orElseGet(() -> Component.literal(readableFallback(source)));
            tooltip.add(Component.translatable("tooltip.humanaugmentation.dna_source", entityName).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.humanaugmentation.dna_stage." + Math.max(0, Math.min(3, stage)))
                    .withStyle(stage >= 3 ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        }
    }

    private static String readableFallback(ResourceLocation id) {
        return Arrays.stream(id.getPath().split("_"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining(" "));
    }
}
