package com.wakame.humanaugmentation.surgery;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class CreativeLimbLayoutPreviewItem extends Item {
    public CreativeLimbLayoutPreviewItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable(
                        "message.humanaugmentation.creative_terminal_only"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider((id, inventory, owner) ->
                    new SurgeryMenu(id, inventory, owner, SurgeryMenu.LIMB_PREVIEW_TERMINAL_POS),
                    Component.translatable("screen.humanaugmentation.limb_layout_preview")),
                    SurgeryMenu.LIMB_PREVIEW_TERMINAL_POS);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
