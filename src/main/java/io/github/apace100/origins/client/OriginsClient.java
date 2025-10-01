package io.github.apace100.origins.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.client.config.OriginsClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.TickEvent;

public final class OriginsClient {
    private static KeyMapping openOriginScreen;

    private OriginsClient() {
    }

    public static void init() {
        OriginsClientConfig.register();
    }

    @EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModEvents {
        private ModEvents() {
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            openOriginScreen = new KeyMapping(
                "key.origins.open_selection",
                InputConstants.KEY_O,
                "key.categories.origins"
            );
            event.register(openOriginScreen);
        }
    }

    @EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class ForgeEvents {
        private ForgeEvents() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || openOriginScreen == null) {
                return;
            }

            if (openOriginScreen.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                LocalPlayer player = minecraft.player;
                if (player != null) {
                    OriginsClientHooks.openOriginScreen(player.getMainHandItem());
                }
            }
        }
    }
}
