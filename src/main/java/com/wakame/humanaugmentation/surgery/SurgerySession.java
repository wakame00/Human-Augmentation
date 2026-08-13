package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.registry.ModBlocks;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HumanAugmentation.MOD_ID)
public final class SurgerySession {
    private static final Map<UUID, BlockPos> PATIENTS = new HashMap<>();

    private SurgerySession() {}

    public static boolean enter(ServerPlayer player, BlockPos tablePos) {
        if (PATIENTS.entrySet().stream().anyMatch(entry -> entry.getValue().equals(tablePos) && !entry.getKey().equals(player.getUUID()))) {
            return false;
        }
        PATIENTS.put(player.getUUID(), tablePos.immutable());
        positionPatient(player, tablePos);
        return true;
    }

    public static boolean isPatient(ServerPlayer player) {
        return PATIENTS.containsKey(player.getUUID());
    }

    public static void exit(ServerPlayer player) {
        BlockPos tablePos = PATIENTS.remove(player.getUUID());
        if (tablePos == null) return;
        var facing = SurgeryMultiblock.facing(player.level(), tablePos);
        BlockPos doorway = SurgeryMultiblock.doorway(tablePos, facing);
        player.setForcedPose(null);
        player.setNoGravity(false);
        player.setDeltaMovement(0.0, 0.0, 0.0);
        player.teleportTo(doorway.getX() + 0.5, doorway.getY() + 1.0, doorway.getZ() + 0.5);
    }

    public static void ejectAt(BlockPos tablePos) {
        PATIENTS.entrySet().removeIf(entry -> {
            if (!entry.getValue().equals(tablePos)) return false;
            return true;
        });
    }

    private static void positionPatient(ServerPlayer player, BlockPos tablePos) {
        var facing = SurgeryMultiblock.facing(player.level(), tablePos);
        player.teleportTo(tablePos.getX() + 0.5, tablePos.getY() + 1.05, tablePos.getZ() + 0.5);
        player.setYRot(facing.toYRot());
        player.setXRot(0.0F);
        player.setForcedPose(Pose.SWIMMING);
        player.setNoGravity(true);
        player.setDeltaMovement(0.0, 0.0, 0.0);
        player.fallDistance = 0.0F;
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BlockPos tablePos = PATIENTS.get(player.getUUID());
        if (tablePos == null) return;
        var tableState = player.level().getBlockState(tablePos);
        if (!tableState.is(ModBlocks.SURGERY_TABLE.get())
                && !tableState.is(ModBlocks.SURGERY_TABLE_T2.get())
                && !tableState.is(ModBlocks.SURGERY_TABLE_T3.get())
                && !tableState.is(ModBlocks.SURGERY_TABLE_T4.get())) {
            player.closeContainer();
            exit(player);
            return;
        }
        positionPatient(player, tablePos);
    }

    @SubscribeEvent
    public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) exit(player);
    }
}
