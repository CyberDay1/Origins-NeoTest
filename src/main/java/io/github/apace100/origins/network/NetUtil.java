package io.github.apace100.origins.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NetUtil {
    private NetUtil(){}
    public static void syncOriginTo(ServerPlayer player, String id) {
        PacketDistributor.sendToPlayer(player, new OriginSyncPayload(id));
    }
}
