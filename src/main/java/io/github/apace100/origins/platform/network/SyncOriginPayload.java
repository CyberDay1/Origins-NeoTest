package io.github.apace100.origins.platform.network;

import io.github.apace100.origins.platform.capabilities.PlayerOrigin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncOriginPayload(String originId) implements CustomPacketPayload {

    public static final Type<SyncOriginPayload> TYPE = new Type<>(new ResourceLocation("origins", "sync_origin"));
    public static final StreamCodec<FriendlyByteBuf, SyncOriginPayload> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> buf.writeUtf(payload.originId),
                           buf -> new SyncOriginPayload(buf.readUtf()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncOriginPayload payload, IPayloadContext context) {
        // Handle sync here
    }

    public static void sendTo(ServerPlayer player) {
        // send logic (placeholder)
    }
}
