package io.github.origins.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class OriginsDataGenerators {
    private OriginsDataGenerators() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(OriginsDataGenerators::gather);
    }

    private static void gather(GatherDataEvent event) {
        var generator = event.getGenerator();

        if (event.includeClient()) {
            generator.addProvider(true, new OriginsEnglishLanguageProvider(generator.getPackOutput()));
        }
    }
}
