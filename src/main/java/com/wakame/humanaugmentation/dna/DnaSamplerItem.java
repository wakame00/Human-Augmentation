package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.registry.ModDataComponents;
import com.wakame.humanaugmentation.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class DnaSamplerItem extends Item {
    public DnaSamplerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        DnaDefinition definition = DnaRegistry.INSTANCE.get(entityId);
        if (definition == null && entityId.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)
                && target.getType() != EntityType.PLAYER && target.getType() != EntityType.ARMOR_STAND) {
            definition = new DnaDefinition(entityId, vanillaSampleChance(target), java.util.List.of());
        }
        if (definition == null) {
            Component entityName = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId)
                    .map(type -> type.getDescription())
                    .orElseGet(() -> Component.literal(entityId.toString()));
            player.displayClientMessage(Component.translatable("message.humanaugmentation.no_dna", entityName).withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        if (player.getRandom().nextDouble() > definition.sampleChance()) {
            stack.hurtAndBreak(1, serverPlayer, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            player.displayClientMessage(Component.translatable("message.humanaugmentation.sample_failed").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        ItemStack sample = new ItemStack(ModItems.DNA_SAMPLE.get());
        sample.set(ModDataComponents.DNA_SOURCE, entityId);
        if (!player.getInventory().add(sample)) player.drop(sample, false);
        stack.hurtAndBreak(1, serverPlayer, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        player.displayClientMessage(Component.translatable("message.humanaugmentation.sample_collected", target.getDisplayName()).withStyle(ChatFormatting.GREEN), true);
        return InteractionResult.SUCCESS;
    }

    private static double vanillaSampleChance(LivingEntity target) {
        return switch (target.getType().getCategory()) {
            case MONSTER -> 0.35D;
            case AMBIENT, AXOLOTLS -> 0.75D;
            case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE -> 0.65D;
            case CREATURE -> 0.60D;
            default -> 0.50D;
        };
    }
}
