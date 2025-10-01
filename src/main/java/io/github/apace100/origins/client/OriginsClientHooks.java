package io.github.apace100.origins.client;

import io.github.apace100.origins.client.gui.OriginSelectionScreen;
import io.github.apace100.origins.neoforge.capability.OriginCapabilities;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class OriginsClientHooks {
    private OriginsClientHooks() {
    }

    public static void openOriginScreen(ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        PlayerOrigin origin = player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
        if (origin != null && origin.hasChosen()) {
            return;
        }

        minecraft.setScreen(new OriginSelectionScreen(stack));
    }
}
