package com.wakame.humanaugmentation.augmentation;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.body.Limb;
import com.wakame.humanaugmentation.registry.ModAttachments;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.common.util.TriState;
import com.wakame.humanaugmentation.surgery.SurgeryMenu;

@EventBusSubscriber(modid = HumanAugmentation.MOD_ID)
public final class BodyDeficiencyEvents {
    private static final int EFFECT_DURATION = 60;

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || player.tickCount % 20 != 0) return;
        var body = player.getData(ModAttachments.BODY);

        if (missing(body, BodySlot.BRAIN)) {
            apply(player, MobEffects.BLINDNESS, 0);
            apply(player, MobEffects.CONFUSION, 0);
            apply(player, MobEffects.WEAKNESS, 2);
        }
        if (missing(body, BodySlot.EYES)) apply(player, MobEffects.BLINDNESS, 0);
        if (missing(body, BodySlot.HEART)) {
            apply(player, MobEffects.WEAKNESS, 2);
            apply(player, MobEffects.MOVEMENT_SLOWDOWN, 1);
        }
        if (missing(body, BodySlot.LUNGS)) {
            apply(player, MobEffects.WEAKNESS, 1);
            apply(player, MobEffects.MOVEMENT_SLOWDOWN, 0);
        }
        if (missing(body, BodySlot.DIGESTIVE)) {
            apply(player, MobEffects.HUNGER, 1);
            apply(player, MobEffects.CONFUSION, 0);
        }
        if (missing(body, BodySlot.BLOOD)) {
            apply(player, MobEffects.WEAKNESS, 1);
            apply(player, MobEffects.POISON, 0);
        }
        if (missing(body, BodySlot.MUSCLES)) apply(player, MobEffects.WEAKNESS, 0);
        if (missing(body, BodySlot.SKELETON)) apply(player, MobEffects.MOVEMENT_SLOWDOWN, 0);
        if (missing(body, BodySlot.SPINE)) {
            apply(player, MobEffects.MOVEMENT_SLOWDOWN, 1);
            apply(player, MobEffects.DIG_SLOWDOWN, 1);
        }
        if (missing(body, BodySlot.SKIN)) apply(player, MobEffects.WEAKNESS, 0);
        int missingMuscles = 0;
        int missingBones = 0;
        int missingHands = 0;
        int missingFeet = 0;
        for (Limb limb : Limb.values()) {
            for (int cell = 0; cell < Limb.SLOT_COUNT; cell++) {
                if (hasAnyLimbLayer(body, limb, cell)) continue;
                if (limb.isExtremity(cell)) {
                    if (limb.isVertical()) missingFeet++; else missingHands++;
                } else if (limb.tissueAt(cell) == BodySlot.SKELETON) {
                    missingBones++;
                } else {
                    missingMuscles++;
                }
            }
        }

        if (missingMuscles > 0) apply(player, MobEffects.WEAKNESS, Math.min(3, (missingMuscles - 1) / 8));
        if (missingBones > 0) apply(player, MobEffects.MOVEMENT_SLOWDOWN, Math.min(2, (missingBones - 1) / 6));
        if (missingHands > 0) apply(player, MobEffects.DIG_SLOWDOWN, Math.min(2, missingHands));
        if (missingFeet > 0) apply(player, MobEffects.MOVEMENT_SLOWDOWN, Math.min(2, missingFeet));
    }

    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!event.getEntity().getItemInHand(event.getHand()).isEmpty() && unusableHand(event.getEntity(), event.getHand())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            warnMissingHand(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().getItemInHand(event.getHand()).isEmpty() && unusableHand(event.getEntity(), event.getHand())) {
            event.setUseItem(TriState.FALSE);
            warnMissingHand(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void interactEntity(PlayerInteractEvent.EntityInteract event) {
        if (!event.getEntity().getItemInHand(event.getHand()).isEmpty() && unusableHand(event.getEntity(), event.getHand())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            warnMissingHand(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void interactEntitySpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!event.getEntity().getItemInHand(event.getHand()).isEmpty() && unusableHand(event.getEntity(), event.getHand())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            warnMissingHand(event.getEntity());
        }
    }

    private static boolean unusableHand(Player player, InteractionHand hand) {
        Limb limb = hand == InteractionHand.MAIN_HAND ? Limb.RIGHT_ARM : Limb.LEFT_ARM;
        var body = player.getData(ModAttachments.BODY);
        for (int layer = 0; layer < 3; layer++) {
            if (body.get(SurgeryMenu.layeredLimbSlotKey(layer, limb, 9)) != null) return false;
        }
        return true;
    }

    private static void warnMissingHand(Player player) {
        if (!player.level().isClientSide()) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.humanaugmentation.missing_hand"), true);
        }
    }

    private static boolean missing(com.wakame.humanaugmentation.body.BodyData body, BodySlot slot) {
        return body.get(slot.name()) == null;
    }

    private static boolean hasAnyLimbLayer(com.wakame.humanaugmentation.body.BodyData body, Limb limb, int cell) {
        for (int layer = 0; layer < 3; layer++) {
            if (body.get(SurgeryMenu.layeredLimbSlotKey(layer, limb, cell)) != null) return true;
        }
        return false;
    }

    private static void apply(Player player, Holder<MobEffect> effect, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, amplifier, true, false));
    }

    private BodyDeficiencyEvents() {}
}
