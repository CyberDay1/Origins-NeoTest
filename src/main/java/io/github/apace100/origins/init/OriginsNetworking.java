package io.github.apace100.origins.init;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.network.SyncPlayerOriginPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public final class OriginsNetworking {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(Origins.MOD_ID, "main"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();
    private static boolean bootstrapped;

    private OriginsNetworking() {
    }

    public static void register() {
        if (bootstrapped) {
            return;
        }

        int index = 0;
        CHANNEL.registerMessage(
            index++,
            SyncPlayerOriginPayload.class,
            SyncPlayerOriginPayload::encode,
            SyncPlayerOriginPayload::decode,
            SyncPlayerOriginPayload::handle
        );

        bootstrapped = true;
    }

    public static void sendToPlayer(ServerPlayer player, Object payload) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
    }

    public static void sendToServer(Object payload) {
        CHANNEL.sendToServer(payload);
    }
}
