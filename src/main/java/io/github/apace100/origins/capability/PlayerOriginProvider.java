package io.github.apace100.origins.capability;

import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = OriginsConstants.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class PlayerOriginProvider {
    private static final Map<UUID, PlayerOrigin> ORIGINS = new ConcurrentHashMap<>();

    private PlayerOriginProvider() {
    }

    public static PlayerOrigin get(Player player) {
        return ORIGINS.computeIfAbsent(player.getUUID(), id -> new PlayerOrigin());
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        PlayerOrigin original = get(event.getOriginal());
        PlayerOrigin copy = get(event.getEntity());
        copy.setOriginId(original.getOriginId().orElse(null));
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        ORIGINS.remove(event.getEntity().getUUID());
    }
}
