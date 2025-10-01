package io.github.apace100.origins.client;

import io.github.apace100.origins.client.gui.OriginSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public final class OriginsClientHooks {
    private OriginsClientHooks() {
    }

    public static void openOriginScreen(ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        minecraft.setScreen(new OriginSelectionScreen(stack));
    }
}
