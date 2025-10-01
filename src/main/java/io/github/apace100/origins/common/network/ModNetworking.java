package io.github.apace100.origins.common.network;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.config.ModConfigs;
import io.github.apace100.origins.common.network.ChooseOriginC2S;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";

    private ModNetworking() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetworking::onRegisterPayloadHandlers);
    }

    private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(SyncConfigS2C.TYPE, SyncConfigS2C.STREAM_CODEC, SyncConfigS2C::handle);
        registrar.playToClient(SyncOriginS2C.TYPE, SyncOriginS2C.STREAM_CODEC, SyncOriginS2C::handle);
        registrar.playToServer(ChooseOriginC2S.TYPE, ChooseOriginC2S.STREAM_CODEC, ChooseOriginC2S::handle);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        sendToPlayer(player, ModConfigs.createSyncPayload());

        PlayerOrigin origin = PlayerOriginManager.get(player);
        if (origin != null) {
            PlayerOriginManager.load(player, origin);
            sendToPlayer(player, SyncOriginS2C.from(origin));
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
