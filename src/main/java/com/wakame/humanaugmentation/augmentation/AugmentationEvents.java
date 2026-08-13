package com.wakame.humanaugmentation.augmentation;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.body.Limb;
import com.wakame.humanaugmentation.registry.ModAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HumanAugmentation.MOD_ID)
public final class AugmentationEvents {
    private static final ResourceLocation HEART_HEALTH = HumanAugmentation.id("artificial_heart_health");
    private static final ResourceLocation LUNGS_OXYGEN = HumanAugmentation.id("artificial_lungs_oxygen");
    private static final ResourceLocation SPINE_KNOCKBACK = HumanAugmentation.id("reinforced_spine_knockback");
    private static final ResourceLocation MUSCLE_DAMAGE = HumanAugmentation.id("artificial_muscles_damage");
    private static final ResourceLocation SKELETON_ARMOR = HumanAugmentation.id("reinforced_skeleton_armor");
    private static final ResourceLocation SKIN_ARMOR = HumanAugmentation.id("artificial_skin_armor");
    private static final ResourceLocation KINETIC_ATTACK_SPEED = HumanAugmentation.id("kinetic_muscles_attack_speed");
    private static final ResourceLocation KINETIC_MOVEMENT = HumanAugmentation.id("kinetic_muscles_movement");

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide() || event.getEntity().tickCount % 20 != 0) {
            return;
        }
        var player = event.getEntity();
        var body = player.getData(ModAttachments.BODY);
        java.util.Map<ResourceLocation, java.util.List<Double>> installedByType = new java.util.HashMap<>();
        java.util.Set<ResourceLocation> setPieces = new java.util.HashSet<>();

        for (var entry : body.allInstalled().entrySet()) {
            var item = BuiltInRegistries.ITEM.getOptional(entry.getValue()).orElse(null);
            if (!(item instanceof AugmentationItem augmentation)) continue;
            double efficiency = placementEfficiency(entry.getKey(), augmentation.preferredSlot())
                    * augmentation.durabilityEfficiency(body.damage(entry.getKey()));
            ResourceLocation id = entry.getValue();
            installedByType.computeIfAbsent(id, ignored -> new java.util.ArrayList<>()).add(efficiency);
            setPieces.add(id);
        }

        double heart = diminishingEfficiency(installedByType, HumanAugmentation.id("artificial_heart"));
        double lungs = diminishingEfficiency(installedByType, HumanAugmentation.id("artificial_lungs_t1"));
        double eyes = diminishingEfficiency(installedByType, HumanAugmentation.id("artificial_eyes_t1"));
        double spine = diminishingEfficiency(installedByType, HumanAugmentation.id("reinforced_spine_t1"));
        double muscles = diminishingEfficiency(installedByType, HumanAugmentation.id("artificial_muscles_t1"));
        double skeleton = diminishingEfficiency(installedByType, HumanAugmentation.id("reinforced_skeleton_t1"));
        double syntheticSkin = diminishingEfficiency(installedByType, HumanAugmentation.id("synthetic_skin_t1"));
        double adaptiveSkin = diminishingEfficiency(installedByType, HumanAugmentation.id("adaptive_skin_t2"));
        double kineticMuscles = diminishingEfficiency(installedByType, HumanAugmentation.id("kinetic_muscles_t2"));

        updateModifier(player.getAttribute(Attributes.MAX_HEALTH), HEART_HEALTH,
                heart * ArtificialHeartItem.HEALTH_BONUS);
        updateModifier(player.getAttribute(Attributes.OXYGEN_BONUS), LUNGS_OXYGEN, lungs * 2.0);
        updateModifier(player.getAttribute(Attributes.KNOCKBACK_RESISTANCE), SPINE_KNOCKBACK, spine * 0.2);
        updateModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), MUSCLE_DAMAGE, muscles * 2.0);
        updateModifier(player.getAttribute(Attributes.ARMOR), SKELETON_ARMOR, skeleton * 3.0);
        updateModifier(player.getAttribute(Attributes.ARMOR_TOUGHNESS), SKIN_ARMOR,
                syntheticSkin * 1.0 + adaptiveSkin * 3.0);
        updateModifier(player.getAttribute(Attributes.ATTACK_SPEED), KINETIC_ATTACK_SPEED, kineticMuscles * 0.35);
        updateModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), KINETIC_MOVEMENT, kineticMuscles * 0.015);

        if (eyes > 0.0) player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, false));
        if (adaptiveSkin > 0.0) player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false));
        if (setPieces.size() >= 3) player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
        if (setPieces.size() >= 6) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, true, false));
    }

    private static double placementEfficiency(String key, BodySlot preferred) {
        if (key.equals(preferred.name())
                || (key.startsWith("layer_") && key.endsWith("_" + preferred.name()))) return 1.0;
        if (key.startsWith("layer_3_bonus_organs_") && preferred.isOrgan()) return 1.0;
        if ((key.startsWith("layer_3_bonus_torso_")
                || key.startsWith("layer_3_bonus_right_arm_")
                || key.startsWith("layer_3_bonus_left_arm_")
                || key.startsWith("layer_3_bonus_right_leg_")
                || key.startsWith("layer_3_bonus_left_leg_")) && !preferred.isOrgan()) return 1.0;
        for (Limb limb : Limb.values()) {
            for (int cell = 0; cell < Limb.SLOT_COUNT; cell++) {
                String limbKey = limb.slotKey(cell);
                boolean matches = key.equals(limbKey)
                        || (key.startsWith("layer_") && key.endsWith("_" + limbKey));
                if (matches && !limb.isExtremity(cell) && limb.tissueAt(cell) == preferred) return 1.0;
            }
        }
        return ArtificialHeartItem.MISPLACED_EFFICIENCY;
    }

    private static double diminishingEfficiency(java.util.Map<ResourceLocation, java.util.List<Double>> installed,
                                                 ResourceLocation id) {
        java.util.List<Double> efficiencies = installed.get(id);
        if (efficiencies == null || efficiencies.isEmpty()) return 0.0;
        efficiencies.sort(java.util.Comparator.reverseOrder());
        double total = 0.0;
        double duplicateMultiplier = 1.0;
        for (double efficiency : efficiencies) {
            total += efficiency * duplicateMultiplier;
            duplicateMultiplier *= 0.5;
            if (duplicateMultiplier < 0.001) break;
        }
        return total;
    }

    private static void updateModifier(AttributeInstance attribute, ResourceLocation id, double amount) {
        if (attribute == null) return;
        AttributeModifier current = attribute.getModifier(id);
        if (amount > 0.0 && (current == null || Math.abs(current.amount() - amount) > 0.0001)) {
            if (current != null) attribute.removeModifier(id);
            attribute.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        } else if (amount <= 0.0 && current != null) {
            attribute.removeModifier(id);
        }
    }

    private AugmentationEvents() {}
}
