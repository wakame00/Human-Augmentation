package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.registry.ModAttachments;
import com.wakame.humanaugmentation.registry.ModDataComponents;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class GeneInjectionItem extends Item {
    public GeneInjectionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ResourceLocation source = stack.get(ModDataComponents.DNA_SOURCE);
        if (source == null) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide()) {
            String key = DnaProfile.sourceKey(source);
            var body = player.getData(ModAttachments.BODY);
            if (body.get(key) != null) {
                player.displayClientMessage(Component.translatable("message.humanaugmentation.dna_already_installed"), true);
                return InteractionResultHolder.fail(stack);
            }
            body.install(key, source);
            ((ServerPlayer)player).syncData(ModAttachments.BODY);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            player.displayClientMessage(Component.translatable("message.humanaugmentation.dna_installed"), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation source = stack.get(ModDataComponents.DNA_SOURCE);
        if (source != null) {
            Component entityName = BuiltInRegistries.ENTITY_TYPE.getOptional(source)
                    .map(type -> Component.translatableWithFallback(type.getDescriptionId(), readableFallback(source)))
                    .orElseGet(() -> Component.literal(readableFallback(source)));
            tooltip.add(Component.translatable("tooltip.humanaugmentation.dna_source", entityName)
                    .withStyle(ChatFormatting.AQUA));
            String abilityKey = switch (source.toString()) {
                case "minecraft:spider" -> "dna.humanaugmentation.ability.spider";
                case "minecraft:enderman" -> "dna.humanaugmentation.ability.enderman";
                case "minecraft:chicken" -> "dna.humanaugmentation.ability.chicken";
                case "minecraft:blaze" -> "dna.humanaugmentation.ability.blaze";
                case "minecraft:axolotl" -> "dna.humanaugmentation.ability.axolotl";
                default -> null;
            };
            if (abilityKey != null) tooltip.add(Component.translatable(abilityKey).withStyle(ChatFormatting.GREEN));
            else tooltip.add(Component.translatable("dna.humanaugmentation.ability.uncharacterized")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static String readableFallback(ResourceLocation id) {
        return Arrays.stream(id.getPath().split("_"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining(" "));
    }
}
