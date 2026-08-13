package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.registry.ModAttachments;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class GenePurgeItem extends Item {
    public GenePurgeItem(Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.sidedSuccess(stack, true);
        var body = player.getData(ModAttachments.BODY);
        int removed = body.removeAllDna();
        if (removed <= 0) {
            player.displayClientMessage(Component.translatable("message.humanaugmentation.no_dna_installed"), true);
            return InteractionResultHolder.fail(stack);
        }
        ((ServerPlayer) player).syncData(ModAttachments.BODY);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        player.displayClientMessage(Component.translatable("message.humanaugmentation.dna_purged", removed), true);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.humanaugmentation.gene_purge").withStyle(ChatFormatting.RED));
    }
}
