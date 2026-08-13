package com.wakame.humanaugmentation.body;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

public final class BodyData implements INBTSerializable<CompoundTag> {
    private final Map<String, ResourceLocation> installed = new HashMap<>();
    private final Map<String, Integer> damage = new HashMap<>();
    private int metabolismLoad;
    private int stability = 100;

    public BodyData() {
        fillNaturalBodyParts();
        fillNaturalLimbs();
    }

    private void fillNaturalBodyParts() {
        for (BodySlot slot : BodySlot.values()) {
            installed.putIfAbsent(slot.name(), natural(slot));
        }
    }

    private void fillNaturalLimbs() {
        for (Limb limb : Limb.values()) {
            for (int i = 0; i < Limb.SLOT_COUNT; i++) {
                ResourceLocation part = limb.isExtremity(i)
                        ? ResourceLocation.fromNamespaceAndPath("humanaugmentation", limb.naturalExtremityPath())
                        : natural(limb.tissueAt(i));
                installed.putIfAbsent(limb.slotKey(i), part);
            }
        }
    }

    private static ResourceLocation natural(BodySlot slot) {
        return ResourceLocation.fromNamespaceAndPath("humanaugmentation", "natural_" + slot.name().toLowerCase(java.util.Locale.ROOT));
    }

    public Map<BodySlot, ResourceLocation> installed() {
        Map<BodySlot, ResourceLocation> result = new java.util.EnumMap<>(BodySlot.class);
        for (BodySlot slot : BodySlot.values()) {
            ResourceLocation id = installed.get(slot.name());
            if (id != null) result.put(slot, id);
        }
        return Map.copyOf(result);
    }

    public Map<String, ResourceLocation> allInstalled() { return Map.copyOf(installed); }
    public ResourceLocation get(String key) { return installed.get(key); }
    public int damage(String key) { return Math.max(0, damage.getOrDefault(key, 0)); }

    public void setDamage(String key, int value) {
        if (!installed.containsKey(key)) return;
        if (value <= 0) damage.remove(key);
        else damage.put(key, value);
    }

    public int damage(String key, int amount, int maximum) {
        if (amount <= 0 || maximum <= 0 || !installed.containsKey(key)) return damage(key);
        int next = Math.min(maximum, damage(key) + amount);
        setDamage(key, next);
        return next;
    }

    public boolean has(BodySlot slot, ResourceLocation augmentation) {
        return augmentation.equals(installed.get(slot.name()));
    }

    public boolean install(BodySlot slot, ResourceLocation augmentation, int capacityCost, int metabolismCost) {
        if (installed.containsKey(slot.name()) || capacityCost > slot.capacity()) {
            return false;
        }
        installed.put(slot.name(), augmentation);
        metabolismLoad += metabolismCost;
        return true;
    }

    public boolean uninstall(BodySlot slot, ResourceLocation augmentation, int metabolismCost) {
        if (!augmentation.equals(installed.get(slot.name()))) return false;
        installed.remove(slot.name());
        damage.remove(slot.name());
        metabolismLoad = Math.max(0, metabolismLoad - metabolismCost);
        return true;
    }

    public boolean install(String key, ResourceLocation augmentation) {
        return install(key, augmentation, 0);
    }

    public boolean install(String key, ResourceLocation augmentation, int metabolismCost) {
        if (installed.containsKey(key)) return false;
        installed.put(key, augmentation);
        metabolismLoad += metabolismCost;
        return true;
    }

    public boolean uninstall(String key, ResourceLocation augmentation) {
        return uninstall(key, augmentation, 0);
    }

    public boolean uninstall(String key, ResourceLocation augmentation, int metabolismCost) {
        if (!augmentation.equals(installed.get(key))) return false;
        installed.remove(key);
        damage.remove(key);
        metabolismLoad = Math.max(0, metabolismLoad - metabolismCost);
        return true;
    }

    public int installedCount() {
        return (int) installed.entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("dna_source_"))
                .filter(entry -> !entry.getValue().getPath().startsWith("natural_"))
                .count();
    }

    public int dnaCount() {
        return (int) installed.keySet().stream().filter(key -> key.startsWith("dna_source_")).count();
    }

    public int removeAllDna() {
        int before = installed.size();
        installed.keySet().removeIf(key -> key.startsWith("dna_source_"));
        damage.keySet().removeIf(key -> key.startsWith("dna_source_"));
        return before - installed.size();
    }

    public int metabolismLoad() {
        return metabolismLoad;
    }

    public int stability() {
        return stability;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag root = new CompoundTag();
        CompoundTag slots = new CompoundTag();
        installed.forEach((slot, id) -> slots.putString(slot, id.toString()));
        root.put("installed", slots);
        CompoundTag damageTag = new CompoundTag();
        damage.forEach(damageTag::putInt);
        root.put("augmentation_damage", damageTag);
        root.putInt("body_version", 6);
        root.putInt("metabolism_load", metabolismLoad);
        root.putInt("stability", stability);
        return root;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag root) {
        installed.clear();
        damage.clear();
        CompoundTag slots = root.getCompound("installed");
        for (String key : slots.getAllKeys()) installed.put(key, ResourceLocation.parse(slots.getString(key)));
        int version = root.contains("body_version") ? root.getInt("body_version") : 0;
        if (version == 0) fillNaturalBodyParts();
        if (version < 2) fillNaturalLimbs();
        if (version < 3) {
            installed.keySet().removeIf(key -> key.startsWith("limb_right_hand_") || key.startsWith("limb_left_hand_")
                    || key.endsWith("_14"));
            fillNaturalLimbs();
            for (Limb limb : Limb.values()) installed.put(limb.slotKey(9),
                    ResourceLocation.fromNamespaceAndPath("humanaugmentation", limb.isVertical() ? "natural_foot" : "natural_hand"));
        }
        if (version < 4) {
            for (Limb limb : Limb.values()) {
                String key = limb.slotKey(9);
                ResourceLocation current = installed.get(key);
                if (current != null && (current.getPath().equals("natural_hand") || current.getPath().equals("natural_foot"))) {
                    installed.put(key, ResourceLocation.fromNamespaceAndPath("humanaugmentation", limb.naturalExtremityPath()));
                }
            }
        }
        if (version < 5) installed.remove("DNA");
        CompoundTag damageTag = root.getCompound("augmentation_damage");
        for (String key : damageTag.getAllKeys()) {
            if (installed.containsKey(key)) damage.put(key, Math.max(0, damageTag.getInt(key)));
        }
        metabolismLoad = root.getInt("metabolism_load");
        stability = root.contains("stability") ? root.getInt("stability") : 100;
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(installed.size());
        installed.forEach((slot, id) -> {
            buffer.writeUtf(slot);
            buffer.writeResourceLocation(id);
            buffer.writeVarInt(damage(slot));
        });
        buffer.writeVarInt(metabolismLoad);
        buffer.writeVarInt(stability);
    }

    public static BodyData read(RegistryFriendlyByteBuf buffer) {
        BodyData data = new BodyData();
        data.installed.clear();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            String key = buffer.readUtf();
            data.installed.put(key, buffer.readResourceLocation());
            int damage = buffer.readVarInt();
            if (damage > 0) data.damage.put(key, damage);
        }
        data.metabolismLoad = buffer.readVarInt();
        data.stability = buffer.readVarInt();
        return data;
    }
}
