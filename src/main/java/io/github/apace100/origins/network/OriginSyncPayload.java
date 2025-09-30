package io.github.apace100.origins.network;

import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OriginSyncPayload(ResourceLocation originId) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OriginsConstants.MODID, "sync_origin");
    public static final Type<OriginSyncPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OriginSyncPayload> STREAM_CODEC = StreamCodec.of(OriginSyncPayload::write, OriginSyncPayload::read);

    private static OriginSyncPayload read(RegistryFriendlyByteBuf buf) {
        ResourceLocation origin = null;
        if (buf.readBoolean()) {
            origin = buf.readResourceLocation();
        }
        return new OriginSyncPayload(origin);
    }

    private static void write(RegistryFriendlyByteBuf buf, OriginSyncPayload payload) {
        ResourceLocation origin = payload.originId();
        if (origin != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(origin);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public Type<OriginSyncPayload> type() {
        return TYPE;
    }
}
