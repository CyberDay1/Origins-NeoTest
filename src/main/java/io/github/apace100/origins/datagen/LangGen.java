package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class LangGen extends LanguageProvider {
    public LangGen(PackOutput output) {
        super(output, Origins.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("item.origins.orb_of_origin", "Orb of Origin");
        add("block.origins.origin_stone", "Origin Stone");
        add("commands.origins.reload_soon", "Origins data reload support is coming soon");
    }
}
