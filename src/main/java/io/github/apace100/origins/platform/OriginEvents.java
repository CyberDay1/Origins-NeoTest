package io.github.apace100.origins.platform;

import io.github.apace100.origins.platform.network.SyncOriginPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class OriginEvents {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(OriginEvents::onPlayerLogin);
    }

    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncOriginPayload.sendTo(player);
        }
    }
}
