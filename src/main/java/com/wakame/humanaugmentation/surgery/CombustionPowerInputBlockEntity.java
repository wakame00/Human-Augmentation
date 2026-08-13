package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class CombustionPowerInputBlockEntity extends BlockEntity {
    public static final int TIER = 1;
    public static final int CAPACITY = 1_000;
    private int storedSp;
    private int burnRemaining;
    private int burnDuration;
    private int generationProgress;
    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && stack.getBurnTime(null) > 0;
        }
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    public CombustionPowerInputBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMBUSTION_POWER_INPUT.get(), pos, state);
    }

    public int storedSp() { return storedSp; }
    public int capacity() { return CAPACITY; }
    public int burnRemaining() { return burnRemaining; }
    public int burnDuration() { return burnDuration; }
    public ItemStackHandler inventory() { return inventory; }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  CombustionPowerInputBlockEntity input) {
        if (input.burnRemaining <= 0 && input.storedSp < input.capacity()) {
            ItemStack fuel = input.inventory.getStackInSlot(0);
            int duration = fuel.getBurnTime(null);
            if (duration > 0) {
                ItemStack container = fuel.getCraftingRemainingItem();
                input.inventory.extractItem(0, 1, false);
                if (!container.isEmpty()) input.inventory.insertItem(1, container, false);
                input.burnRemaining = duration;
                input.burnDuration = duration;
                input.setChanged();
            }
        }
        if (input.burnRemaining > 0 && input.storedSp < input.capacity()) {
            input.burnRemaining--;
            if (++input.generationProgress >= 2) {
                input.generationProgress = 0;
                input.addSp(1);
            } else {
                input.setChanged();
            }
        }
    }

    public int addSp(int amount) {
        int accepted = Math.min(Math.max(0, amount), CAPACITY - storedSp);
        if (accepted > 0) {
            storedSp += accepted;
            setChanged();
        }
        return accepted;
    }

    public boolean consumeSp(int amount) {
        if (amount < 0 || storedSp < amount) return false;
        storedSp -= amount;
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("stored_sp", storedSp);
        tag.putInt("burn_remaining", burnRemaining);
        tag.putInt("burn_duration", burnDuration);
        tag.putInt("generation_progress", generationProgress);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedSp = Math.max(0, Math.min(CAPACITY, tag.getInt("stored_sp")));
        burnRemaining = Math.max(0, tag.getInt("burn_remaining"));
        burnDuration = Math.max(0, tag.getInt("burn_duration"));
        generationProgress = Math.max(0, tag.getInt("generation_progress"));
        if (tag.contains("inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }
}
