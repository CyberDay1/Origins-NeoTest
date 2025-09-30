package io.github.apace100.origins.common.network;

import io.github.apace100.origins.common.config.OriginsConfig;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

public record SyncConfigS2C(boolean syncPowersOnLogin, int maxTrackedPowers) {
    public static void encode(SyncConfigS2C payload, FriendlyByteBuf buffer) {
        buffer.writeBoolean(payload.syncPowersOnLogin());
        buffer.writeVarInt(payload.maxTrackedPowers());
    }

    public static SyncConfigS2C decode(FriendlyByteBuf buffer) {
        boolean syncPowersOnLogin = buffer.readBoolean();
        int maxTrackedPowers = buffer.readVarInt();
        return new SyncConfigS2C(syncPowersOnLogin, maxTrackedPowers);
    }

    public static void handle(SyncConfigS2C payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() != null && context.getDirection().getReceptionSide().isClient()) {
                OriginsConfig.applySync(payload);
            }
        });
        context.setPacketHandled(true);
    }
}
