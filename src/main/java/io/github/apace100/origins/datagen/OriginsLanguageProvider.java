package io.github.apace100.origins.datagen;

import io.github.apace100.origins.neoforge.OriginsNeoForge;
import io.github.apace100.origins.common.registry.ModBlocks;
import io.github.apace100.origins.common.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class OriginsLanguageProvider extends LanguageProvider {

    public OriginsLanguageProvider(PackOutput output, String locale) {
        super(output, OriginsNeoForge.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.ORIGIN_STONE.get(), "Origin Stone");
        add(ModItems.ORB_OF_ORIGIN.get(), "Orb of Origin");

        add("commands.origins.reload_soon", "Reload scheduled - datapack changes will apply soon.");
        add("commands.origins.list.empty", "No origins are currently registered.");
        add("commands.origins.list.header", "Available origins: %s");
        add("commands.origins.list.entry", "%s (%s) - %s powers");
        add("commands.origins.set", "Set %s's origin to %s");
        add("commands.origins.reset", "Cleared origin for %s");
        add("commands.origins.error.unknown", "Unknown origin: %s");

        add("screen.origins.select_origin", "Choose Your Origin");
        add("screen.origins.origin", "Origin");
        add("screen.origins.confirm", "Confirm");
        add("screen.origins.reset", "Reset Origin");
        add("screen.origins.none", "No Origin");
        add("screen.origins.no_selection", "No origin selected");

        add("key.categories.origins", "Origins");
        add("key.origins.open_selection", "Open Origin Selection");
    }
}
