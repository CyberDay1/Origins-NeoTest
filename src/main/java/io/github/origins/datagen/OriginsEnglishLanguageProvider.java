package io.github.origins.datagen;

import io.github.origins.Origins;
import io.github.origins.registry.ModBlocks;
import io.github.origins.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

final class OriginsEnglishLanguageProvider extends LanguageProvider {
    OriginsEnglishLanguageProvider(PackOutput output) {
        super(output, Origins.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(ModItems.ORB_OF_ORIGIN, "Orb of Origin");
        add(ModBlocks.ORIGIN_STONE.get(), "Origin Stone");
        add("commands.origins.reload_soon", "Origins reload is coming soon");
    }
}
