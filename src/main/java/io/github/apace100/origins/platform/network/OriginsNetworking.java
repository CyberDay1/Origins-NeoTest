package io.github.apace100.origins.platform.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class OriginsNetworking {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(OriginsNetworking::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").play(SyncOriginPayload.TYPE, SyncOriginPayload.STREAM_CODEC, SyncOriginPayload::handle);
    }
}
