package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.registry.ModBlockEntities;
import com.wakame.humanaugmentation.registry.ModDataComponents;
import com.wakame.humanaugmentation.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class DnaAnalyzerBlockEntity extends BlockEntity {
    private static final int CAPACITY = 32_000;
    private static final int FE_PER_TICK = 40;
    private int progress;
    private final StoredEnergy energy = new StoredEnergy();
    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return stack.is(ModItems.DNA_SAMPLE.get());
            if (slot == 1) return stack.is(ModItems.DNA_EXTRACTION_REAGENT.get())
                    || stack.is(ModItems.DNA_AMPLIFICATION_MIX.get()) || stack.is(ModItems.STERILE_SYRINGE.get());
            return false;
        }
        @Override public int getSlotLimit(int slot) { return slot == 2 ? 1 : 16; }
        @Override protected void onContentsChanged(int slot) {
            setChanged();
            syncClient();
        }
    };

    public DnaAnalyzerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DNA_ANALYZER.get(), pos, state);
    }
    public EnergyStorage energy() { return energy; }
    public ItemStackHandler inventory() { return inventory; }
    public ItemStack displayStack(int slot) { return inventory.getStackInSlot(slot); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DnaAnalyzerBlockEntity analyzer) {
        analyzer.pullEnergy(level, pos);
        ItemStack sample = analyzer.inventory.getStackInSlot(0);
        if (sample.isEmpty() || sample.get(ModDataComponents.DNA_SOURCE) == null) {
            analyzer.progress = 0;
            return;
        }
        int stage = sample.getOrDefault(ModDataComponents.DNA_STAGE, 0);
        if (stage >= 3) {
            analyzer.fillInjection(sample);
            return;
        }
        ItemStack reagent = analyzer.inventory.getStackInSlot(1);
        if (stage == 0 && !reagent.is(ModItems.DNA_EXTRACTION_REAGENT.get())) return;
        if (stage == 1 && !reagent.is(ModItems.DNA_AMPLIFICATION_MIX.get())) return;
        int total = switch (stage) { case 0 -> 2_000; case 1 -> 4_000; default -> 8_000; };
        int use = Math.min(FE_PER_TICK, total - analyzer.progress);
        if (analyzer.energy.extractEnergy(use, true) != use) return;
        analyzer.energy.extractEnergy(use, false);
        analyzer.progress += use;
        if (analyzer.progress >= total) {
            if (stage <= 1) analyzer.inventory.extractItem(1, 1, false);
            sample.set(ModDataComponents.DNA_STAGE, stage + 1);
            analyzer.progress = 0;
            analyzer.setChanged();
            analyzer.syncClient();
        }
    }

    private void fillInjection(ItemStack sample) {
        ItemStack reagent = inventory.getStackInSlot(1);
        if (!reagent.is(ModItems.STERILE_SYRINGE.get()) || !inventory.getStackInSlot(2).isEmpty()) return;
        ItemStack injection = ModItems.GENE_INJECTION.toStack();
        injection.set(ModDataComponents.DNA_SOURCE, sample.get(ModDataComponents.DNA_SOURCE));
        inventory.extractItem(0, 1, false);
        inventory.extractItem(1, 1, false);
        inventory.setStackInSlot(2, injection);
        progress = 0;
        setChanged();
    }

    private void pullEnergy(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (energy.getEnergyStored() >= energy.getMaxEnergyStored()) break;
            var other = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if (other == null || !other.canExtract()) continue;
            int request = Math.min(1_000, energy.getMaxEnergyStored() - energy.getEnergyStored());
            int simulated = other.extractEnergy(request, true);
            int accepted = energy.receiveEnergy(simulated, false);
            if (accepted > 0) other.extractEnergy(accepted, false);
        }
    }

    public boolean insertOne(int slot, ItemStack held, Player player) {
        ItemStack one = held.copyWithCount(1);
        ItemStack remainder = inventory.insertItem(slot, one, false);
        if (!remainder.isEmpty()) return false;
        if (!player.getAbilities().instabuild) held.shrink(1);
        return true;
    }

    public ItemStack extractForPlayer() {
        for (int slot : new int[]{2, 0, 1}) {
            ItemStack result = inventory.extractItem(slot, inventory.getStackInSlot(slot).getCount(), false);
            if (!result.isEmpty()) return result;
        }
        return ItemStack.EMPTY;
    }

    public String stageName() {
        ItemStack sample = inventory.getStackInSlot(0);
        int stage = sample.getOrDefault(ModDataComponents.DNA_STAGE, 0);
        return switch (stage) { case 0 -> "RAW"; case 1 -> "EXTRACTED"; case 2 -> "AMPLIFIED"; default -> "ANALYZED"; };
    }

    public int progress() { return progress; }

    public int requiredProgress() {
        ItemStack sample = inventory.getStackInSlot(0);
        int stage = sample.getOrDefault(ModDataComponents.DNA_STAGE, 0);
        return switch (stage) { case 0 -> 2_000; case 1 -> 4_000; case 2 -> 8_000; default -> 0; };
    }

    public String waitingFor() {
        ItemStack sample = inventory.getStackInSlot(0);
        if (sample.isEmpty()) return "DNA_SAMPLE";
        if (sample.get(ModDataComponents.DNA_SOURCE) == null) return "VALID_DNA_SAMPLE";
        int stage = sample.getOrDefault(ModDataComponents.DNA_STAGE, 0);
        ItemStack reagent = inventory.getStackInSlot(1);
        if (stage == 0 && !reagent.is(ModItems.DNA_EXTRACTION_REAGENT.get())) return "EXTRACTION_REAGENT";
        if (stage == 1 && !reagent.is(ModItems.DNA_AMPLIFICATION_MIX.get())) return "AMPLIFICATION_MIX";
        if (stage >= 3 && !reagent.is(ModItems.STERILE_SYRINGE.get())) return "STERILE_SYRINGE";
        if (stage >= 3 && !inventory.getStackInSlot(2).isEmpty()) return "OUTPUT_RECOVERY";
        if (energy.getEnergyStored() < FE_PER_TICK) return "FE";
        return "PROCESSING";
    }

    private void syncClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet,
                             HolderLookup.Provider registries) {
        CompoundTag tag = packet.getTag();
        if (tag != null) loadAdditional(tag, registries);
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("energy", energy.getEnergyStored());
        tag.putInt("progress", progress);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        energy.setStored(tag.getInt("energy"));
        progress = Math.max(0, tag.getInt("progress"));
    }

    private final class StoredEnergy extends EnergyStorage {
        private StoredEnergy() { super(CAPACITY, 1_000, 1_000); }
        private void setStored(int amount) { energy = Math.max(0, Math.min(capacity, amount)); }
        @Override public int receiveEnergy(int maxReceive, boolean simulate) {
            int accepted = super.receiveEnergy(maxReceive, simulate);
            if (accepted > 0 && !simulate) setChanged();
            return accepted;
        }
        @Override public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (extracted > 0 && !simulate) setChanged();
            return extracted;
        }
    }
}
