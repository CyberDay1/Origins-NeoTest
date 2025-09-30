package io.github.apace100.origins.registry;

import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OriginsConstants.MODID);

    public static final DeferredBlock<Block> ORIGIN_STONE = BLOCKS.register("origin_stone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .mapColor(MapColor.COLOR_CYAN)
        .strength(1.5F, 6.0F)
        .sound(SoundType.STONE)
        .pushReaction(PushReaction.NORMAL)));

    private OriginsBlocks() {
    }
}
