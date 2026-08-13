package com.wakame.humanaugmentation.surgery;

import com.wakame.humanaugmentation.body.BodyRegion;
import com.wakame.humanaugmentation.body.BodySlot;
import com.wakame.humanaugmentation.body.Limb;
import com.wakame.humanaugmentation.registry.ModItems;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class SurgeryScreen extends AbstractContainerScreen<SurgeryMenu> {
    private static final BodyRegion[] DISPLAY_TABS = {
            BodyRegion.ORGANS, BodyRegion.TORSO, BodyRegion.SKIN,
            BodyRegion.RIGHT_ARM, BodyRegion.RIGHT_LEG
    };
    private static final int BG = 0xFF0F1423;
    private static final int PANEL = 0xFF151D30;
    private static final int PANEL_LIGHT = 0xFF1C2940;
    private static final int BORDER = 0xFF24516A;
    private static final int CYAN = 0xFF00D6FF;
    private static final int GREEN = 0xFF00FF66;
    private static final int RED = 0xFFFF3344;
    private static final int METABOLISM_LABEL = 0xFFFF5555;
    private static final int STABILITY_LABEL = 0xFF55FF55;
    private static final int VALUE_TEXT = 0xFFFFFFFF;
    private static final int TEXT = 0xFFE0F7FA;

    public SurgeryScreen(SurgeryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = SurgeryLayout.WIDTH;
        imageHeight = SurgeryLayout.HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, BG);
        drawGrid(graphics);
        panel(graphics, 6, 6, 294, 28);
        panel(graphics, 6, 34, 82, 151);
        panel(graphics, 86, 34, 210, 151);
        panel(graphics, 214, 34, 294, 151);
        panel(graphics, 64, 156, 232, 242);
        drawTabs(graphics, mouseX - leftPos, mouseY - topPos);
        drawLayerTabs(graphics, mouseX - leftPos, mouseY - topPos);
        drawPatient(graphics);
        drawAnatomicalNetwork(graphics);
        drawLimbPreviewDiagram(graphics);
        drawSlotBackgrounds(graphics);
        drawSlotGhosts(graphics);
        drawStatusPanel(graphics, mouseX - leftPos, mouseY - topPos);
        drawInventorySlots(graphics);
        drawTargetCorners(graphics, 10, 38, 78, 147);
    }

    private void drawGrid(GuiGraphics graphics) {
        for (int x = 8; x < imageWidth; x += 16) graphics.fill(leftPos + x, topPos + 30, leftPos + x + 1, topPos + 154, 0x141B6A80);
        for (int y = 34; y < 154; y += 16) graphics.fill(leftPos + 4, topPos + y, leftPos + imageWidth - 4, topPos + y + 1, 0x141B6A80);
    }

    private void panel(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        graphics.fill(leftPos + x1, topPos + y1, leftPos + x2, topPos + y2, PANEL);
        graphics.fill(leftPos + x1, topPos + y1, leftPos + x2, topPos + y1 + 1, BORDER);
        graphics.fill(leftPos + x1, topPos + y2 - 1, leftPos + x2, topPos + y2, BORDER);
        graphics.fill(leftPos + x1, topPos + y1, leftPos + x1 + 1, topPos + y2, BORDER);
        graphics.fill(leftPos + x2 - 1, topPos + y1, leftPos + x2, topPos + y2, BORDER);
    }

    private void drawTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = 75;
        for (BodyRegion region : DISPLAY_TABS) {
            int width = 42;
            boolean hover = inside(mouseX, mouseY, x, 8, x + width, 26);
            boolean selected = isDisplayedTabSelected(region);
            int color = selected ? 0xFF08778E : hover ? PANEL_LIGHT : PANEL;
            graphics.fill(leftPos + x, topPos + 8, leftPos + x + width, topPos + 26, color);
            graphics.fill(leftPos + x, topPos + 25, leftPos + x + width, topPos + 26, selected ? CYAN : BORDER);
            String key = region == BodyRegion.RIGHT_ARM ? "arms"
                    : region == BodyRegion.RIGHT_LEG ? "legs"
                    : region.name().toLowerCase();
            Component label = Component.translatable("screen.humanaugmentation.region." + key);
            graphics.drawCenteredString(font, label, leftPos + x + width / 2, topPos + 13, TEXT);
            x += 43;
        }
    }

    private boolean isDisplayedTabSelected(BodyRegion tab) {
        if (tab == BodyRegion.RIGHT_ARM) {
            return menu.selectedRegion() == BodyRegion.RIGHT_ARM || menu.selectedRegion() == BodyRegion.LEFT_ARM;
        }
        if (tab == BodyRegion.RIGHT_LEG) {
            return menu.selectedRegion() == BodyRegion.RIGHT_LEG || menu.selectedRegion() == BodyRegion.LEFT_LEG;
        }
        return menu.selectedRegion() == tab;
    }

    private void drawLayerTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        if (menu.availableLayers() <= 1) return;
        String[] labels = {"I", "II", "III"};
        int x = 220;
        for (int layer = 0; layer < menu.availableLayers(); layer++) {
            boolean selected = menu.selectedLayer() == layer;
            boolean hover = inside(mouseX, mouseY, x, 36, x + 18, 45);
            graphics.fill(leftPos + x, topPos + 36, leftPos + x + 18, topPos + 45,
                    selected ? 0xFF08778E : hover ? PANEL_LIGHT : 0xFF090D16);
            graphics.fill(leftPos + x, topPos + 44, leftPos + x + 18, topPos + 45,
                    selected ? CYAN : BORDER);
            graphics.drawCenteredString(font, labels[layer], leftPos + x + 9, topPos + 36,
                    selected ? VALUE_TEXT : TEXT);
            x += 20;
        }
    }

    private void drawPatient(GuiGraphics graphics) {
        InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, leftPos + 12, topPos + 40, leftPos + 76, topPos + 126,
                34, 0.0625F, 0.0F, 0.0F, minecraft.player);
        drawEcg(graphics, 12, 132, 68, 10, GREEN);
        String health = compactHealth(minecraft.player.getHealth()) + "/"
                + compactHealth(minecraft.player.getMaxHealth());
        graphics.drawCenteredString(font, Component.literal(health),
                leftPos + 44, topPos + 118, TEXT);
    }

    private void drawSlotBackgrounds(GuiGraphics graphics) {
        Limb limb = menu.selectedRegion().limb();
        if (limb != null) {
            Limb[] rendered = displayedLimbs(limb);
            for (Limb renderedLimb : rendered) {
                for (int cell = 0; cell < Limb.SLOT_COUNT; cell++) {
                    if (!menu.isUnlocked(renderedLimb, cell)) continue;
                    slotFrame(graphics, menu.limbX(renderedLimb, cell) - 1,
                            menu.limbY(renderedLimb, cell) - 1, renderedLimb.isExtremity(cell));
                }
            }
        } else {
            for (BodySlot slot : BodySlot.values()) {
                if (!menu.selectedRegion().contains(slot)) continue;
                if (menu.isUnlocked(slot)) {
                    slotFrame(graphics, SurgeryLayout.primaryX(slot) - 1, SurgeryLayout.primaryY(slot) - 1, false);
                }
            }
        }
        if (menu.selectedLayer() == 2 && menu.surgeryTier() >= 5) {
            for (int bonus = 0; bonus < 2; bonus++) {
                slotFrame(graphics,
                        SurgeryLayout.thirdLayerBonusX(menu.selectedRegion(), bonus) - 1,
                        SurgeryLayout.thirdLayerBonusY(menu.selectedRegion(), bonus) - 1, true);
            }
        }
        if (!menu.usesCombinedLimbLayout() || menu.selectedRegion().limb() == null) {
            graphics.drawCenteredString(font,
                    Component.translatable("screen.humanaugmentation.region." + menu.selectedRegion().name().toLowerCase()),
                    leftPos + 148, topPos + 137, CYAN);
        }
    }

    private void drawAnatomicalNetwork(GuiGraphics graphics) {
        if (menu.selectedRegion() != BodyRegion.ORGANS) return;
        int trunkX = 190;
        graphics.fill(leftPos + trunkX, topPos + 39, leftPos + trunkX + 2, topPos + 130, 0x44336D7C);
        graphics.fill(leftPos + trunkX + 3, topPos + 48, leftPos + trunkX + 4, topPos + 126, 0x443D2438);

        for (BodySlot slot : BodySlot.values()) {
            if (!BodyRegion.ORGANS.contains(slot)) continue;
            int slotX = SurgeryLayout.primaryX(slot);
            int slotY = SurgeryLayout.primaryY(slot);
            int centerY = slotY + 8;
            int branchColor = slot == BodySlot.HEART || slot == BodySlot.BLOOD ? 0x553D2438 : 0x44336D7C;
            int branchStart = Math.min(slotX + 16, trunkX);
            int branchEnd = Math.max(slotX + 16, trunkX);
            graphics.fill(leftPos + branchStart, topPos + centerY,
                    leftPos + branchEnd, topPos + centerY + 1, branchColor);
            graphics.fill(leftPos + trunkX - 2, topPos + centerY - 1, leftPos + trunkX + 3, topPos + centerY + 2, branchColor);
        }
        graphics.fill(leftPos + trunkX - 3, topPos + 37, leftPos + trunkX + 5, topPos + 39, 0x5524516A);
        graphics.fill(leftPos + trunkX - 3, topPos + 130, leftPos + trunkX + 5, topPos + 132, 0x5524516A);
    }

    private void drawLimbPreviewDiagram(GuiGraphics graphics) {
        Limb limb = menu.selectedRegion().limb();
        if (limb == null) return;
        int line = 0x88336D7C;
        if (menu.usesCombinedLimbLayout() && limb.isVertical()) {
            for (int centerX : new int[]{116, 176}) {
                graphics.fill(leftPos + centerX, topPos + 46, leftPos + centerX + 2, topPos + 145, line);
                graphics.fill(leftPos + centerX - 18, topPos + 83,
                        leftPos + centerX + 20, topPos + 85, line);
            }
            return;
        }
        if (menu.usesCombinedLimbLayout()) {
            for (int centerX : new int[]{116, 176}) {
                graphics.fill(leftPos + centerX, topPos + 46,
                        leftPos + centerX + 2, topPos + 145, line);
                graphics.fill(leftPos + centerX - 18, topPos + 83,
                        leftPos + centerX + 20, topPos + 85, line);
            }
            return;
        }
        if (!menu.isLimbPreviewLayout()) return;
        if (!limb.isVertical()) {
            boolean left = limb == Limb.LEFT_ARM;
            int jointX = left ? 152 : 128;
            graphics.fill(leftPos + 96, topPos + 65, leftPos + 188, topPos + 67, line);
            graphics.fill(leftPos + jointX, topPos + 60, leftPos + jointX + 2, topPos + 100, line);
            if (left) graphics.fill(leftPos + 90, topPos + 76, leftPos + 102, topPos + 78, line);
            else graphics.fill(leftPos + 180, topPos + 76, leftPos + 192, topPos + 78, line);
        } else {
            int offset = limb == Limb.RIGHT_LEG ? 20 : -8;
            int centerX = 122 + offset;
            graphics.fill(leftPos + centerX, topPos + 48, leftPos + centerX + 2, topPos + 139, line);
            graphics.fill(leftPos + 106 + offset, topPos + 75, leftPos + 142 + offset, topPos + 77, line);
        }
    }

    private void drawSlotGhosts(GuiGraphics graphics) {
        Limb limb = menu.selectedRegion().limb();
        if (limb != null) {
            Limb[] rendered = displayedLimbs(limb);
            for (Limb renderedLimb : rendered) {
                for (int cell = 0; cell < Limb.SLOT_COUNT; cell++) {
                    if (!menu.isUnlocked(renderedLimb, cell)) continue;
                    if (!menu.getSlot(menu.selectedLimbIndex(renderedLimb, cell)).hasItem()) {
                        renderGhost(graphics, ghostForLimb(renderedLimb, cell),
                                menu.limbX(renderedLimb, cell), menu.limbY(renderedLimb, cell));
                    }
                }
            }
            return;
        }

        for (BodySlot slot : BodySlot.values()) {
            if (!menu.selectedRegion().contains(slot)) continue;
            if (menu.isUnlocked(slot) && !menu.getSlot(menu.selectedPrimaryIndex(slot)).hasItem()) {
                renderGhost(graphics, ghostFor(slot), SurgeryLayout.primaryX(slot), SurgeryLayout.primaryY(slot));
            }
        }
    }

    private void renderGhost(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.setColor(0.55F, 0.65F, 0.68F, 0.22F);
        graphics.renderItem(stack, leftPos + x, topPos + y);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static ItemStack ghostFor(BodySlot slot) {
        return switch (slot) {
            case BRAIN -> ModItems.NATURAL_BRAIN.toStack();
            case EYES -> ModItems.NATURAL_EYES.toStack();
            case HEART -> ModItems.NATURAL_HEART.toStack();
            case LUNGS -> ModItems.NATURAL_LUNGS.toStack();
            case DIGESTIVE -> ModItems.NATURAL_DIGESTIVE.toStack();
            case BLOOD -> ModItems.NATURAL_BLOOD.toStack();
            case MUSCLES -> ModItems.NATURAL_MUSCLES.toStack();
            case SKELETON -> ModItems.NATURAL_SKELETON.toStack();
            case SPINE -> ModItems.NATURAL_SPINE.toStack();
            case SKIN -> ModItems.NATURAL_SKIN.toStack();
            case SPECIAL -> ModItems.NATURAL_SPECIAL.toStack();
        };
    }

    private static ItemStack ghostForLimb(Limb limb, int cell) {
        if (!limb.isExtremity(cell)) return ghostFor(limb.tissueAt(cell));
        return switch (limb) {
            case RIGHT_ARM -> ModItems.NATURAL_RIGHT_HAND.toStack();
            case LEFT_ARM -> ModItems.NATURAL_LEFT_HAND.toStack();
            case RIGHT_LEG -> ModItems.NATURAL_RIGHT_FOOT.toStack();
            case LEFT_LEG -> ModItems.NATURAL_LEFT_FOOT.toStack();
        };
    }

    private void slotFrame(GuiGraphics graphics, int x, int y, boolean extremity) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + 18, topPos + y + 18, extremity ? 0xFF26384B : 0xFF090D16);
        graphics.fill(leftPos + x, topPos + y, leftPos + x + 18, topPos + y + 1, extremity ? GREEN : BORDER);
        graphics.fill(leftPos + x, topPos + y + 17, leftPos + x + 18, topPos + y + 18, extremity ? GREEN : BORDER);
    }

    private void drawInventorySlots(GuiGraphics graphics) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                inventorySlot(graphics, SurgeryLayout.INVENTORY_X - 1 + column * 18,
                        SurgeryLayout.INVENTORY_Y - 1 + row * 18, false);
            }
        }
        graphics.fill(leftPos + SurgeryLayout.INVENTORY_X - 3, topPos + SurgeryLayout.HOTBAR_Y - 5,
                leftPos + SurgeryLayout.INVENTORY_X + 9 * 18 + 1, topPos + SurgeryLayout.HOTBAR_Y - 4, BORDER);
        for (int column = 0; column < 9; column++) {
            inventorySlot(graphics, SurgeryLayout.INVENTORY_X - 1 + column * 18,
                    SurgeryLayout.HOTBAR_Y - 1, true);
        }
    }

    private void inventorySlot(GuiGraphics graphics, int x, int y, boolean hotbar) {
        int edge = hotbar ? 0xFF32718E : BORDER;
        graphics.fill(leftPos + x, topPos + y, leftPos + x + 18, topPos + y + 18, edge);
        graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + 17, topPos + y + 17, 0xFF090D16);
        graphics.fill(leftPos + x + 2, topPos + y + 2, leftPos + x + 17, topPos + y + 3, 0xFF1C2940);
    }

    private void drawStatusPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        drawMeter(graphics, 220, 48, Component.translatable("screen.humanaugmentation.metabolism_label"),
                Integer.toString(menu.metabolismLoad()), METABOLISM_LABEL,
                Math.min(1.0F, menu.metabolismLoad() / 12.0F), menu.metabolismLoad() > 8 ? RED : GREEN);
        drawMeter(graphics, 220, 76, Component.translatable("screen.humanaugmentation.stability_label"),
                menu.stability() + "%", STABILITY_LABEL,
                menu.stability() / 100.0F, menu.stability() < 50 ? RED : GREEN);
        graphics.drawString(font, Component.translatable("screen.humanaugmentation.installed", menu.installedCount()), leftPos + 220, topPos + 104, TEXT, false);
        int tierColor = menu.surgeryTier() > 0 ? CYAN : RED;
        graphics.drawString(font, Component.translatable("screen.humanaugmentation.table_tier", menu.surgeryTier()),
                leftPos + 220, topPos + 114, tierColor, false);
        graphics.drawString(font, Component.translatable("screen.humanaugmentation.sp_amount",
                compactAmount(menu.storedSp()), compactAmount(menu.spCapacity())),
                leftPos + 220, topPos + 124, VALUE_TEXT, false);
        if (menu.metabolismLoad() > 8 || menu.stability() < 50) {
            float pulse = 0.55F + 0.35F * (float)Math.sin(Util.getMillis() / 350.0);
            int alpha = Math.max(0, Math.min(255, (int)(pulse * 255.0F)));
            int warning = (alpha << 24) | 0x00FF3344;
            graphics.fill(leftPos + 219, topPos + 135, leftPos + 289, topPos + 148, warning);
            graphics.drawString(font, Component.translatable("screen.humanaugmentation.warning_short"),
                    leftPos + 222, topPos + 138, 0xFFFFFFFF, false);
        } else {
            boolean hover = inside(mouseX, mouseY, 219, 135, 289, 148);
            int background = hover ? 0xCC164D37 : 0xAA10271E;
            int edge = hover ? 0xFF55FF88 : 0xFF20884D;
            graphics.fill(leftPos + 219, topPos + 135, leftPos + 289, topPos + 148, edge);
            graphics.fill(leftPos + 220, topPos + 136, leftPos + 288, topPos + 147, background);
            graphics.drawCenteredString(font, Component.translatable("screen.humanaugmentation.ready"),
                    leftPos + 254, topPos + 137, hover ? VALUE_TEXT : GREEN);
        }
    }

    private void drawMeter(GuiGraphics graphics, int x, int y, Component label, String valueText,
                           int labelColor, float value, int barColor) {
        graphics.drawString(font, label, leftPos + x, topPos + y, labelColor, false);
        int valueX = x + 68 - font.width(valueText);
        graphics.drawString(font, valueText, leftPos + valueX, topPos + y, VALUE_TEXT, false);

        int barY = y + 12;
        graphics.fill(leftPos + x, topPos + barY, leftPos + x + 68, topPos + barY + 7, BORDER);
        graphics.fill(leftPos + x + 1, topPos + barY + 1, leftPos + x + 67, topPos + barY + 6, 0xFF060910);
        int fillWidth = (int)(64 * Math.max(0, Math.min(1, value)));
        if (fillWidth > 0) {
            graphics.fill(leftPos + x + 2, topPos + barY + 2,
                    leftPos + x + 2 + fillWidth, topPos + barY + 5, barColor);
        }
    }

    private void drawEcg(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        double load = 1.0 + menu.metabolismLoad() * 0.12 + (1.0 - minecraft.player.getHealth() / minecraft.player.getMaxHealth()) * 2.0;
        int phase = (int)(Util.getMillis() / Math.max(18.0, 55.0 / load));
        int previousY = y + height / 2;
        for (int px = 0; px < width; px++) {
            int sample = Math.floorMod(px + phase, 24);
            int offset = switch (sample) { case 8 -> -2; case 9 -> 4; case 10 -> -5; case 11 -> 2; default -> 0; };
            int nextY = y + height / 2 + offset;
            graphics.fill(leftPos + x + px, topPos + Math.min(previousY, nextY), leftPos + x + px + 1, topPos + Math.max(previousY, nextY) + 1, color);
            previousY = nextY;
        }
    }

    private void drawTargetCorners(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        int length = 8;
        graphics.fill(leftPos + x1, topPos + y1, leftPos + x1 + length, topPos + y1 + 1, CYAN);
        graphics.fill(leftPos + x1, topPos + y1, leftPos + x1 + 1, topPos + y1 + length, CYAN);
        graphics.fill(leftPos + x2 - length, topPos + y2 - 1, leftPos + x2, topPos + y2, CYAN);
        graphics.fill(leftPos + x2 - 1, topPos + y2 - length, leftPos + x2, topPos + y2, CYAN);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("screen.humanaugmentation.surgery_terminal"), 12, 13, TEXT, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 82, 153, TEXT, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (hoveredSlot != null && !menu.getCarried().isEmpty()) {
            int cost = menu.previewCost(hoveredSlot, menu.getCarried());
            if (cost > 0) {
                graphics.renderTooltip(font, Component.translatable("screen.humanaugmentation.sp_cost_preview", cost),
                        mouseX, mouseY - 14);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double x = mouseX - leftPos;
        double y = mouseY - topPos;
        int tabX = 75;
        for (BodyRegion region : DISPLAY_TABS) {
            if (inside(x, y, tabX, 8, tabX + 42, 26)) {
                menu.selectRegion(region.ordinal());
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, region.ordinal());
                return true;
            }
            tabX += 43;
        }
        if (menu.availableLayers() > 1) {
            int layerX = 220;
            for (int layer = 0; layer < menu.availableLayers(); layer++) {
                if (inside(x, y, layerX, 36, layerX + 18, 45)) {
                    menu.clickMenuButton(minecraft.player, 200 + layer);
                    Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, 200 + layer);
                    return true;
                }
                layerX += 20;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean inside(double x, double y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x < x2 && y >= y1 && y < y2;
    }

    private static String compactAmount(int value) {
        if (value >= 1_000_000_000) return String.format(java.util.Locale.ROOT, "%.1fB", value / 1_000_000_000.0);
        if (value >= 1_000_000) return String.format(java.util.Locale.ROOT, "%.1fM", value / 1_000_000.0);
        if (value >= 1_000) return String.format(java.util.Locale.ROOT, "%.1fK", value / 1_000.0);
        return Integer.toString(value);
    }

    private static String compactHealth(float value) {
        float rounded = Math.round(value * 10.0F) / 10.0F;
        if (Math.abs(rounded - Math.round(rounded)) < 0.001F) {
            return Integer.toString(Math.round(rounded));
        }
        return String.format(java.util.Locale.ROOT, "%.1f", rounded);
    }

    private Limb[] displayedLimbs(Limb selected) {
        if (!menu.usesCombinedLimbLayout()) return new Limb[]{selected};
        return selected.isVertical()
                ? new Limb[]{Limb.LEFT_LEG, Limb.RIGHT_LEG}
                : new Limb[]{Limb.RIGHT_ARM, Limb.LEFT_ARM};
    }
}
