package com.example.origins;

import net.neoforged.bus.api.IEventBus;

public class ModEventBusEvents {
    public static void init(IEventBus modBus) {
        // Register DeferredRegisters here
        ModItems.ITEMS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
    }
}
