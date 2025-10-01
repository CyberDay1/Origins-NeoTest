package io.github.apace100.origins.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class PlayerOriginUtil {
    private PlayerOriginUtil() {}

    public static Optional<PlayerOrigin> get(Player player) {
        return Optional.ofNullable(player.getCapability(OriginCapabilities.PLAYER_ORIGIN));
    }

    public static PlayerOrigin getOrCreate(Player player) {
        return get(player).orElseGet(PlayerOrigin::new);
    }

    public static void setAndSync(ServerPlayer player, String id) {
        get(player).ifPresent(data -> {
            data.setOriginId(id);
            // Networking sync handled in networking section
        });
    }
}
