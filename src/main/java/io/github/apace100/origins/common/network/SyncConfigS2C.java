package io.github.apace100.origins.common.network;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.config.ModConfigs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncConfigS2C(boolean syncPowersOnLogin, int maxTrackedPowers) implements CustomPacketPayload {
    public static final Type<SyncConfigS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "sync_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfigS2C> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, SyncConfigS2C::syncPowersOnLogin,
        ByteBufCodecs.VAR_INT, SyncConfigS2C::maxTrackedPowers,
        SyncConfigS2C::new
    );

    @Override
    public Type<SyncConfigS2C> type() {
        return TYPE;
    }

    public static void handle(SyncConfigS2C payload, IPayloadContext context) {
        context.enqueueWork(() -> ModConfigs.applySync(payload));
    }
}
