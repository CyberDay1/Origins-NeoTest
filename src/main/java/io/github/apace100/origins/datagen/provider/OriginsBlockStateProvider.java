package io.github.apace100.origins.datagen.provider;

import io.github.apace100.origins.registry.OriginsBlocks;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OriginsBlockStateProvider extends BlockStateProvider {
    public OriginsBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, OriginsConstants.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(OriginsBlocks.ORIGIN_STONE.get());
    }
}
