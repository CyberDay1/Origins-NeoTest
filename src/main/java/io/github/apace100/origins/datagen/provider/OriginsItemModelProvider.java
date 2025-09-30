package io.github.apace100.origins.datagen.provider;

import io.github.apace100.origins.registry.OriginsItems;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OriginsItemModelProvider extends ItemModelProvider {
    public OriginsItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, OriginsConstants.MODID, helper);
    }

    @Override
    protected void registerModels() {
        basicItem(OriginsItems.ORB_OF_ORIGIN.get());
        basicItem(OriginsItems.ORIGIN_STONE.get());
    }
}
