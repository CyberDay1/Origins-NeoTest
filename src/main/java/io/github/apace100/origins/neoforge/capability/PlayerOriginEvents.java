package io.github.apace100.origins.neoforge.capability;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class PlayerOriginEvents {
    private PlayerOriginEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(PlayerOriginEvents::onClone);
        NeoForge.EVENT_BUS.addListener(PlayerOriginEvents::onLoggedOut);
    }

    private static void onClone(PlayerEvent.Clone event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        PlayerOriginManager.copy(event.getOriginal(), event.getEntity());
    }

    private static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PlayerOrigin origin = PlayerOriginManager.get(player);
        if (origin != null) {
            PlayerOriginManager.save(player, origin);
        }

        PlayerOriginManager.remove(player);
    }
}
