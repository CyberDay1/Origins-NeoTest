package io.github.apace100.origins.datagen;

import io.github.apace100.origins.datagen.tags.OriginsBlockTags;
import io.github.apace100.origins.datagen.tags.OriginsItemTags;
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
        var packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();

        if (event.includeServer()) {
            var blockTags = new OriginsBlockTags(packOutput, lookupProvider, existingFileHelper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new OriginsItemTags(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
            generator.addProvider(true, new ModLootTableProvider(packOutput, lookupProvider));
            generator.addProvider(true, new ModRecipeProvider(packOutput, lookupProvider));
        }

        if (event.includeClient()) {
            generator.addProvider(true, new ModBlockStateGen(packOutput, existingFileHelper));
            generator.addProvider(true, new ModItemModelGen(packOutput, existingFileHelper));
            generator.addProvider(true, new OriginsLanguageProvider(packOutput));
        }
    }
}
