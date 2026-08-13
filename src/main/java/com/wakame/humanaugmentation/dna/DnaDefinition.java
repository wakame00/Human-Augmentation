package com.wakame.humanaugmentation.dna;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record DnaDefinition(ResourceLocation entity, double sampleChance, List<ResourceLocation> genes) {
    public DnaDefinition { genes = List.copyOf(genes); }
}
