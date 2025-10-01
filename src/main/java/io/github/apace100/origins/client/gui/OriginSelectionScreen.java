package io.github.apace100.origins.client.gui;

import io.github.apace100.origins.common.network.ChooseOriginC2S;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.registry.OriginRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OriginSelectionScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen.origins.select_origin");
    private static final Component CONFIRM = Component.translatable("screen.origins.confirm");
    private static final Component RESET = Component.translatable("screen.origins.reset");
    private static final Component NONE = Component.translatable("screen.origins.none");

    private final ItemStack heldStack;
    private List<Origin> origins = List.of();
    private Origin selected;
    private CycleButton<Optional<Origin>> originCycle;
    private Button confirmButton;

    public OriginSelectionScreen(ItemStack heldStack) {
        super(TITLE);
        this.heldStack = heldStack.copy();
    }

    @Override
    protected void init() {
        origins = new ArrayList<>(OriginRegistry.values());
        origins.sort(Comparator.comparing(origin -> origin.id().toString()));

        Optional<Origin> initial = origins.isEmpty() ? Optional.empty() : Optional.of(origins.get(0));
        selected = initial.orElse(null);

        originCycle = addRenderableWidget(CycleButton.<Optional<Origin>>builder(value -> value.map(Origin::name).orElse(NONE))
            .withValues(buildOptions())
            .withInitialValue(initial)
            .displayOnlyValue()
            .create(width / 2 - 100, height / 2 - 30, 200, 20, Component.translatable("screen.origins.origin"),
                (button, value) -> selected = value.orElse(null)));

        confirmButton = addRenderableWidget(Button.builder(CONFIRM, button -> confirmSelection())
            .bounds(width / 2 - 100, height / 2, 200, 20)
            .build());

        addRenderableWidget(Button.builder(RESET, button -> {
            ModNetworking.sendToServer(new ChooseOriginC2S(Optional.empty()));
            onClose();
        }).bounds(width / 2 - 100, height / 2 + 24, 200, 20).build());

        confirmButton.active = selected != null;
    }

    private List<Optional<Origin>> buildOptions() {
        List<Optional<Origin>> options = new ArrayList<>();
        options.add(Optional.empty());
        origins.forEach(origin -> options.add(Optional.of(origin)));
        return options;
    }

    private void confirmSelection() {
        if (selected != null) {
            ModNetworking.sendToServer(new ChooseOriginC2S(Optional.of(selected.id())));
        }
        onClose();
    }

    @Override
    public void tick() {
        super.tick();
        if (originCycle != null) {
            confirmButton.active = selected != null;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 60, 0xFFFFFF);
        if (selected != null) {
            graphics.drawCenteredString(font, selected.description(), width / 2, height / 2 - 10, 0xAAAAAA);
        } else {
            graphics.drawCenteredString(font, Component.translatable("screen.origins.no_selection"), width / 2, height / 2 - 10, 0xAAAAAA);
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

    public ItemStack getHeldStack() {
        return heldStack;
    }
}
