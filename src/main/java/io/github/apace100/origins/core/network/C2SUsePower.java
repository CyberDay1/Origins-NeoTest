package io.github.apace100.origins.core.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

public record C2SUsePower(ResourceLocation powerId) {
    public static void encode(C2SUsePower message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.powerId);
    }

    public static C2SUsePower decode(FriendlyByteBuf buffer) {
        return new C2SUsePower(buffer.readResourceLocation());
    }

    public static void handle(C2SUsePower message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "Received power activation packet for " + message.powerId()));
            }
        });
        context.setPacketHandled(true);
    }
}
