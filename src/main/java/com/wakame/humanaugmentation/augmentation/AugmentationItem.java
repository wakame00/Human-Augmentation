package com.wakame.humanaugmentation.augmentation;

import com.wakame.humanaugmentation.body.BodySlot;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;

public class AugmentationItem extends Item {
    private final BodySlot preferredSlot;
    private final int tier;
    private final int capacityCost;
    private final int metabolismCost;
    private final int maximumDurability;

    public AugmentationItem(BodySlot preferredSlot, int tier, int capacityCost, int metabolismCost, Properties properties) {
        super(properties.durability(durabilityForTier(tier)));
        this.preferredSlot = preferredSlot;
        this.tier = tier;
        this.capacityCost = capacityCost;
        this.metabolismCost = metabolismCost;
        this.maximumDurability = durabilityForTier(tier);
    }

    public BodySlot preferredSlot() { return preferredSlot; }
    public int tier() { return tier; }
    public int capacityCost() { return capacityCost; }
    public int metabolismCost() { return metabolismCost; }
    public int maximumDurability() { return maximumDurability; }

    public double durabilityEfficiency(int damage) {
        int remaining = Math.max(0, maximumDurability - damage);
        if (remaining == 0) return 0.0;
        double ratio = (double) remaining / maximumDurability;
        if (ratio <= 0.30) return 0.50;
        if (ratio <= 0.70) return 0.85;
        return 1.0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        double efficiency = durabilityEfficiency(stack.getDamageValue());
        String state = efficiency <= 0.0 ? "disabled" : efficiency < 0.85 ? "critical"
                : efficiency < 1.0 ? "worn" : "normal";
        ChatFormatting color = efficiency <= 0.0 ? ChatFormatting.DARK_RED
                : efficiency < 0.85 ? ChatFormatting.RED
                : efficiency < 1.0 ? ChatFormatting.YELLOW : ChatFormatting.GREEN;
        tooltip.add(Component.translatable("tooltip.humanaugmentation.part_condition." + state,
                (int)Math.round(efficiency * 100.0)).withStyle(color));
        tooltip.add(Component.translatable("tooltip.humanaugmentation.augmentation_tier", tier)
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.humanaugmentation.preferred_slot",
                Component.translatable("tooltip.humanaugmentation.body_slot."
                        + preferredSlot.name().toLowerCase(java.util.Locale.ROOT)))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.humanaugmentation.augmentation_costs",
                capacityCost, metabolismCost).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable(getDescriptionId() + ".effect")
                .withStyle(ChatFormatting.GREEN));
    }

    private static int durabilityForTier(int tier) {
        return switch (tier) {
            case 1 -> 1000;
            case 2 -> 2500;
            case 3 -> 6000;
            case 4 -> 15000;
            default -> 30000;
        };
    }
}
