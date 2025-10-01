package io.github.apace100.origins.neoforge.capability;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.network.SyncOriginS2C;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.registry.OriginRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class PlayerOriginManager {
    private static final String ORIGIN_DATA_KEY = Origins.MOD_ID + "_origin";

    private PlayerOriginManager() {
    }

    public static PlayerOrigin get(Player player) {
        return player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
    }

    public static boolean set(ServerPlayer player, ResourceLocation originId) {
        PlayerOrigin origin = get(player);
        if (origin == null) {
            return false;
        }

        Optional<Origin> definition = OriginRegistry.get(originId);
        if (definition.isEmpty()) {
            Origins.LOGGER.warn("Tried to assign unknown origin {} to player {}", originId, player.getGameProfile().getName());
            return false;
        }

        origin.setOriginId(originId);
        Set<ResourceLocation> powers = definition.get().powers().stream().collect(Collectors.toSet());
        origin.setPowers(powers);
        save(player, origin);
        sync(player, origin);
        return true;
    }

    public static void clear(ServerPlayer player) {
        PlayerOrigin origin = get(player);
        if (origin == null) {
            return;
        }

        origin.setOriginId(null);
        origin.setPowers(Collections.emptySet());
        save(player, origin);
        sync(player, origin);
    }

    public static void load(Player player, PlayerOrigin origin) {
        CompoundTag data = player.getPersistentData();
        if (data.contains(ORIGIN_DATA_KEY, Tag.TAG_COMPOUND)) {
            origin.loadNBT(data.getCompound(ORIGIN_DATA_KEY));
        }
    }

    public static void save(Player player, PlayerOrigin origin) {
        player.getPersistentData().put(ORIGIN_DATA_KEY, origin.saveNBT());
    }

    public static void sync(ServerPlayer player) {
        PlayerOrigin origin = get(player);
        if (origin == null) {
            return;
        }

        sync(player, origin);
    }

    public static void remove(Player player) {
        PlayerOriginProvider provider = PlayerOriginProvider.getInstance();
        if (provider != null) {
            provider.invalidate(player.getUUID());
        }
    }

    public static void copy(Player original, Player clone) {
        PlayerOriginProvider provider = PlayerOriginProvider.getInstance();
        if (provider == null) {
            return;
        }

        PlayerOrigin originalOrigin = get(original);
        PlayerOrigin newOrigin = new PlayerOrigin();
        if (originalOrigin != null) {
            newOrigin.copyFrom(originalOrigin);
        }

        provider.set(clone, newOrigin);
        if (!clone.level().isClientSide) {
            save(clone, newOrigin);
        }

        if (clone instanceof ServerPlayer serverPlayer) {
            sync(serverPlayer, newOrigin);
        }
    }

    private static void sync(ServerPlayer player, PlayerOrigin origin) {
        Optional<ResourceLocation> originId = origin.getOriginIdOptional();
        ModNetworking.sendToPlayer(player, new SyncOriginS2C(originId, Set.copyOf(origin.getPowers())));
    }
}
