package com.example.origins;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, OriginsNeoForge.MOD_ID);

    public static final DeferredHolder<Block, Block> EXAMPLE_BLOCK =
            BLOCKS.register(
                    "example_block",
                    () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
}
