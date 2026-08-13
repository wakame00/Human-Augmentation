package com.wakame.humanaugmentation.augmentation;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.registry.ModAttachments;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = HumanAugmentation.MOD_ID)
public final class AugmentationDurabilityEvents {
    @SubscribeEvent
    public static void afterDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getNewDamage() <= 0.0F) return;
        int wear = Math.max(1, (int)Math.ceil(event.getNewDamage() * 2.0F));
        damageRandomInstalledPart(player, wear);
    }

    @SubscribeEvent
    public static void attack(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isCanceled()) return;
        damageArmPart(player, 1);
    }

    private static void damageArmPart(ServerPlayer player, int wear) {
        var body = player.getData(ModAttachments.BODY);
        String main = player.getMainArm().getSerializedName();
        List<String> candidates = artificialKeys(player, "limb_" + main + "_arm_");
        if (candidates.isEmpty()) candidates = artificialKeys(player, null);
        damageOne(player, candidates, wear);
    }

    private static void damageRandomInstalledPart(ServerPlayer player, int wear) {
        damageOne(player, artificialKeys(player, null), wear);
    }

    private static List<String> artificialKeys(ServerPlayer player, String keyFragment) {
        List<String> candidates = new ArrayList<>();
        for (var entry : player.getData(ModAttachments.BODY).allInstalled().entrySet()) {
            if (keyFragment != null && !entry.getKey().contains(keyFragment)) continue;
            var item = BuiltInRegistries.ITEM.getOptional(entry.getValue()).orElse(null);
            if (item instanceof AugmentationItem) candidates.add(entry.getKey());
        }
        return candidates;
    }

    private static void damageOne(ServerPlayer player, List<String> candidates, int wear) {
        if (candidates.isEmpty()) return;
        String key = candidates.get(player.getRandom().nextInt(candidates.size()));
        var body = player.getData(ModAttachments.BODY);
        var id = body.get(key);
        var item = id == null ? null : BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        if (!(item instanceof AugmentationItem augmentation)) return;
        body.damage(key, wear, augmentation.maximumDurability());
        player.syncData(ModAttachments.BODY);
    }

    private AugmentationDurabilityEvents() {}
}
