package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.init.OriginsBlocks;
import io.github.apace100.origins.init.OriginsItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

final class OriginsEnglishLanguageProvider extends LanguageProvider {
    OriginsEnglishLanguageProvider(PackOutput output) {
        super(output, Origins.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(OriginsItems.ORB_OF_ORIGIN.get(), "Orb of Origin");
        add(OriginsBlocks.ORIGIN_STONE.get(), "Origin Stone");
        add("commands.origins.reload_soon", "Origins reload is coming soon");
    }
}
