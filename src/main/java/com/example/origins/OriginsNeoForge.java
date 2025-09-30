package com.example.origins;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(OriginsNeoForge.MOD_ID)
public class OriginsNeoForge {
    public static final String MOD_ID = "origins";

    public OriginsNeoForge(IEventBus modEventBus) {
        // Register event listeners, registries, etc.
        ModEventBusEvents.init(modEventBus);
    }
}
