package io.github.apace100.origins.platform.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "origins");

    public static final RegistryObject<Block> ORIGIN_STONE = BLOCKS.register("origin_stone",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
}
