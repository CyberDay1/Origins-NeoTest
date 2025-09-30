package io.github.apace100.origins.datagen.provider;

import io.github.apace100.origins.registry.OriginsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.loot.BlockLootSubProvider;

import java.util.List;
import java.util.Set;

public class OriginsBlockLootTables extends BlockLootSubProvider {
    public OriginsBlockLootTables(HolderLookup.Provider lookup) {
        super(Set.<Item>of(), FeatureFlags.REGISTRY.allFlags(), lookup);
    }

    @Override
    protected void generate() {
        dropSelf(OriginsBlocks.ORIGIN_STONE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(OriginsBlocks.ORIGIN_STONE.get());
    }
}
