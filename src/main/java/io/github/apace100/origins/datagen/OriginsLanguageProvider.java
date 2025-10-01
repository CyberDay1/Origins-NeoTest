package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class OriginsLanguageProvider extends LanguageProvider {
    public OriginsLanguageProvider(PackOutput out) { super(out, Origins.MOD_ID, "en_us"); }

    @Override
    protected void addTranslations() {
        add("itemGroup.origins", "Origins");
        add(ModBlocks.ORIGIN_STONE.get(), "Origin Stone");
        add(ModItems.ORB_OF_ORIGIN.get(), "Orb of Origin");
        add("command.origins.set", "Set origin");
        add("command.origins.clear", "Cleared origin");
        add("screen.origins.selection_confirmed", "Origin set to %s");
        add("screen.origins.origin_cleared", "Origin cleared.");
        add("key.origins.toggle_phantom", "Toggle Phantom Form");
        add("power.origins.phantomize.out_of_hunger", "You are too hungry to remain phantomized.");
        add("power.origins.avian.sleep_restriction", "Avians refuse to sleep above Y=86.");
    }
}
