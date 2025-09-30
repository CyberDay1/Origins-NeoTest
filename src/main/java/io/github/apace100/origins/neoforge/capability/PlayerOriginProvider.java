package io.github.apace100.origins.neoforge.capability;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerOriginProvider implements ICapabilityProvider<Player, Void, PlayerOrigin> {
    private static PlayerOriginProvider instance;

    private final Map<UUID, PlayerOrigin> origins = new ConcurrentHashMap<>();

    public PlayerOriginProvider() {
        instance = this;
    }

    @Override
    public PlayerOrigin getCapability(Player player, Void context) {
        return origins.computeIfAbsent(player.getUUID(), uuid -> {
            PlayerOrigin origin = new PlayerOrigin();
            PlayerOriginManager.load(player, origin);
            return origin;
        });
    }

    public static PlayerOriginProvider getInstance() {
        return instance;
    }

    public void set(Player player, PlayerOrigin origin) {
        origins.put(player.getUUID(), origin);
    }

    public void invalidate(UUID uuid) {
        origins.remove(uuid);
    }
}
