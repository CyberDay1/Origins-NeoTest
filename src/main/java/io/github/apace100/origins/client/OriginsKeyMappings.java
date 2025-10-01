package io.github.apace100.origins.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.client.gui.OriginSelectionScreen;
import io.github.apace100.origins.common.config.ModConfigs;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Origins.MOD_ID)
public final class OriginsKeyMappings {
    private static KeyMapping openOriginsScreen;

    private OriginsKeyMappings() {
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        openOriginsScreen = new KeyMapping("key.origins.open_selection", InputConstants.KEY_O, "key.categories.inventory");
        event.register(openOriginsScreen);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (openOriginsScreen != null && openOriginsScreen.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                PlayerOrigin origin = PlayerOriginManager.get(minecraft.player);
                boolean hasOrigin = origin != null && origin.getOriginIdOptional().isPresent();
                if (!hasOrigin || ModConfigs.SERVER.allowOrbReuse.get()) {
                    ItemStack held = minecraft.player.getMainHandItem();
                    minecraft.setScreen(new OriginSelectionScreen(held));
                }
            }
        }
    }
}
