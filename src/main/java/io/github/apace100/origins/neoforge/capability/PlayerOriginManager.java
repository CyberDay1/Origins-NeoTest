package io.github.apace100.origins.neoforge.capability;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.network.SyncOriginS2C;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class PlayerOriginManager {
    private static final String ORIGIN_DATA_KEY = Origins.MOD_ID + "_origin";

    private PlayerOriginManager() {
    }

    public static PlayerOrigin get(Player player) {
        return player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
    }

    public static void set(ServerPlayer player, ResourceLocation originId) {
        PlayerOrigin origin = get(player);
        if (origin == null) {
            return;
        }

        origin.setOriginId(originId);
        save(player, origin);
        sync(player, origin);
    }

    public static void clear(ServerPlayer player) {
        PlayerOrigin origin = get(player);
        if (origin == null) {
            return;
        }

        origin.setOriginId(null);
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
        ModNetworking.sendToPlayer(player, new SyncOriginS2C(originId));
    }
}
