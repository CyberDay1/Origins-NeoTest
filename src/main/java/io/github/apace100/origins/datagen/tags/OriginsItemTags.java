package io.github.apace100.origins.datagen.tags;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OriginsItemTags extends ItemTagsProvider {

    public OriginsItemTags(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookupProvider,
        CompletableFuture<TagLookup<Block>> blockTags,
        ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, Origins.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.BOOKSHELF_BOOKS).add(ModItems.ORB_OF_ORIGIN.get());
    }
}
