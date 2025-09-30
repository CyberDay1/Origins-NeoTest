package io.github.apace100.origins.datagen;

import io.github.apace100.origins.datagen.provider.OriginsBlockStateProvider;
import io.github.apace100.origins.datagen.provider.OriginsBlockTags;
import io.github.apace100.origins.datagen.provider.OriginsItemModelProvider;
import io.github.apace100.origins.datagen.provider.OriginsItemTags;
import io.github.apace100.origins.datagen.provider.OriginsLanguageProvider;
import io.github.apace100.origins.datagen.provider.OriginsLootTableProvider;
import io.github.apace100.origins.datagen.provider.OriginsRecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class OriginsDataGen {
    private OriginsDataGen() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var pack = generator.getPackOutput();
        var lookup = event.getLookupProvider();
        var existing = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new OriginsLanguageProvider(pack));
        generator.addProvider(event.includeClient(), new OriginsBlockStateProvider(pack, existing));
        generator.addProvider(event.includeClient(), new OriginsItemModelProvider(pack, existing));
        generator.addProvider(event.includeServer(), new OriginsLootTableProvider(pack, lookup));
        generator.addProvider(event.includeServer(), new OriginsRecipeProvider(pack, lookup));
        var blockTags = new OriginsBlockTags(pack, lookup, existing);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new OriginsItemTags(pack, lookup, blockTags.contentsGetter(), existing));
    }
}
