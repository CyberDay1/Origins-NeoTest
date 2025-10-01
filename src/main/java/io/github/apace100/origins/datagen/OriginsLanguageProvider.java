package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class OriginsLanguageProvider extends LanguageProvider {

    public OriginsLanguageProvider(PackOutput output, String locale) {
        super(output, Origins.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.ORIGIN_STONE.get(), "Origin Stone");
        add(ModItems.ORB_OF_ORIGIN.get(), "Orb of Origin");

        add("commands.origins.list", "Current origin: %s (%s)");
        add("commands.origins.list.empty", "No origin selected.");
        add("commands.origins.set", "Set origin for %s to %s");
        add("commands.origins.clear", "Cleared origin for %s");
        add("commands.origins.error.unknown", "Unknown origin: %s");
        add("commands.origins.error.unavailable", "You already have an origin assigned.");

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
