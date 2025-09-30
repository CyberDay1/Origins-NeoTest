package io.github.apace100.origins.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class OriginsDataGeneration {
    private OriginsDataGeneration() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(OriginsDataGeneration::gatherData);
    }

    private static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        if (event.includeClient()) {
            generator.addProvider(true, new LangGen(output));
        }
    }
}
