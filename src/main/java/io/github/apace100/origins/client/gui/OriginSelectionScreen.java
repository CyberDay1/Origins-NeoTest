package io.github.apace100.origins.client.gui;

import io.github.apace100.origins.common.network.ChooseOriginC2S;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.origin.OriginImpact;
import io.github.apace100.origins.common.registry.OriginRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OriginSelectionScreen extends Screen implements AltOriginScreen {
    private static final Component TITLE = Component.translatable("screen.origins.select_origin");
    private static final Component CONFIRM = Component.translatable("screen.origins.confirm");
    private static final Component RESET = Component.translatable("screen.origins.reset");
    private static final Component NO_SELECTION = Component.translatable("screen.origins.no_selection");
    private static final Component RANDOM_NAME = Component.translatable("screen.origins.random_origin");
    private static final Component RANDOM_DESCRIPTION = Component.translatable("screen.origins.random_origin.description");
    private static final Component RANDOM_IMPACT = Component.translatable("screen.origins.random_origin.impact");
    private static final int CHOICES_WIDTH = 219;
    private static final int CHOICES_HEIGHT = 182;
    private static final int ORIGIN_ICON_SIZE = 26;
    private static final int COUNT_PER_PAGE = 35;
    private static final int DETAILS_WIDTH = 176;
    private static final ItemStack RANDOM_ICON = new ItemStack(Items.NETHER_STAR);

    private final ItemStack heldStack;
    private final List<Origin> origins = new ArrayList<>();
    private final List<OriginIconButton> iconButtons = new ArrayList<>();
    private final RandomSource random = RandomSource.create();

    private Button confirmButton;
    private Button resetButton;
    private Button nextPageButton;
    private Button previousPageButton;

    private int calculatedLeft;
    private int calculatedTop;
    private int detailLeft;
    private int detailTop;
    private int currentPage;
    private int pages = 1;
    private boolean includeRandom;

    private int selectedIndex = -1;
    private Origin selectedOrigin;
    private boolean randomSelected;
    private ItemStack detailIcon = ItemStack.EMPTY;
    private Component detailTitle = NO_SELECTION;
    private Component detailImpact;
    private List<FormattedCharSequence> descriptionLines = List.of();
    private float tickTime;

    public OriginSelectionScreen(ItemStack heldStack) {
        super(TITLE);
        this.heldStack = heldStack.copy();
    }

    @Override
    protected void init() {
        origins.clear();
        origins.addAll(OriginRegistry.values());
        origins.sort(Comparator.comparing(origin -> origin.name().getString()));
        includeRandom = !origins.isEmpty();

        int totalEntries = getTotalEntries();
        pages = Math.max(1, (int) Math.ceil(totalEntries / (float) COUNT_PER_PAGE));
        currentPage = Math.min(currentPage, pages - 1);

        calculatedTop = (height - CHOICES_HEIGHT) / 2;
        detailTop = calculatedTop;
        detailLeft = (width - (CHOICES_WIDTH + 10 + DETAILS_WIDTH)) / 2 + CHOICES_WIDTH + 10;
        calculatedLeft = detailLeft - 10 - CHOICES_WIDTH;

        previousPageButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> changePage(-1))
            .bounds(calculatedLeft, calculatedTop + CHOICES_HEIGHT + 5, 20, 20)
            .build());
        nextPageButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> changePage(1))
            .bounds(calculatedLeft + CHOICES_WIDTH - 20, calculatedTop + CHOICES_HEIGHT + 5, 20, 20)
            .build());

        confirmButton = addRenderableWidget(Button.builder(CONFIRM, button -> confirmSelection())
            .bounds(detailLeft + DETAILS_WIDTH - 88, detailTop + CHOICES_HEIGHT - 24, 80, 20)
            .build());
        resetButton = addRenderableWidget(Button.builder(RESET, button -> {
            ModNetworking.sendToServer(new ChooseOriginC2S(Optional.empty()));
            onClose();
        }).bounds(detailLeft + 8, detailTop + CHOICES_HEIGHT - 24, 80, 20).build());

        rebuildIconButtons();
        updatePageButtons();
        clearSelection();
    }

    private void changePage(int delta) {
        if (pages <= 1) {
            return;
        }
        currentPage = (currentPage + delta) % pages;
        if (currentPage < 0) {
            currentPage += pages;
        }
        rebuildIconButtons();
        updatePageButtons();
    }

    private void updatePageButtons() {
        boolean showPages = pages > 1;
        if (previousPageButton != null) {
            previousPageButton.visible = showPages;
        }
        if (nextPageButton != null) {
            nextPageButton.visible = showPages;
        }
    }

    private void rebuildIconButtons() {
        iconButtons.forEach(this::removeWidget);
        iconButtons.clear();

        int startIndex = currentPage * COUNT_PER_PAGE;
        int endIndex = Math.min(startIndex + COUNT_PER_PAGE, getTotalEntries());
        int x = 0;
        int y = 0;
        for (int i = startIndex; i < endIndex; i++) {
            if (x > 6) {
                x = 0;
                y++;
            }
            int actualX = (12 + (x * (ORIGIN_ICON_SIZE + 2))) + calculatedLeft;
            int actualY = (10 + (y * (ORIGIN_ICON_SIZE + 4))) + calculatedTop;
            Origin origin = i < origins.size() ? origins.get(i) : null;
            boolean randomEntry = origin == null;
            OriginIconButton button = new OriginIconButton(actualX, actualY, randomEntry ? -1 : i, origin, randomEntry);
            iconButtons.add(button);
            addRenderableWidget(button);
            x++;
        }
    }

    private int getTotalEntries() {
        return includeRandom ? origins.size() + 1 : origins.size();
    }

    private void selectOrigin(int index) {
        if (index < 0 || index >= origins.size()) {
            return;
        }
        selectedIndex = index;
        selectedOrigin = origins.get(index);
        randomSelected = false;
        updateDetailForSelection();
    }

    private void selectRandom() {
        if (!includeRandom) {
            return;
        }
        selectedIndex = -1;
        selectedOrigin = null;
        randomSelected = true;
        updateDetailForSelection();
    }

    private void clearSelection() {
        selectedIndex = -1;
        selectedOrigin = null;
        randomSelected = false;
        detailTitle = NO_SELECTION;
        detailImpact = null;
        detailIcon = heldStack.isEmpty() ? ItemStack.EMPTY : heldStack.copy();
        descriptionLines = List.of();
        if (confirmButton != null) {
            confirmButton.active = false;
        }
    }

    private void updateDetailForSelection() {
        if (randomSelected) {
            detailTitle = RANDOM_NAME;
            detailImpact = RANDOM_IMPACT;
            detailIcon = RANDOM_ICON.copy();
            descriptionLines = this.font.split(RANDOM_DESCRIPTION, DETAILS_WIDTH - 16);
        } else if (selectedOrigin != null) {
            detailTitle = selectedOrigin.name().copy();
            OriginImpact impact = selectedOrigin.impact();
            detailImpact = Component.translatable("screen.origins.impact", impact.displayName());
            detailIcon = selectedOrigin.icon().copy();
            descriptionLines = this.font.split(selectedOrigin.description(), DETAILS_WIDTH - 16);
        } else {
            detailTitle = NO_SELECTION;
            detailImpact = null;
            detailIcon = heldStack.isEmpty() ? ItemStack.EMPTY : heldStack.copy();
            descriptionLines = List.of();
        }

        if (confirmButton != null) {
            confirmButton.active = randomSelected || selectedOrigin != null;
        }
    }

    private void confirmSelection() {
        if (minecraft == null) {
            return;
        }

        if (randomSelected) {
            if (origins.isEmpty()) {
                return;
            }
            Origin origin = pickRandomOrigin();
            ModNetworking.sendToServer(new ChooseOriginC2S(Optional.of(origin.id())));
        } else if (selectedOrigin != null) {
            ModNetworking.sendToServer(new ChooseOriginC2S(Optional.of(selectedOrigin.id())));
        } else {
            return;
        }

        onClose();
    }

    private Origin pickRandomOrigin() {
        if (minecraft != null && minecraft.level != null) {
            return origins.get(minecraft.level.random.nextInt(origins.size()));
        }
        return origins.get(random.nextInt(origins.size()));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        renderOriginChoicesBox(graphics);
        renderDetailPanel(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderPageIndicator(graphics);
        tickTime += partialTick;
    }

    private void renderOriginChoicesBox(GuiGraphics graphics) {
        graphics.fillGradient(calculatedLeft, calculatedTop, calculatedLeft + CHOICES_WIDTH, calculatedTop + CHOICES_HEIGHT, 0xCC101010, 0xCC181818);
        graphics.renderOutline(calculatedLeft, calculatedTop, CHOICES_WIDTH, CHOICES_HEIGHT, 0xFF5A5A5A);
    }

    private void renderPageIndicator(GuiGraphics graphics) {
        if (pages <= 1) {
            return;
        }
        String text = (currentPage + 1) + "/" + pages;
        graphics.drawCenteredString(font, text, calculatedLeft + (CHOICES_WIDTH / 2), calculatedTop + CHOICES_HEIGHT + 10, 0xFFFFFF);
    }

    private void renderDetailPanel(GuiGraphics graphics) {
        graphics.fill(detailLeft, detailTop, detailLeft + DETAILS_WIDTH, detailTop + CHOICES_HEIGHT, 0xAA000000);
        graphics.drawCenteredString(font, title, detailLeft + DETAILS_WIDTH / 2, detailTop - 16, 0xFFFFFF);

        int textX = detailLeft + 8;
        int textY = detailTop + 8;
        graphics.drawString(font, detailTitle, textX, textY, 0xFFFFFF);
        textY += font.lineHeight + 4;

        if (detailImpact != null) {
            graphics.drawString(font, detailImpact, textX, textY, 0xAAAAAA);
            textY += font.lineHeight + 6;
        }

        if (!detailIcon.isEmpty()) {
            graphics.renderItem(detailIcon, detailLeft + DETAILS_WIDTH - 28, detailTop + 8);
        }

        for (FormattedCharSequence line : descriptionLines) {
            graphics.drawString(font, line, textX, textY, 0xDDDDDD);
            textY += font.lineHeight;
        }

        if (origins.isEmpty()) {
            graphics.drawString(font, Component.translatable("screen.origins.empty"), textX, textY + 4, 0xAAAAAA);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public MutableComponent getCurrentLayerTranslationKey() {
        return Component.translatable("screen.origins.origin");
    }

    @Override
    public boolean isIconHighlighted(int mouseX, int mouseY, int x, int y) {
        return (getFocused() instanceof OriginIconButton button && button.getX() == x && button.getY() == y) || isMouseHoveringIcon(mouseX, mouseY, x, y);
    }

    @Override
    public boolean isOriginSelected(int index) {
        return selectedIndex == index && !randomSelected;
    }

    @Override
    public boolean isRandomOriginSelected() {
        return randomSelected;
    }

    @Override
    public Font getScreenFont() {
        return font;
    }

    @Override
    public float getTickTime() {
        return tickTime;
    }

    private final class OriginIconButton extends AbstractWidget {
        private final int index;
        private final Origin origin;
        private final boolean randomEntry;

        OriginIconButton(int x, int y, int index, Origin origin, boolean randomEntry) {
            super(x, y, ORIGIN_ICON_SIZE, ORIGIN_ICON_SIZE, Component.empty());
            this.index = index;
            this.origin = origin;
            this.randomEntry = randomEntry;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            if (randomEntry) {
                renderRandomOrigin(graphics, mouseX, mouseY, partialTick, getX(), getY(), isRandomOriginSelected());
            } else if (origin != null) {
                renderOriginWidget(graphics, mouseX, mouseY, partialTick, getX(), getY(), isOriginSelected(index), origin.impact().textureKey(), origin.name().copy());
                graphics.renderItem(origin.icon().copy(), getX() + 5, getY() + 5);
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (randomEntry) {
                selectRandom();
            } else if (origin != null) {
                selectOrigin(index);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
