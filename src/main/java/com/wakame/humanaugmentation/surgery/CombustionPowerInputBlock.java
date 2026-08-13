package com.wakame.humanaugmentation.surgery;

import com.mojang.serialization.MapCodec;
import com.wakame.humanaugmentation.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class CombustionPowerInputBlock extends BaseEntityBlock {
    public static final MapCodec<CombustionPowerInputBlock> CODEC = simpleCodec(CombustionPowerInputBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public CombustionPowerInputBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CombustionPowerInputBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.COMBUSTION_POWER_INPUT.get(),
                CombustionPowerInputBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CombustionPowerInputBlockEntity input)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.getBurnTime(null) <= 0) {
            if (!level.isClientSide()) player.displayClientMessage(
                    Component.translatable("message.humanaugmentation.not_fuel"), true);
            return ItemInteractionResult.CONSUME;
        }
        if (!level.isClientSide()) {
            ItemStack remainder = input.inventory().insertItem(0, stack.copyWithCount(1), false);
            if (remainder.isEmpty() && !player.getAbilities().instabuild) stack.shrink(1);
            player.displayClientMessage(remainder.isEmpty()
                    ? Component.translatable("message.humanaugmentation.fuel_inserted")
                    : Component.translatable("message.humanaugmentation.fuel_slot_full"), true);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
}
