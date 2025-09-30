package io.github.apace100.origins.datagen.provider;

import io.github.apace100.origins.registry.OriginsBlocks;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class OriginsBlockTags extends BlockTagsProvider {
    public OriginsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper helper) {
        super(output, lookup, OriginsConstants.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OriginsBlocks.ORIGIN_STONE.get());
    }
}
