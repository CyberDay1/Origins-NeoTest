package io.github.apace100.origins.common.network;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import io.github.apace100.origins.power.OriginPowerManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TogglePhantomizeC2S(boolean phantomized) implements CustomPacketPayload {
    public static final Type<TogglePhantomizeC2S> TYPE = new Type<>(Origins.id("toggle_phantomize"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePhantomizeC2S> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, TogglePhantomizeC2S::phantomized,
        TogglePhantomizeC2S::new
    );

    @Override
    public Type<TogglePhantomizeC2S> type() {
        return TYPE;
    }

    public static void handle(TogglePhantomizeC2S payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!OriginPowerManager.hasPower(player, OriginPowerManager.PHASE)) {
                return;
            }

            PlayerOrigin origin = PlayerOriginManager.get(player);
            if (origin == null) {
                return;
            }

            if (payload.phantomized() && !player.isCreative() && player.getFoodData().getFoodLevel() <= 0) {
                player.displayClientMessage(Component.translatable("power.origins.phantomize.out_of_hunger"), true);
                PlayerOriginManager.setPhantomized(player, false);
                return;
            }

            PlayerOriginManager.setPhantomized(player, payload.phantomized());
        });
    }
}
