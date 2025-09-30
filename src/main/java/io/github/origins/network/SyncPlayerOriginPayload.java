package io.github.origins.network;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkEvent;

public record SyncPlayerOriginPayload(UUID playerId, ResourceLocation originId) {
    public static void encode(SyncPlayerOriginPayload payload, FriendlyByteBuf buffer) {
        buffer.writeUUID(payload.playerId());
        buffer.writeResourceLocation(payload.originId());
    }

    public static SyncPlayerOriginPayload decode(FriendlyByteBuf buffer) {
        UUID playerId = buffer.readUUID();
        ResourceLocation originId = buffer.readResourceLocation();
        return new SyncPlayerOriginPayload(playerId, originId);
    }

    public static void handle(SyncPlayerOriginPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Client-side handling will be implemented once power syncing is available.
        });
        context.setPacketHandled(true);
    }
}
