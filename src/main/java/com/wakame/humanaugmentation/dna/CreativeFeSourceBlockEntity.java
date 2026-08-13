package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class CreativeFeSourceBlockEntity extends BlockEntity {
    private static final IEnergyStorage ENERGY = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return Math.max(0, maxExtract); }
        @Override public int getEnergyStored() { return Integer.MAX_VALUE; }
        @Override public int getMaxEnergyStored() { return Integer.MAX_VALUE; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    };

    public CreativeFeSourceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_FE_SOURCE.get(), pos, state);
    }
    public IEnergyStorage energy() { return ENERGY; }
}
