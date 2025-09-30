package io.github.apace100.origins.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class ModDataGen {
    private ModDataGen() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModDataGen::gatherData);
    }

    private static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeClient()) {
            generator.addProvider(true, new ModBlockStateGen(output, existingFileHelper));
            generator.addProvider(true, new ModItemModelGen(output, existingFileHelper));
            generator.addProvider(true, new LangGen(output));
        }

        if (event.includeServer()) {
            var lookup = event.getLookupProvider();
            generator.addProvider(true, new ModLootTableProvider(output, lookup));
            generator.addProvider(true, new ModRecipeProvider(output, lookup));
        }
    }
}
