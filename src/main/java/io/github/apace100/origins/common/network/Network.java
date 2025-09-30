package io.github.apace100.origins.common.network;

import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public final class Network {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(Origins.MOD_ID, "main"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();

    private static boolean bootstrapped;
    private static int index;

    private Network() {
    }

    public static void init() {
        if (bootstrapped) {
            return;
        }

        CHANNEL.registerMessage(
            nextIndex(),
            SyncConfigS2C.class,
            SyncConfigS2C::encode,
            SyncConfigS2C::decode,
            SyncConfigS2C::handle
        );

        bootstrapped = true;
    }

    private static int nextIndex() {
        return index++;
    }

    public static void sendToPlayer(ServerPlayer player, Object payload) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
    }

    public static void sendToServer(Object payload) {
        CHANNEL.sendToServer(payload);
    }
}
