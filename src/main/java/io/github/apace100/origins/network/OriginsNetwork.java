package io.github.apace100.origins.network;

import io.github.apace100.origins.Origins;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class OriginsNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent e) {
        var registrar = e.registrar("1");
        registrar.playToClient(OriginSyncPayload.TYPE, OriginSyncPayload.CODEC, OriginSyncPayload::handleClient);
    }
}
