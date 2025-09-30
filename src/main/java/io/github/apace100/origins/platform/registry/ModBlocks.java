package io.github.apace100.origins.platform.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "origins");

    public static final DeferredHolder<Block, Block> ORIGIN_STONE = BLOCKS.register(
        "origin_stone",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE))
    );
}
