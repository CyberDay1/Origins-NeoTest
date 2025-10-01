package io.github.apace100.origins.common.network;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ChooseOriginC2S(Optional<ResourceLocation> originId) implements CustomPacketPayload {
    public static final Type<ChooseOriginC2S> TYPE = new Type<>(Origins.id("choose_origin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChooseOriginC2S> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), ChooseOriginC2S::originId, ChooseOriginC2S::new
    );

    @Override
    public Type<ChooseOriginC2S> type() {
        return TYPE;
    }

    public static void handle(ChooseOriginC2S payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            payload.originId().ifPresentOrElse(id -> {
                if (!PlayerOriginManager.set(player, id)) {
                    player.displayClientMessage(Component.translatable("commands.origins.error.unknown", id.toString()), true);
                }
            }, () -> PlayerOriginManager.clear(player));
        });
    }
}
