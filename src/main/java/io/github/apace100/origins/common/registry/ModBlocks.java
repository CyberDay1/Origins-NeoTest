package io.github.apace100.origins.common.registry;

import io.github.apace100.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister<Block> REGISTER =
        DeferredRegister.create(Registries.BLOCK, Origins.MOD_ID);

    public static final DeferredHolder<Block, Block> ORIGIN_STONE = REGISTER.register(
        "origin_stone",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE))
    );

    private ModBlocks() {
    }
}
