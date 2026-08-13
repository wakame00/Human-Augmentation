package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.augmentation.ArtificialHeartItem;
import com.wakame.humanaugmentation.augmentation.AugmentationItem;
import com.wakame.humanaugmentation.body.BiologicalBodyPartItem;
import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.body.BodyRegion;
import com.wakame.humanaugmentation.body.Limb;
import com.wakame.humanaugmentation.registry.ModAttachments;
import com.wakame.humanaugmentation.registry.ModItems;
import com.wakame.humanaugmentation.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class SurgeryMenu extends AbstractContainerMenu {
    public static final BlockPos CREATIVE_TERMINAL_POS = new BlockPos(0, -2048, 0);
    public static final BlockPos LIMB_PREVIEW_TERMINAL_POS = new BlockPos(1, -2048, 0);
    public static final BlockPos COMBINED_LEG_PREVIEW_TERMINAL_POS = new BlockPos(2, -2048, 0);
    private static final int PRIMARY_SLOT_COUNT = BodySlot.values().length;
    private static final int BODY_LAYER_COUNT = 3;
    private static final int LAYERED_PRIMARY_SLOT_COUNT = PRIMARY_SLOT_COUNT * BODY_LAYER_COUNT;
    private static final int LIMB_LAYER_SIZE = Limb.values().length * Limb.SLOT_COUNT;
    private static final int BASE_BODY_SLOT_COUNT = LAYERED_PRIMARY_SLOT_COUNT + LIMB_LAYER_SIZE * BODY_LAYER_COUNT;
    private static final int THIRD_LAYER_BONUS_PER_REGION = 2;
    private static final int THIRD_LAYER_BONUS_COUNT = BodyRegion.values().length * THIRD_LAYER_BONUS_PER_REGION;
    private static final int BODY_SLOT_COUNT = BASE_BODY_SLOT_COUNT + THIRD_LAYER_BONUS_COUNT;
    private final ContainerData data;
    private final SimpleContainer bodySlots;
    private final Player player;
    private final BlockPos tablePos;
    private BodyRegion selectedRegion = BodyRegion.ORGANS;
    private int selectedLayer;
    private boolean detailOpen = true;

    public SurgeryMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, createClientBody(), new SimpleContainerData(7), inventory.player, buffer.readBlockPos());
    }

    public SurgeryMenu(int id, Inventory inventory, Player player) {
        this(id, inventory, player, BlockPos.ZERO);
    }

    public SurgeryMenu(int id, Inventory inventory, Player player, BlockPos tablePos) {
        this(id, inventory, createServerBody(player), new ContainerData() {
            @Override
            public int get(int index) {
                var body = player.getData(ModAttachments.BODY);
                return switch (index) {
                    case 0 -> body.installedCount();
                    case 1 -> body.metabolismLoad();
                    case 2 -> body.stability();
                    case 3 -> isCreativeTerminal(tablePos) ? 5 : SurgeryMultiblock.tier(player.level(), tablePos);
                    case 4 -> {
                        if (isCreativeTerminal(tablePos)) yield Integer.MAX_VALUE;
                        var input = SurgeryMultiblock.powerInput(player.level(), tablePos);
                        yield input == null ? 0 : input.storedSp();
                    }
                    case 5 -> {
                        if (isCreativeTerminal(tablePos)) yield Integer.MAX_VALUE;
                        var input = SurgeryMultiblock.powerInput(player.level(), tablePos);
                        yield input == null ? 0 : input.capacity();
                    }
                    case 6 -> isCreativeTerminal(tablePos) ? 4
                            : SurgeryMultiblock.specialistCount(player.level(), tablePos);
                    default -> 0;
                };
            }

            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 7; }
        }, player, tablePos);
    }

    private SurgeryMenu(int id, Inventory inventory, SimpleContainer bodySlots, ContainerData data, Player player,
                        BlockPos tablePos) {
        super(ModMenus.SURGERY.get(), id);
        this.data = data;
        this.bodySlots = bodySlots;
        this.player = player;
        this.tablePos = tablePos;
        if (isCombinedLegPreviewLayout()) selectedRegion = BodyRegion.RIGHT_LEG;
        for (int layer = 0; layer < BODY_LAYER_COUNT; layer++) {
            int layerIndex = layer;
            for (BodySlot bodySlot : BodySlot.values()) {
                int index = primaryContainerIndex(layer, bodySlot);
                addSlot(new Slot(bodySlots, index, SurgeryLayout.primaryX(bodySlot), SurgeryLayout.primaryY(bodySlot)) {
                @Override public boolean isActive() {
                    return detailOpen && selectedLayer == layerIndex && selectedRegion.contains(bodySlot)
                            && SurgeryTierRules.unlocked(surgeryTier(), bodySlot)
                            && surgeryTier() >= requiredLayerTier(layerIndex);
                }
                @Override public boolean mayPlace(ItemStack stack) {
                    if (!isActive()) return false;
                    if (stack.getItem() instanceof AugmentationItem augmentation)
                        return augmentation.tier() <= surgeryTier();
                    if (isExtremityItem(stack)) return false;
                    return stack.getItem() instanceof BiologicalBodyPartItem part && part.bodySlot() == bodySlot;
                }
                @Override public int getMaxStackSize() { return 1; }
                @Override public void setChanged() { super.setChanged(); syncBodySlot(layerIndex, bodySlot); }
                @Override public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    syncBodySlot(layerIndex, bodySlot);
                }
                });
            }
        }
        for (BodyRegion region : BodyRegion.values()) {
            for (int bonus = 0; bonus < THIRD_LAYER_BONUS_PER_REGION; bonus++) {
                int index = thirdLayerBonusContainerIndex(region, bonus);
                String key = thirdLayerBonusKey(region, bonus);
                addSlot(new Slot(bodySlots, index, SurgeryLayout.thirdLayerBonusX(region, bonus),
                        SurgeryLayout.thirdLayerBonusY(region, bonus)) {
                    @Override public boolean isActive() {
                        return detailOpen && selectedLayer == 2 && surgeryTier() >= 5 && selectedRegion == region;
                    }
                    @Override public boolean mayPlace(ItemStack stack) {
                        return isActive() && stack.getItem() instanceof AugmentationItem augmentation
                                && augmentation.tier() <= surgeryTier();
                    }
                    @Override public int getMaxStackSize() { return 1; }
                    @Override public void setChanged() {
                        super.setChanged();
                        syncBonusSlot(index, key);
                    }
                    @Override public void onTake(Player player, ItemStack stack) {
                        super.onTake(player, stack);
                        syncBonusSlot(index, key);
                    }
                });
            }
        }
        for (int layer = 0; layer < BODY_LAYER_COUNT; layer++) {
            int layerIndex = layer;
            for (Limb limb : Limb.values()) {
                BodyRegion region = regionFor(limb);
                for (int cell = 0; cell < Limb.SLOT_COUNT; cell++) {
                    int cellIndex = cell;
                    int index = limbContainerIndex(layer, limb, cell);
                    BodySlot tissue = limb.tissueAt(cell);
                    String key = layeredLimbSlotKey(layer, limb, cell);
                    boolean extremity = limb.isExtremity(cell);
                    addSlot(new Slot(bodySlots, index, limbX(limb, cell),
                            limbY(limb, cell)) {
                        @Override public boolean isActive() {
                            return detailOpen && selectedLayer == layerIndex
                                    && (selectedRegion == region || combinedLimbRegion(region))
                                    && SurgeryTierRules.unlocked(surgeryTier(), limb, cellIndex)
                                    && surgeryTier() >= requiredLayerTier(layerIndex);
                        }
                        @Override public boolean mayPlace(ItemStack stack) {
                            if (!isActive()) return false;
                            if (stack.getItem() instanceof AugmentationItem augmentation)
                                return augmentation.tier() <= surgeryTier();
                            if (extremity) return isCorrectExtremity(stack, limb);
                            if (isExtremityItem(stack)) return false;
                            return stack.getItem() instanceof BiologicalBodyPartItem part && part.bodySlot() == tissue;
                        }
                        @Override public int getMaxStackSize() { return 1; }
                        @Override public void setChanged() { super.setChanged(); syncLimbSlot(layerIndex, index, key); }
                        @Override public void onTake(Player player, ItemStack stack) {
                            super.onTake(player, stack);
                            syncLimbSlot(layerIndex, index, key);
                        }
                    });
                }
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9,
                        SurgeryLayout.INVENTORY_X + column * 18, SurgeryLayout.INVENTORY_Y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column,
                SurgeryLayout.INVENTORY_X + column * 18, SurgeryLayout.HOTBAR_Y));
        addDataSlots(data);
    }

    private static int indexInRegion(BodySlot target) {
        BodyRegion region = regionOf(target);
        int index = 0;
        for (BodySlot slot : BodySlot.values()) {
            if (region.contains(slot)) {
                if (slot == target) return index;
                index++;
            }
        }
        return 0;
    }

    private static BodyRegion regionOf(BodySlot slot) {
        for (BodyRegion region : BodyRegion.values()) if (region.contains(slot)) return region;
        return BodyRegion.TORSO;
    }

    private static BodyRegion regionFor(Limb limb) { return BodyRegion.valueOf(limb.name()); }

    private static boolean isCorrectExtremity(ItemStack stack, Limb limb) {
        return switch (limb) {
            case RIGHT_ARM -> stack.is(ModItems.NATURAL_RIGHT_HAND.get());
            case LEFT_ARM -> stack.is(ModItems.NATURAL_LEFT_HAND.get());
            case RIGHT_LEG -> stack.is(ModItems.NATURAL_RIGHT_FOOT.get());
            case LEFT_LEG -> stack.is(ModItems.NATURAL_LEFT_FOOT.get());
        };
    }

    private static boolean isExtremityItem(ItemStack stack) {
        return stack.is(ModItems.NATURAL_HAND.get()) || stack.is(ModItems.NATURAL_FOOT.get())
                || stack.is(ModItems.NATURAL_RIGHT_HAND.get()) || stack.is(ModItems.NATURAL_LEFT_HAND.get())
                || stack.is(ModItems.NATURAL_RIGHT_FOOT.get()) || stack.is(ModItems.NATURAL_LEFT_FOOT.get());
    }

    private static int limbContainerIndex(int layer, Limb limb, int cell) {
        return LAYERED_PRIMARY_SLOT_COUNT + layer * LIMB_LAYER_SIZE
                + limb.ordinal() * Limb.SLOT_COUNT + cell;
    }

    private static int primaryContainerIndex(int layer, BodySlot slot) {
        return layer * PRIMARY_SLOT_COUNT + slot.ordinal();
    }

    public static String layeredSlotKey(int layer, BodySlot slot) {
        return layer == 0 ? slot.name() : "layer_" + (layer + 1) + "_" + slot.name();
    }

    public static String layeredLimbSlotKey(int layer, Limb limb, int cell) {
        String base = limb.slotKey(cell);
        return layer == 0 ? base : "layer_" + (layer + 1) + "_" + base;
    }

    private static int thirdLayerBonusContainerIndex(BodyRegion region, int bonus) {
        return BASE_BODY_SLOT_COUNT + region.ordinal() * THIRD_LAYER_BONUS_PER_REGION + bonus;
    }

    public static String thirdLayerBonusKey(BodyRegion region, int bonus) {
        return "layer_3_bonus_" + region.name().toLowerCase(java.util.Locale.ROOT) + "_" + bonus;
    }

    private static int requiredLayerTier(int layer) {
        return switch (layer) {
            case 0 -> 1;
            case 1 -> 4;
            default -> 5;
        };
    }

    private static SimpleContainer createClientBody() { return new SimpleContainer(BODY_SLOT_COUNT); }

    private static SimpleContainer createServerBody(Player player) {
        SimpleContainer container = new SimpleContainer(BODY_SLOT_COUNT);
        for (int layer = 0; layer < BODY_LAYER_COUNT; layer++) {
            int layerIndex = layer;
            for (BodySlot slot : BodySlot.values()) {
                var id = player.getData(ModAttachments.BODY).get(layeredSlotKey(layer, slot));
                String key = layeredSlotKey(layer, slot);
                if (id != null) BuiltInRegistries.ITEM.getOptional(id).ifPresent(item ->
                        container.setItem(primaryContainerIndex(layerIndex, slot), bodyStack(player, key, item)));
            }
        }
        for (int layer = 0; layer < BODY_LAYER_COUNT; layer++) {
            int layerIndex = layer;
            for (Limb limb : Limb.values()) {
                for (int cell = 0; cell < Limb.SLOT_COUNT; cell++) {
                    var id = player.getData(ModAttachments.BODY).get(layeredLimbSlotKey(layer, limb, cell));
                    int containerIndex = limbContainerIndex(layerIndex, limb, cell);
                    String key = layeredLimbSlotKey(layer, limb, cell);
                    if (id != null) BuiltInRegistries.ITEM.getOptional(id).ifPresent(item ->
                            container.setItem(containerIndex, bodyStack(player, key, item)));
                }
            }
        }
        for (BodyRegion region : BodyRegion.values()) {
            for (int bonus = 0; bonus < THIRD_LAYER_BONUS_PER_REGION; bonus++) {
                String key = thirdLayerBonusKey(region, bonus);
                var id = player.getData(ModAttachments.BODY).get(key);
                int index = thirdLayerBonusContainerIndex(region, bonus);
                if (id != null) BuiltInRegistries.ITEM.getOptional(id).ifPresent(item ->
                        container.setItem(index, bodyStack(player, key, item)));
            }
        }
        return container;
    }

    private void syncLimbSlot(int layer, int containerIndex, String key) {
        if (player.level().isClientSide()) return;
        var body = player.getData(ModAttachments.BODY);
        var previous = body.get(key);
        ItemStack stack = bodySlots.getItem(containerIndex);
        var next = stack.isEmpty() ? null : BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (previous != null && !previous.equals(next)) {
            int metabolism = augmentationMetabolism(previous);
            body.uninstall(key, previous, layeredMetabolism(metabolism, layer));
        }
        if (next != null && !next.equals(previous)) {
            int metabolism = augmentationMetabolism(next);
            body.install(key, next, layeredMetabolism(metabolism, layer));
        }
        if (next != null) body.setDamage(key, stack.getDamageValue());
        player.syncData(ModAttachments.BODY);
    }

    private void syncBodySlot(int layer, BodySlot slot) {
        if (player.level().isClientSide()) return;
        var body = player.getData(ModAttachments.BODY);
        String key = layeredSlotKey(layer, slot);
        var previous = body.get(key);
        ItemStack stack = bodySlots.getItem(primaryContainerIndex(layer, slot));
        var next = stack.isEmpty() ? null : BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (previous != null && !previous.equals(next)) {
            int cost = augmentationMetabolism(previous);
            if (layer == 0) body.uninstall(slot, previous, cost);
            else body.uninstall(key, previous, layeredMetabolism(cost, layer));
        }
        if (next != null && !next.equals(previous)) {
            int capacity = augmentationCapacity(next);
            int metabolism = augmentationMetabolism(next);
            if (layer == 0) body.install(slot, next, capacity, metabolism);
            else body.install(key, next, layeredMetabolism(metabolism, layer));
        }
        if (next != null) body.setDamage(key, stack.getDamageValue());
        player.syncData(ModAttachments.BODY);
    }

    private void syncBonusSlot(int containerIndex, String key) {
        if (player.level().isClientSide()) return;
        var body = player.getData(ModAttachments.BODY);
        var previous = body.get(key);
        ItemStack stack = bodySlots.getItem(containerIndex);
        var next = stack.isEmpty() ? null : BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (previous != null && !previous.equals(next)) {
            body.uninstall(key, previous, layeredMetabolism(augmentationMetabolism(previous), 2));
        }
        if (next != null && !next.equals(previous)) {
            body.install(key, next, layeredMetabolism(augmentationMetabolism(next), 2));
        }
        if (next != null) body.setDamage(key, stack.getDamageValue());
        player.syncData(ModAttachments.BODY);
    }

    private static int layeredMetabolism(int base, int layer) {
        return switch (layer) {
            case 1 -> (int)Math.ceil(base * 1.25);
            case 2 -> (int)Math.ceil(base * 1.75);
            default -> base;
        };
    }

    public int installedCount() { return data.get(0); }
    public int metabolismLoad() { return data.get(1); }
    public int stability() { return data.get(2); }
    public int surgeryTier() { return data.get(3); }
    public int storedSp() { return data.get(4); }
    public int spCapacity() { return data.get(5); }
    public int specialistCount() { return data.get(6); }
    public boolean isUnlocked(BodySlot slot) { return SurgeryTierRules.unlocked(surgeryTier(), slot); }
    public boolean isUnlocked(Limb limb, int cell) { return SurgeryTierRules.unlocked(surgeryTier(), limb, cell); }
    public int selectedPrimaryIndex(BodySlot slot) { return primaryContainerIndex(selectedLayer, slot); }
    public int selectedLimbIndex(Limb limb, int cell) {
        return limbContainerIndex(selectedLayer, limb, cell);
    }
    public int thirdLayerBonusIndex(BodyRegion region, int bonus) {
        return thirdLayerBonusContainerIndex(region, bonus);
    }
    public boolean isLimbPreviewLayout() { return LIMB_PREVIEW_TERMINAL_POS.equals(tablePos); }
    public boolean isCombinedLegPreviewLayout() { return COMBINED_LEG_PREVIEW_TERMINAL_POS.equals(tablePos); }
    public boolean usesCombinedLimbLayout() { return !isLimbPreviewLayout(); }
    public int limbX(Limb limb, int cell) {
        if (usesCombinedLimbLayout() && limb.isVertical()) return SurgeryLayout.combinedLegX(limb, cell);
        if (usesCombinedLimbLayout()) return SurgeryLayout.combinedArmX(limb, cell);
        return isLimbPreviewLayout() ? SurgeryLayout.previewLimbX(limb, cell) : SurgeryLayout.limbX(limb, cell);
    }
    public int limbY(Limb limb, int cell) {
        if (usesCombinedLimbLayout() && limb.isVertical()) return SurgeryLayout.combinedLegY(limb, cell);
        if (usesCombinedLimbLayout()) return SurgeryLayout.combinedArmY(limb, cell);
        return isLimbPreviewLayout() ? SurgeryLayout.previewLimbY(limb, cell) : SurgeryLayout.limbY(limb, cell);
    }
    private boolean combinedLimbRegion(BodyRegion region) {
        Limb selected = selectedRegion.limb();
        Limb candidate = region.limb();
        return usesCombinedLimbLayout() && selected != null && candidate != null
                && selected.isVertical() == candidate.isVertical();
    }
    public int previewCost(Slot target, ItemStack carried) {
        if (!(carried.getItem() instanceof AugmentationItem augmentation)) return 0;
        int index = slots.indexOf(target);
        if (index < 0 || index >= BODY_SLOT_COUNT) return 0;
        if (index < LAYERED_PRIMARY_SLOT_COUNT) {
            int layer = index / PRIMARY_SLOT_COUNT;
            BodySlot slot = BodySlot.values()[index % PRIMARY_SLOT_COUNT];
            int cost = SurgeryPowerCost.installation(augmentation, slot);
            cost = switch (layer) {
                case 1 -> (int)Math.ceil(cost * 1.5 / 10.0) * 10;
                case 2 -> (int)Math.ceil(cost * 2.5 / 10.0) * 10;
                default -> cost;
            };
            return applySpecialistCost(cost);
        }
        if (index >= BASE_BODY_SLOT_COUNT) {
            int cost = SurgeryPowerCost.installation(augmentation, augmentation.preferredSlot());
            cost = (int)Math.ceil(cost * 2.5 / 10.0) * 10;
            return applySpecialistCost(cost);
        }
        int relative = index - LAYERED_PRIMARY_SLOT_COUNT;
        int layer = relative / LIMB_LAYER_SIZE;
        int withinLayer = relative % LIMB_LAYER_SIZE;
        Limb limb = Limb.values()[withinLayer / Limb.SLOT_COUNT];
        int cell = withinLayer % Limb.SLOT_COUNT;
        int cost = SurgeryPowerCost.installation(augmentation, limb, cell);
        cost = switch (layer) {
            case 1 -> (int)Math.ceil(cost * 1.5 / 10.0) * 10;
            case 2 -> (int)Math.ceil(cost * 2.5 / 10.0) * 10;
            default -> cost;
        };
        return applySpecialistCost(cost);
    }

    private int applySpecialistCost(int cost) {
        int specialists = specialistCount();
        if (specialists <= 0) return cost;
        double multiplier = 0.85 * Math.pow(0.90, specialists - 1);
        return Math.max(1, (int)Math.ceil(cost * multiplier / 10.0) * 10);
    }
    public BodyRegion selectedRegion() { return selectedRegion; }
    public int selectedLayer() { return selectedLayer; }
    public int availableLayers() { return surgeryTier() >= 5 ? 3 : surgeryTier() >= 4 ? 2 : 1; }
    public boolean detailOpen() { return detailOpen; }

    public void selectRegion(int id) {
        if (id >= 0 && id < BodyRegion.values().length) {
            selectedRegion = BodyRegion.values()[id];
            detailOpen = true;
        } else if (id == 100) {
            detailOpen = false;
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 200 && id < 200 + BODY_LAYER_COUNT) {
            int layer = id - 200;
            if (layer >= availableLayers()) return false;
            selectedLayer = layer;
            return true;
        }
        if (id != 100 && (id < 0 || id >= BodyRegion.values().length)) return false;
        selectRegion(id);
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int bodySlotCount = BODY_SLOT_COUNT;
        if (index < bodySlotCount) {
            if (!moveItemStackTo(stack, bodySlotCount, slots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof AugmentationItem || stack.getItem() instanceof BiologicalBodyPartItem) {
            boolean moved = false;
            for (int target = 0; target < BODY_SLOT_COUNT; target++) {
                Slot bodySlot = slots.get(target);
                if (bodySlot.isActive() && bodySlot.mayPlace(stack) && !bodySlot.hasItem()
                        && moveItemStackTo(stack, target, target + 1, false)) {
                    moved = true;
                    break;
                }
            }
            if (!moved) return ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override public boolean stillValid(Player player) { return true; }

    private static int augmentationMetabolism(net.minecraft.resources.ResourceLocation id) {
        return BuiltInRegistries.ITEM.getOptional(id)
                .filter(item -> item instanceof AugmentationItem)
                .map(item -> ((AugmentationItem)item).metabolismCost())
                .orElse(0);
    }

    private static int augmentationCapacity(net.minecraft.resources.ResourceLocation id) {
        return BuiltInRegistries.ITEM.getOptional(id)
                .filter(item -> item instanceof AugmentationItem)
                .map(item -> ((AugmentationItem)item).capacityCost())
                .orElse(0);
    }

    private static ItemStack bodyStack(Player player, String key, net.minecraft.world.item.Item item) {
        ItemStack stack = new ItemStack(item);
        if (stack.isDamageableItem()) {
            stack.setDamageValue(Math.min(stack.getMaxDamage(), player.getData(ModAttachments.BODY).damage(key)));
        }
        return stack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) SurgerySession.exit(serverPlayer);
    }

    private static boolean isCreativeTerminal(BlockPos pos) {
        return CREATIVE_TERMINAL_POS.equals(pos) || LIMB_PREVIEW_TERMINAL_POS.equals(pos)
                || COMBINED_LEG_PREVIEW_TERMINAL_POS.equals(pos);
    }
}
