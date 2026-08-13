package com.wakame.humanaugmentation.dna;

import com.wakame.humanaugmentation.body.BodyData;
import net.minecraft.resources.ResourceLocation;

public final class DnaProfile {
    public static String sourceKey(ResourceLocation source) {
        return "dna_source_" + source.getNamespace() + "_" + source.getPath().replace('/', '_');
    }

    public static boolean hasSource(BodyData body, ResourceLocation source) {
        return source.equals(body.get(sourceKey(source)));
    }

    public static boolean hasGene(BodyData body, ResourceLocation gene) {
        for (var entry : body.allInstalled().entrySet()) {
            if (!entry.getKey().startsWith("dna_source_")) continue;
            DnaDefinition definition = DnaRegistry.INSTANCE.get(entry.getValue());
            if (definition != null && definition.genes().contains(gene)) return true;
        }
        return false;
    }

    private DnaProfile() {}
}
