package com.wakame.humanaugmentation.dna;

import com.mojang.serialization.MapCodec;
import com.wakame.humanaugmentation.registry.ModBlockEntities;
import com.wakame.humanaugmentation.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;

public final class DnaAnalyzerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<DnaAnalyzerBlock> CODEC = simpleCodec(DnaAnalyzerBlock::new);
    public DnaAnalyzerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DnaAnalyzerBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.DNA_ANALYZER.get(),
                DnaAnalyzerBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DnaAnalyzerBlockEntity analyzer))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        boolean accepted;
        if (stack.is(ModItems.DNA_SAMPLE.get())) accepted = analyzer.insertOne(0, stack, player);
        else if (stack.is(ModItems.DNA_EXTRACTION_REAGENT.get())
                || stack.is(ModItems.DNA_AMPLIFICATION_MIX.get())
                || stack.is(ModItems.STERILE_SYRINGE.get())) accepted = analyzer.insertOne(1, stack, player);
        else {
            player.displayClientMessage(Component.translatable("message.humanaugmentation.dna_analyzer_status",
                    analyzer.energy().getEnergyStored(), analyzer.stageName()), true);
            return ItemInteractionResult.CONSUME;
        }
        player.displayClientMessage(Component.translatable(accepted
                ? "message.humanaugmentation.dna_analyzer_inserted"
                : "message.humanaugmentation.dna_analyzer_rejected"), true);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DnaAnalyzerBlockEntity analyzer)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                ItemStack extracted = analyzer.extractForPlayer();
                if (!extracted.isEmpty() && !player.getInventory().add(extracted)) player.drop(extracted, false);
            } else {
                player.displayClientMessage(Component.translatable("message.humanaugmentation.dna_analyzer_detailed_status",
                        analyzer.energy().getEnergyStored(), analyzer.stageName(), analyzer.progress(),
                        analyzer.requiredProgress(), analyzer.waitingFor(),
                        stackName(analyzer.displayStack(0)), stackName(analyzer.displayStack(1)),
                        stackName(analyzer.displayStack(2))), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static Component stackName(ItemStack stack) {
        return stack.isEmpty() ? Component.literal("-") : stack.getHoverName();
    }
}
