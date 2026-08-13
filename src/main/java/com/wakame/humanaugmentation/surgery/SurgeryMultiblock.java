package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class SurgeryMultiblock {
    public static int tier(Level level, BlockPos table) {
        Direction facing = facing(level, table);
        int result = componentTier(level, table, Component.TABLE);
        result = Math.min(result, componentTier(level, local(table, facing, 1, 0, 0), Component.ARM));
        result = Math.min(result, componentTier(level, local(table, facing, -1, 0, 0), Component.ARM));
        if (result <= 0 || powerInput(level, table) == null) return 0;

        // Floor around the patient bed. The south-center frame is the doorway threshold.
        for (int x = -1; x <= 1; x++) {
            for (int z : new int[]{-1, 1}) {
                if (x == 0 && z == -1) continue; // Power input.
                result = Math.min(result, componentTier(level, local(table, facing, x, 0, z), Component.FRAME));
            }
        }

        // Two-block-high chamber walls. South-center remains open as the entrance.
        for (int y = 1; y <= 2; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (Math.abs(x) != 1 && Math.abs(z) != 1) continue;
                    if (x == 0 && z == 1) {
                        if (!level.getBlockState(local(table, facing, x, y, z)).isAir()) return 0;
                        continue;
                    }
                    if (result >= 2 && x == 0 && y == 1 && z == -1) {
                        if (!level.getBlockState(local(table, facing, x, y, z)).is(ModBlocks.PRECISION_CONTROL_MODULE.get()))
                            return 0;
                        continue;
                    }
                    if (result >= 3 && x == 0 && y == 2 && z == -1) {
                        if (!level.getBlockState(local(table, facing, x, y, z)).is(ModBlocks.LIFE_SUPPORT_MODULE.get()))
                            return 0;
                        continue;
                    }
                    if (result >= 4 && x == -1 && y == 2 && z == -1) {
                        if (!level.getBlockState(local(table, facing, x, y, z)).is(ModBlocks.NEURAL_CONTROL_MODULE.get()))
                            return 0;
                        continue;
                    }
                    result = Math.min(result, componentTier(level, local(table, facing, x, y, z), Component.FRAME));
                }
            }
        }

        // Complete 3x3 roof.
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                result = Math.min(result, componentTier(level, local(table, facing, x, 3, z), Component.FRAME));
            }
        }
        return Math.max(0, result);
    }

    public static CombustionPowerInputBlockEntity powerInput(Level level, BlockPos table) {
        BlockPos inputPos = local(table, facing(level, table), 0, 0, -1);
        return level.getBlockEntity(inputPos) instanceof CombustionPowerInputBlockEntity input ? input : null;
    }

    public static int specialistCount(Level level, BlockPos table) {
        int tier = tier(level, table);
        if (tier < 2) return 0;
        Direction facing = facing(level, table);
        int count = level.getBlockState(local(table, facing, 0, 1, -1))
                .is(ModBlocks.PRECISION_CONTROL_MODULE.get()) ? 1 : 0;
        if (tier >= 3 && level.getBlockState(local(table, facing, 0, 2, -1))
                .is(ModBlocks.LIFE_SUPPORT_MODULE.get())) count++;
        if (tier >= 4 && level.getBlockState(local(table, facing, -1, 2, -1))
                .is(ModBlocks.NEURAL_CONTROL_MODULE.get())) count++;
        return count;
    }

    public static Direction facing(Level level, BlockPos table) {
        BlockState state = level.getBlockState(table);
        return state.hasProperty(SurgeryTableBlock.FACING) ? state.getValue(SurgeryTableBlock.FACING) : Direction.NORTH;
    }

    public static BlockPos local(BlockPos origin, Direction facing, int x, int y, int z) {
        return origin.relative(facing.getClockWise(), x).relative(facing.getOpposite(), z).above(y);
    }

    public static BlockPos doorway(BlockPos table, Direction facing) {
        return local(table, facing, 0, 0, 1);
    }

    private static int componentTier(Level level, BlockPos pos, Component component) {
        var state = level.getBlockState(pos);
        return switch (component) {
            case TABLE -> state.is(ModBlocks.SURGERY_TABLE_T4.get()) ? 4
                    : state.is(ModBlocks.SURGERY_TABLE_T3.get()) ? 3
                    : state.is(ModBlocks.SURGERY_TABLE_T2.get()) ? 2
                    : state.is(ModBlocks.SURGERY_TABLE.get()) ? 1 : 0;
            case ARM -> state.is(ModBlocks.SURGICAL_ARM_T4.get()) ? 4
                    : state.is(ModBlocks.SURGICAL_ARM_T3.get()) ? 3
                    : state.is(ModBlocks.SURGICAL_ARM_T2.get()) ? 2
                    : state.is(ModBlocks.SURGICAL_ARM.get()) ? 1 : 0;
            case FRAME -> state.is(ModBlocks.MEDICAL_FRAME_T4.get()) ? 4
                    : state.is(ModBlocks.MEDICAL_FRAME_T3.get()) ? 3
                    : state.is(ModBlocks.MEDICAL_FRAME_T2.get()) ? 2
                    : state.is(ModBlocks.MEDICAL_FRAME.get()) ? 1 : 0;
        };
    }

    private enum Component { TABLE, ARM, FRAME }

    private SurgeryMultiblock() {}
}
