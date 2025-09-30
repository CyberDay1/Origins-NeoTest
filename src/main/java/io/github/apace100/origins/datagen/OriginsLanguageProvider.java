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
    }
}
