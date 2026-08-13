package com.wakame.humanaugmentation.dna;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wakame.humanaugmentation.HumanAugmentation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public final class DnaRegistry extends SimpleJsonResourceReloadListener {
    public static final DnaRegistry INSTANCE = new DnaRegistry();
    private volatile Map<ResourceLocation, DnaDefinition> definitions = Map.of();
    private DnaRegistry() { super(new Gson(), "dna_sources"); }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, DnaDefinition> loaded = new HashMap<>();
        entries.forEach((id, json) -> {
            try {
                JsonObject object = GsonHelper.convertToJsonObject(json, "DNA definition");
                ResourceLocation entity = ResourceLocation.parse(GsonHelper.getAsString(object, "entity"));
                double chance = GsonHelper.getAsDouble(object, "sample_chance", 1.0D);
                ArrayList<ResourceLocation> genes = new ArrayList<>();
                GsonHelper.getAsJsonArray(object, "genes").forEach(gene -> genes.add(ResourceLocation.parse(gene.getAsString())));
                loaded.put(entity, new DnaDefinition(entity, chance, genes));
            } catch (RuntimeException exception) {
                HumanAugmentation.LOGGER.error("Invalid DNA definition {}", id, exception);
            }
        });
        definitions = Map.copyOf(loaded);
        HumanAugmentation.LOGGER.info("Loaded {} DNA source definitions", definitions.size());
    }

    public DnaDefinition get(ResourceLocation entityId) { return definitions.get(entityId); }
}
