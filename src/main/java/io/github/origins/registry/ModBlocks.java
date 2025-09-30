package io.github.origins.registry;

import io.github.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, Origins.MOD_ID);
    public static final RegistryObject<Block> ORIGIN_STONE = REGISTRY.register(
        "origin_stone",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE))
    );

    private ModBlocks() {
    }
}
