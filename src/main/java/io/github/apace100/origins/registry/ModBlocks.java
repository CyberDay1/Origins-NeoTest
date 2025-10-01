package io.github.apace100.origins.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "origins");

    public static final DeferredHolder<Block, Block> ORIGIN_STONE = BLOCKS.register(
        "origin_stone",
        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 9.0F))
    );
}
