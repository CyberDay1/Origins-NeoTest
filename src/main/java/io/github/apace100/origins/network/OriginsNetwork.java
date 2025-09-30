package io.github.apace100.origins.network;

import io.github.apace100.origins.util.OriginsConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = OriginsConstants.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class OriginsNetwork {
    private static final String NETWORK_VERSION = "1";

    private OriginsNetwork() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
        registrar.playToClient(OriginSyncPayload.TYPE, OriginSyncPayload.STREAM_CODEC, OriginsNetwork::handleSync);
    }

    private static void handleSync(OriginSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Client-side sync placeholder
        });
    }
}
