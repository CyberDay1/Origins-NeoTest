package io.github.apace100.origins.datagen.provider;

import io.github.apace100.origins.registry.OriginsBlocks;
import io.github.apace100.origins.registry.OriginsItems;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class OriginsLanguageProvider extends LanguageProvider {
    public OriginsLanguageProvider(PackOutput output) {
        super(output, OriginsConstants.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(OriginsBlocks.ORIGIN_STONE.get(), "Origin Stone");
        add(OriginsItems.ORB_OF_ORIGIN.get(), "Orb of Origin");
    }
}
