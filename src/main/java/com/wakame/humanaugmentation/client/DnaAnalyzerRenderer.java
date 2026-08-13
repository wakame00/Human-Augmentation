package com.wakame.humanaugmentation.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wakame.humanaugmentation.dna.DnaAnalyzerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class DnaAnalyzerRenderer implements BlockEntityRenderer<DnaAnalyzerBlockEntity> {
    @Override
    public void render(DnaAnalyzerBlockEntity analyzer, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        float[] positions = {0.28F, 0.5F, 0.72F};
        long time = analyzer.getLevel() == null ? 0L : analyzer.getLevel().getGameTime();
        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = analyzer.displayStack(slot);
            if (stack.isEmpty()) continue;
            pose.pushPose();
            pose.translate(positions[slot], 1.08F, 0.5F);
            pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees((time + partialTick) * 2.0F + slot * 35.0F));
            pose.scale(0.42F, 0.42F, 0.42F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND,
                    packedLight, packedOverlay, pose, buffers, analyzer.getLevel(), slot);
            pose.popPose();
        }
    }
}
