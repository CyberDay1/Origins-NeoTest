package io.github.apace100.origins.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface AltOriginScreen {

    static void renderImpactIcon(GuiGraphics graphics, int x, int y, String impact, boolean highlighted) {
        int color = switch (impact) {
            case "NONE" -> 0xFF6F6F6F;
            case "LOW" -> 0xFF4CAF50;
            case "MEDIUM" -> 0xFFFFC107;
            case "HIGH" -> 0xFFF57C00;
            case "VERY_HIGH" -> 0xFFD32F2F;
            default -> 0xFF9E9E9E;
        };
        if (highlighted) {
            int base = color & 0x00FFFFFF;
            int lightened = Math.min(base + 0x00141414, 0x00FFFFFF);
            color = 0xFF000000 | lightened;
        }
        graphics.fill(x + 1, y + 1, x + 7, y + 7, color);
    }

    default void renderRandomOrigin(GuiGraphics graphics, int mouseX, int mouseY, float delta, int x, int y, boolean selected) {
        boolean highlighted = isIconHighlighted(mouseX, mouseY, x, y);
        renderSlotBackground(graphics, x, y, selected, highlighted);
        ItemStack stack = new ItemStack(Items.NETHER_STAR);
        graphics.renderItem(stack, x + 5, y + 5);
        int impactIndex = (int) (getTickTime() / 15.0) % 4;
        String impact = switch (impactIndex) {
            case 0 -> "LOW";
            case 1 -> "MEDIUM";
            case 2 -> "HIGH";
            default -> "VERY_HIGH";
        };
        renderImpactIcon(graphics, x, y, impact, highlighted);
    }

    default void renderOriginWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta, int x, int y, boolean selected, String impact, MutableComponent originName) {
        boolean mouseHovering = isMouseHoveringIcon(mouseX, mouseY, x, y);
        boolean highlighted = isIconHighlighted(mouseX, mouseY, x, y);
        renderSlotBackground(graphics, x, y, selected, highlighted);
        renderImpactIcon(graphics, x, y, impact, highlighted);
        if (mouseHovering) {
            Component text = getCurrentLayerTranslationKey().append(": ").append(originName);
            graphics.renderTooltip(getScreenFont(), text, mouseX, mouseY);
        }
    }

    default void renderSlotBackground(GuiGraphics graphics, int x, int y, boolean selected, boolean highlighted) {
        int borderColor = highlighted ? 0xFFFFFFFF : 0xFF5A5A5A;
        int innerColor = selected ? 0xFF243A5A : 0xFF111111;
        if (highlighted && !selected) {
            innerColor = 0xFF1C2233;
        }
        graphics.fill(x, y, x + 26, y + 26, borderColor);
        graphics.fill(x + 1, y + 1, x + 25, y + 25, innerColor);
        if (selected) {
            graphics.fill(x + 2, y + 2, x + 24, y + 24, 0x551C64FF);
        }
    }

    MutableComponent getCurrentLayerTranslationKey();

    boolean isIconHighlighted(int mouseX, int mouseY, int x, int y);

    boolean isOriginSelected(int index);

    boolean isRandomOriginSelected();

    Font getScreenFont();

    default boolean isMouseHoveringIcon(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseY >= y && mouseX < x + 26 && mouseY < y + 26;
    }

    float getTickTime();
}
