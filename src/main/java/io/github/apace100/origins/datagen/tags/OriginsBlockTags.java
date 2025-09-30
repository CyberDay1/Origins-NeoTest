package io.github.apace100.origins.datagen.tags;

import io.github.apace100.origins.common.registry.ModBlocks;
import io.github.apace100.origins.neoforge.OriginsNeoForge;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OriginsBlockTags extends BlockTagsProvider {

    public OriginsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, OriginsNeoForge.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ORIGIN_STONE.get());
    }
}
