package com.wakame.humanaugmentation.registry;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.surgery.SurgeryTableBlock;
import com.wakame.humanaugmentation.surgery.CombustionPowerInputBlock;
import com.wakame.humanaugmentation.dna.DnaAnalyzerBlock;
import com.wakame.humanaugmentation.dna.CreativeFeSourceBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HumanAugmentation.MOD_ID);
    private static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(HumanAugmentation.MOD_ID);

    public static final DeferredBlock<Block> SURGERY_TABLE = BLOCKS.register("surgery_table", () -> new SurgeryTableBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SURGERY_TABLE_T2 = BLOCKS.register("surgery_table_t2", () -> new SurgeryTableBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SURGERY_TABLE_T3 = BLOCKS.register("surgery_table_t3", () -> new SurgeryTableBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(6.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SURGERY_TABLE_T4 = BLOCKS.register("surgery_table_t4", () -> new SurgeryTableBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(8.0F).requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> SURGERY_TABLE_ITEM = BLOCK_ITEMS.register("surgery_table", () ->
            new BlockItem(SURGERY_TABLE.get(), new Item.Properties()));
    public static final DeferredBlock<Block> MEDICAL_FRAME = simple("medical_frame");
    public static final DeferredBlock<Block> SURGICAL_ARM = simple("surgical_arm");
    public static final DeferredBlock<Block> MEDICAL_FRAME_T2 = simple("medical_frame_t2");
    public static final DeferredBlock<Block> SURGICAL_ARM_T2 = simple("surgical_arm_t2");
    public static final DeferredBlock<Block> PRECISION_CONTROL_MODULE = simple("precision_control_module");
    public static final DeferredBlock<Block> MEDICAL_FRAME_T3 = simple("medical_frame_t3");
    public static final DeferredBlock<Block> SURGICAL_ARM_T3 = simple("surgical_arm_t3");
    public static final DeferredBlock<Block> LIFE_SUPPORT_MODULE = simple("life_support_module");
    public static final DeferredBlock<Block> MEDICAL_FRAME_T4 = simple("medical_frame_t4");
    public static final DeferredBlock<Block> SURGICAL_ARM_T4 = simple("surgical_arm_t4");
    public static final DeferredBlock<Block> NEURAL_CONTROL_MODULE = simple("neural_control_module");
    public static final DeferredBlock<Block> COMBUSTION_POWER_INPUT = BLOCKS.register("combustion_power_input", () ->
            new CombustionPowerInputBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DNA_ANALYZER = BLOCKS.register("dna_analyzer", () ->
            new DnaAnalyzerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(4.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CREATIVE_FE_SOURCE = BLOCKS.register("creative_fe_source", () ->
            new CreativeFeSourceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(-1.0F, 3_600_000.0F)));
    public static final DeferredItem<BlockItem> MEDICAL_FRAME_ITEM = blockItem("medical_frame", MEDICAL_FRAME);
    public static final DeferredItem<BlockItem> SURGICAL_ARM_ITEM = blockItem("surgical_arm", SURGICAL_ARM);
    public static final DeferredItem<BlockItem> SURGERY_TABLE_T2_ITEM = blockItem("surgery_table_t2", SURGERY_TABLE_T2);
    public static final DeferredItem<BlockItem> MEDICAL_FRAME_T2_ITEM = blockItem("medical_frame_t2", MEDICAL_FRAME_T2);
    public static final DeferredItem<BlockItem> SURGICAL_ARM_T2_ITEM = blockItem("surgical_arm_t2", SURGICAL_ARM_T2);
    public static final DeferredItem<BlockItem> PRECISION_CONTROL_MODULE_ITEM =
            blockItem("precision_control_module", PRECISION_CONTROL_MODULE);
    public static final DeferredItem<BlockItem> SURGERY_TABLE_T3_ITEM = blockItem("surgery_table_t3", SURGERY_TABLE_T3);
    public static final DeferredItem<BlockItem> MEDICAL_FRAME_T3_ITEM = blockItem("medical_frame_t3", MEDICAL_FRAME_T3);
    public static final DeferredItem<BlockItem> SURGICAL_ARM_T3_ITEM = blockItem("surgical_arm_t3", SURGICAL_ARM_T3);
    public static final DeferredItem<BlockItem> LIFE_SUPPORT_MODULE_ITEM = blockItem("life_support_module", LIFE_SUPPORT_MODULE);
    public static final DeferredItem<BlockItem> SURGERY_TABLE_T4_ITEM = blockItem("surgery_table_t4", SURGERY_TABLE_T4);
    public static final DeferredItem<BlockItem> MEDICAL_FRAME_T4_ITEM = blockItem("medical_frame_t4", MEDICAL_FRAME_T4);
    public static final DeferredItem<BlockItem> SURGICAL_ARM_T4_ITEM = blockItem("surgical_arm_t4", SURGICAL_ARM_T4);
    public static final DeferredItem<BlockItem> NEURAL_CONTROL_MODULE_ITEM = blockItem("neural_control_module", NEURAL_CONTROL_MODULE);
    public static final DeferredItem<BlockItem> COMBUSTION_POWER_INPUT_ITEM =
            blockItem("combustion_power_input", COMBUSTION_POWER_INPUT);
    public static final DeferredItem<BlockItem> DNA_ANALYZER_ITEM = blockItem("dna_analyzer", DNA_ANALYZER);
    public static final DeferredItem<BlockItem> CREATIVE_FE_SOURCE_ITEM = blockItem("creative_fe_source", CREATIVE_FE_SOURCE);

    private ModBlocks() {}

    private static DeferredBlock<Block> simple(String name) {
        return BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                .strength(3.5F).requiresCorrectToolForDrops()));
    }

    private static DeferredItem<BlockItem> blockItem(String name, DeferredBlock<? extends Block> block) {
        return BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
    }
}
