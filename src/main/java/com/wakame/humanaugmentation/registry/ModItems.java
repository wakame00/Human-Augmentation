package com.wakame.humanaugmentation.registry;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.augmentation.ArtificialHeartItem;
import com.wakame.humanaugmentation.augmentation.AugmentationItem;
import com.wakame.humanaugmentation.augmentation.AugmentationRepairKitItem;
import com.wakame.humanaugmentation.body.BiologicalBodyPartItem;
import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.dna.DnaSamplerItem;
import com.wakame.humanaugmentation.dna.DnaSampleItem;
import com.wakame.humanaugmentation.dna.GeneInjectionItem;
import com.wakame.humanaugmentation.dna.GenePurgeItem;
import com.wakame.humanaugmentation.surgery.CreativeSurgeryTerminalItem;
import com.wakame.humanaugmentation.surgery.CreativeLimbLayoutPreviewItem;
import com.wakame.humanaugmentation.surgery.CreativeCombinedLegPreviewItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HumanAugmentation.MOD_ID);

    public static final DeferredItem<Item> ARTIFICIAL_HEART = ITEMS.register("artificial_heart", () -> new ArtificialHeartItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> ARTIFICIAL_LUNGS_T1 = augmentation("artificial_lungs_t1", BodySlot.LUNGS, 2, 8);
    public static final DeferredItem<Item> ARTIFICIAL_EYES_T1 = augmentation("artificial_eyes_t1", BodySlot.EYES, 1, 5);
    public static final DeferredItem<Item> REINFORCED_SPINE_T1 = augmentation("reinforced_spine_t1", BodySlot.SPINE, 2, 7);
    public static final DeferredItem<Item> ARTIFICIAL_MUSCLES_T1 = augmentation("artificial_muscles_t1", BodySlot.MUSCLES, 2, 8);
    public static final DeferredItem<Item> REINFORCED_SKELETON_T1 = augmentation("reinforced_skeleton_t1", BodySlot.SKELETON, 2, 7);
    public static final DeferredItem<Item> SYNTHETIC_SKIN_T1 = augmentation("synthetic_skin_t1", BodySlot.SKIN, 1, 4);
    public static final DeferredItem<Item> ADAPTIVE_SKIN_T2 = ITEMS.register("adaptive_skin_t2", () ->
            new AugmentationItem(BodySlot.SKIN, 2, 3, 9, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KINETIC_MUSCLES_T2 = ITEMS.register("kinetic_muscles_t2", () ->
            new AugmentationItem(BodySlot.MUSCLES, 2, 3, 12, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> NATURAL_BRAIN = bodyPart("natural_brain", BodySlot.BRAIN);
    public static final DeferredItem<Item> NATURAL_EYES = bodyPart("natural_eyes", BodySlot.EYES);
    public static final DeferredItem<Item> NATURAL_HEART = bodyPart("natural_heart", BodySlot.HEART);
    public static final DeferredItem<Item> NATURAL_LUNGS = bodyPart("natural_lungs", BodySlot.LUNGS);
    public static final DeferredItem<Item> NATURAL_DIGESTIVE = bodyPart("natural_digestive", BodySlot.DIGESTIVE);
    public static final DeferredItem<Item> NATURAL_BLOOD = bodyPart("natural_blood", BodySlot.BLOOD);
    public static final DeferredItem<Item> NATURAL_MUSCLES = bodyPart("natural_muscles", BodySlot.MUSCLES);
    public static final DeferredItem<Item> NATURAL_SKELETON = bodyPart("natural_skeleton", BodySlot.SKELETON);
    public static final DeferredItem<Item> NATURAL_SPINE = bodyPart("natural_spine", BodySlot.SPINE);
    public static final DeferredItem<Item> NATURAL_SKIN = bodyPart("natural_skin", BodySlot.SKIN);
    public static final DeferredItem<Item> NATURAL_SPECIAL = bodyPart("natural_special", BodySlot.SPECIAL);
    public static final DeferredItem<Item> NATURAL_HAND = bodyPart("natural_hand", BodySlot.MUSCLES);
    public static final DeferredItem<Item> NATURAL_FOOT = bodyPart("natural_foot", BodySlot.MUSCLES);
    public static final DeferredItem<Item> NATURAL_RIGHT_HAND = bodyPart("natural_right_hand", BodySlot.MUSCLES);
    public static final DeferredItem<Item> NATURAL_LEFT_HAND = bodyPart("natural_left_hand", BodySlot.MUSCLES);
    public static final DeferredItem<Item> NATURAL_RIGHT_FOOT = bodyPart("natural_right_foot", BodySlot.MUSCLES);
    public static final DeferredItem<Item> NATURAL_LEFT_FOOT = bodyPart("natural_left_foot", BodySlot.MUSCLES);
    public static final DeferredItem<Item> DNA_SAMPLER = ITEMS.register("dna_sampler", () -> new DnaSamplerItem(new Item.Properties().durability(64)));
    public static final DeferredItem<Item> DNA_SAMPLE = ITEMS.register("dna_sample", () -> new DnaSampleItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> DNA_EXTRACTION_REAGENT = ITEMS.registerSimpleItem("dna_extraction_reagent");
    public static final DeferredItem<Item> DNA_AMPLIFICATION_MIX = ITEMS.registerSimpleItem("dna_amplification_mix");
    public static final DeferredItem<Item> STERILE_SYRINGE = ITEMS.registerSimpleItem("sterile_syringe");
    public static final DeferredItem<Item> GENE_INJECTION = ITEMS.register("gene_injection", () ->
            new GeneInjectionItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> GENE_PURGE = ITEMS.register("gene_purge", () ->
            new GenePurgeItem(new Item.Properties().stacksTo(8)));
    public static final DeferredItem<Item> AUGMENTATION_REPAIR_KIT = ITEMS.register("augmentation_repair_kit", () ->
            new AugmentationRepairKitItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> CREATIVE_SURGERY_TERMINAL = ITEMS.register("creative_surgery_terminal", () ->
            new CreativeSurgeryTerminalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> CREATIVE_LIMB_LAYOUT_PREVIEW = ITEMS.register("creative_limb_layout_preview", () ->
            new CreativeLimbLayoutPreviewItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> CREATIVE_COMBINED_LEG_PREVIEW = ITEMS.register("creative_combined_leg_preview", () ->
            new CreativeCombinedLegPreviewItem(new Item.Properties().stacksTo(1)));

    private ModItems() {}

    private static DeferredItem<Item> bodyPart(String name, BodySlot slot) {
        return ITEMS.register(name, () -> new BiologicalBodyPartItem(slot, new Item.Properties().stacksTo(1)));
    }

    private static DeferredItem<Item> augmentation(String name, BodySlot slot, int capacityCost, int metabolismCost) {
        return ITEMS.register(name, () -> new AugmentationItem(slot, 1, capacityCost, metabolismCost,
                new Item.Properties().stacksTo(1)));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
