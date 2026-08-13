package com.wakame.humanaugmentation.compat;

import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class OptionalRegistryLookup {
    public static Optional<Item> item(String id) {
        return parse(id).flatMap(BuiltInRegistries.ITEM::getOptional);
    }

    public static Optional<Block> block(String id) {
        return parse(id).flatMap(BuiltInRegistries.BLOCK::getOptional);
    }

    public static Optional<EntityType<?>> entityType(String id) {
        return parse(id).flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional);
    }

    public static boolean itemExists(String id) { return item(id).isPresent(); }
    public static boolean blockExists(String id) { return block(id).isPresent(); }
    public static boolean entityTypeExists(String id) { return entityType(id).isPresent(); }

    private static Optional<ResourceLocation> parse(String id) {
        return Optional.ofNullable(ResourceLocation.tryParse(id));
    }

    private OptionalRegistryLookup() {}
}
