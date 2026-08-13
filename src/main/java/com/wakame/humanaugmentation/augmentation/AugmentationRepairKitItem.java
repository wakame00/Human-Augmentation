package com.wakame.humanaugmentation.augmentation;

import com.wakame.humanaugmentation.registry.ModAttachments;
import java.util.Comparator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class AugmentationRepairKitItem extends Item {
    public AugmentationRepairKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.sidedSuccess(stack, true);

        var body = player.getData(ModAttachments.BODY);
        var target = body.allInstalled().entrySet().stream()
                .map(entry -> {
                    var item = BuiltInRegistries.ITEM.getOptional(entry.getValue()).orElse(null);
                    return item instanceof AugmentationItem augmentation && body.damage(entry.getKey()) > 0
                            ? new RepairTarget(entry.getKey(), augmentation, body.damage(entry.getKey())) : null;
                })
                .filter(java.util.Objects::nonNull)
                .max(Comparator.comparingDouble(RepairTarget::damageRatio));

        if (target.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.humanaugmentation.no_damaged_augmentation"), true);
            return InteractionResultHolder.fail(stack);
        }

        RepairTarget repair = target.get();
        int amount = Math.max(1, repair.augmentation().maximumDurability() / 4);
        int repaired = Math.min(repair.damage(), amount);
        body.setDamage(repair.key(), repair.damage() - repaired);
        ((ServerPlayer) player).syncData(ModAttachments.BODY);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        player.displayClientMessage(Component.translatable("message.humanaugmentation.augmentation_repaired", repaired), true);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.humanaugmentation.augmentation_repair_kit")
                .withStyle(ChatFormatting.GRAY));
    }

    private record RepairTarget(String key, AugmentationItem augmentation, int damage) {
        double damageRatio() {
            return (double) damage / augmentation.maximumDurability();
        }
    }
}
