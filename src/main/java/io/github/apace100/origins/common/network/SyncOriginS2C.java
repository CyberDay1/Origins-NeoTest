package io.github.apace100.origins.common.network;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.neoforge.capability.OriginCapabilities;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SyncOriginS2C(Optional<ResourceLocation> originId) implements CustomPacketPayload {
    public static final Type<SyncOriginS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "sync_origin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncOriginS2C> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), SyncOriginS2C::originId, SyncOriginS2C::new
    );

    @Override
    public Type<SyncOriginS2C> type() {
        return TYPE;
    }

    public static SyncOriginS2C from(PlayerOrigin origin) {
        return new SyncOriginS2C(origin.getOriginIdOptional());
    }

    public static void handle(SyncOriginS2C payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) {
                return;
            }

            PlayerOrigin origin = player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
            if (origin != null) {
                origin.setOriginId(payload.originId().orElse(null));
            }
        });
    }
}
