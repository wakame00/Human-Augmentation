package com.wakame.humanaugmentation.compat;

import java.util.List;

public enum CompatTarget {
    CREATE("Create", "create"),
    MEKANISM("Mekanism", "mekanism"),
    APPLIED_ENERGISTICS_2("Applied Energistics 2", "ae2"),
    ORITECH("Oritech", "oritech"),
    ALEX_CAVES("Alex's Caves", "alexscaves"),
    GOETY("Goety", "goety"),
    IRONS_SPELLBOOKS("Iron's Spells 'n Spellbooks", "irons_spellbooks"),
    PROJECT_E("ProjectE", "projecte"),
    CC_TWEAKED("CC: Tweaked", "computercraft"),
    ARS_NOUVEAU("Ars Nouveau", "ars_nouveau"),
    BLOOD_MAGIC("Blood Magic", "bloodmagic"),
    ENDER_IO("Ender IO", "enderio"),
    DRACONIC_EVOLUTION("Draconic Evolution", "draconicevolution"),
    MYSTICAL_AGRICULTURE("Mystical Agriculture", "mysticalagriculture"),
    PSI("Psi", "psi"),
    SLASHBLADE("SlashBlade", "slashblade"),
    ICE_AND_FIRE("Ice and Fire", "iceandfire", "iceandfire_ce");

    private final String displayName;
    private final List<String> modIds;

    CompatTarget(String displayName, String... modIds) {
        this.displayName = displayName;
        this.modIds = List.of(modIds);
    }

    public String displayName() { return displayName; }
    public List<String> modIds() { return modIds; }
}
