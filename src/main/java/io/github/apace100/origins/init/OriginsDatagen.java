package io.github.apace100.origins.init;

import io.github.apace100.origins.datagen.OriginsDataGenerators;
import net.neoforged.bus.api.IEventBus;

public final class OriginsDatagen {
    private OriginsDatagen() {
    }

    public static void register(IEventBus modEventBus) {
        OriginsDataGenerators.register(modEventBus);
    }
}
