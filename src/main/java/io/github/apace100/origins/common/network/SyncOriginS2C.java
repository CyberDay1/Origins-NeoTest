package io.github.apace100.origins.common.network;
import io.github.apace100.origins.util.ResourceLocationCompat;

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
import java.util.Set;

public record SyncOriginS2C(Optional<ResourceLocation> originId, Set<ResourceLocation> powers, boolean phantomized) implements CustomPacketPayload {
    public static final Type<SyncOriginS2C> TYPE = new Type<>(ResourceLocationCompat.mod("sync_origin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncOriginS2C> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), SyncOriginS2C::originId,
        ByteBufCodecs.collection(java.util.HashSet::new, ResourceLocation.STREAM_CODEC), SyncOriginS2C::powers,
        ByteBufCodecs.BOOL, SyncOriginS2C::phantomized,
        SyncOriginS2C::new
    );

    @Override
    public Type<SyncOriginS2C> type() {
        return TYPE;
    }

    public static SyncOriginS2C from(PlayerOrigin origin) {
        return new SyncOriginS2C(origin.getOriginIdOptional(), Set.copyOf(origin.getPowers()), origin.isPhantomized());
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
                origin.setPowers(Set.copyOf(payload.powers()));
                origin.setPhantomized(payload.phantomized());
            }
        });
    }
}
