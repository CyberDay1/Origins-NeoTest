package io.github.apace100.origins.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.client.config.OriginsClientConfig;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.network.TogglePhantomizeC2S;
import io.github.apace100.origins.neoforge.capability.OriginCapabilities;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.power.OriginPowerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class OriginsClient {
    private static KeyMapping openOriginScreen;
    private static KeyMapping togglePhantom;

    private OriginsClient() {
    }

    public static void init() {
        OriginsClientConfig.register();
    }

    @EventBusSubscriber(modid = Origins.MOD_ID, value = Dist.CLIENT)
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
            togglePhantom = new KeyMapping(
                "key.origins.toggle_phantom",
                InputConstants.KEY_V,
                "key.categories.origins"
            );
            event.register(openOriginScreen);
            event.register(togglePhantom);
        }
    }

    @EventBusSubscriber(modid = Origins.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeEvents {
        private ForgeEvents() {
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null) {
                return;
            }

            if (openOriginScreen != null && openOriginScreen.consumeClick()) {
                PlayerOrigin origin = player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
                if (origin == null || origin.hasChosen()) {
                    return;
                }
                OriginsClientHooks.openOriginScreen(player.getMainHandItem());
            }

            if (togglePhantom != null && togglePhantom.consumeClick()) {
                PlayerOrigin origin = player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
                if (origin != null && OriginPowerManager.hasPower(player, OriginPowerManager.PHASE)) {
                    ModNetworking.sendToServer(new TogglePhantomizeC2S(!origin.isPhantomized()));
                }
            }

            PlayerOrigin origin = player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
            if (origin != null && origin.isPhantomized() && OriginPowerManager.hasPower(player, OriginPowerManager.PHASE) && !player.isSpectator()) {
                if (player.isShiftKeyDown()) {
                    player.noPhysics = true;
                    player.resetFallDistance();
                } else if (player.noPhysics) {
                    player.noPhysics = false;
                }
            } else if (!player.isSpectator() && player.noPhysics) {
                player.noPhysics = false;
            }
        }
    }
}
