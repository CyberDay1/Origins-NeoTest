package io.github.apace100.origins.events;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.datagen.tags.OriginsBlockTags;
import io.github.apace100.origins.datagen.tags.OriginsItemTags;
import io.github.apace100.origins.datagen.OriginsLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Origins.MOD_ID)
public final class DataGenEvents {
    @SubscribeEvent
    public static void gather(GatherDataEvent e) {
        PackOutput out = e.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookup = e.getLookupProvider();
        ExistingFileHelper helper = e.getExistingFileHelper();
        if (e.includeClient()) {
            e.getGenerator().addProvider(true, new OriginsLanguageProvider(out));
        }
        if (e.includeServer()) {
            var blockTags = new OriginsBlockTags(out, lookup, helper);
            e.getGenerator().addProvider(true, blockTags);
            e.getGenerator().addProvider(true, new OriginsItemTags(out, lookup, blockTags.contentsGetter(), helper));
        }
    }
}
