package io.github.apace100.origins.network;
import io.github.apace100.origins.util.ResourceLocationCompat;

import io.github.apace100.origins.Origins;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OriginSyncPayload(String originId) implements CustomPacketPayload {
    public static final Type<OriginSyncPayload> TYPE = new Type<>(ResourceLocationCompat.mod("sync_origin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OriginSyncPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, OriginSyncPayload::originId, OriginSyncPayload::new);

    @Override
    public Type<OriginSyncPayload> type() {
        return TYPE;
    }

    public static void handleClient(final OriginSyncPayload msg, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;
            // TODO: update client cache/UI as needed
        });
    }
}
