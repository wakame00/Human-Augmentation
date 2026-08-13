package com.wakame.humanaugmentation.surgery;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.Nullable;

public final class SurgeryTableBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<SurgeryTableBlock> CODEC = simpleCodec(SurgeryTableBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public SurgeryTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override protected MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        open(player, pos);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        open(player, pos);
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    private void open(Player player, BlockPos pos) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!SurgerySession.enter(serverPlayer, pos)) {
            serverPlayer.displayClientMessage(Component.translatable("message.humanaugmentation.surgery_table_occupied"), true);
            return;
        }
        serverPlayer.openMenu(menuProvider(pos), pos);
    }

    private MenuProvider menuProvider(BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) -> new SurgeryMenu(id, inventory, player, pos),
                Component.translatable("screen.humanaugmentation.surgery"));
    }
}
