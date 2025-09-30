package io.github.apace100.origins.platform.neoforge.init;

import io.github.apace100.origins.OriginsNeoForge;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(Registries.BLOCK, OriginsNeoForge.MOD_ID);

    public static final RegistryObject<Block> PLACEHOLDER = REGISTER.register(
            "placeholder_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    private ModBlocks() {
    }
}
