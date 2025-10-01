package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class OriginsBlockTags extends BlockTagsProvider {
    public OriginsBlockTags(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper helper) {
        super(out, lookup, Origins.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ORIGIN_STONE.get());
    }

    public CompletableFuture<TagsProvider.TagLookup<Block>> contentsGetter() {
        return super.contentsGetter();
    }
}
