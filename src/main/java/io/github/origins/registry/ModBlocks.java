package io.github.origins.registry;

import io.github.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, Origins.MOD_ID);

    private ModBlocks() {
    }
}
