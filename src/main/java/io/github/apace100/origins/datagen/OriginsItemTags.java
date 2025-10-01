package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class OriginsItemTags extends ItemTagsProvider {
    public OriginsItemTags(
        PackOutput out,
        CompletableFuture<HolderLookup.Provider> lookup,
        CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
        ExistingFileHelper helper
    ) {
        super(out, lookup, blockTags, Origins.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.BOOKSHELF_BOOKS).add(ModItems.ORB_OF_ORIGIN.get());
    }
}
