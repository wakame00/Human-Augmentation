package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.registry.ModAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HumanAugmentation.MOD_ID)
public final class DnaAbilityEvents {
    private static final ResourceLocation WALL_AFFINITY = HumanAugmentation.id("wall_affinity");
    private static final ResourceLocation SPATIAL_SENSE = HumanAugmentation.id("spatial_sense");
    private static final ResourceLocation HYDROPHOBIC_TISSUE = HumanAugmentation.id("hydrophobic_tissue");
    private static final ResourceLocation LIGHTWEIGHT_BONES = HumanAugmentation.id("lightweight_bones");
    private static final ResourceLocation HEAT_RESISTANCE = HumanAugmentation.id("heat_resistance");
    private static final ResourceLocation AQUATIC_REGENERATION = HumanAugmentation.id("aquatic_regeneration");

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        var body = player.getData(ModAttachments.BODY);

        if (DnaProfile.hasGene(body, WALL_AFFINITY) && player.horizontalCollision && !player.isShiftKeyDown()) {
            var movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.x, Math.max(movement.y, 0.18D), movement.z);
            player.resetFallDistance();
            player.hurtMarked = true;
        }

        if (player.tickCount % 20 != 0) return;
        if (DnaProfile.hasGene(body, LIGHTWEIGHT_BONES)) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, true, false));
        }
        if (DnaProfile.hasGene(body, HEAT_RESISTANCE)) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
        }
        if (DnaProfile.hasGene(body, AQUATIC_REGENERATION) && player.isInWaterOrBubble()) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, false));
        }
        if (DnaProfile.hasGene(body, SPATIAL_SENSE)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, false));
        }
        if (DnaProfile.hasGene(body, HYDROPHOBIC_TISSUE) && player.isInWaterOrRain()) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, true, false));
        }
    }

    private DnaAbilityEvents() {}
}
