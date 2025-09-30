package io.github.apace100.origins.neoforge.capability;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerOriginProvider implements ICapabilityProvider<Player, Void, PlayerOrigin> {
    private final Map<UUID, PlayerOrigin> origins = new ConcurrentHashMap<>();

    @Override
    public PlayerOrigin getCapability(Player player, Void context) {
        return origins.computeIfAbsent(player.getUUID(), uuid -> new PlayerOrigin());
    }
}
