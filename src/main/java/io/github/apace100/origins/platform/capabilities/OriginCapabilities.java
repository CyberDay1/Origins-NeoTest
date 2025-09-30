package io.github.apace100.origins.platform.capabilities;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class OriginCapabilities {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(OriginCapabilities::onRegisterCaps);
    }

    private static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.register(PlayerOrigin.class);
    }
}
