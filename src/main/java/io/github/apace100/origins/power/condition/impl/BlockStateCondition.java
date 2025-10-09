package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Scaffold implementation for block state datapack conditions.
 */
public final class BlockStateCondition implements Condition<BlockState> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("block_state");
    private static final Codec<BlockStateCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockStateCondition::block)
    ).apply(instance, BlockStateCondition::new));

    private final Block block;

    private BlockStateCondition(Block block) {
        this.block = block;
    }

    public Block block() {
        return block;
    }

    @Override
    public boolean test(BlockState state) {
        return state != null && state.is(block);
    }

    public static BlockStateCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("block")) {
            Origins.LOGGER.warn("Block state condition '{}' is missing required 'block' field", id);
            return null;
        }

        String raw = GsonHelper.getAsString(json, "block");
        ResourceLocation blockId;
        try {
            blockId = ResourceLocation.parse(raw);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Block state condition '{}' has invalid block id '{}': {}", id, raw, exception.getMessage());
            return null;
        }

        Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(blockId);
        if (block.isEmpty()) {
            Origins.LOGGER.warn("Block state condition '{}' references unknown block '{}'", id, blockId);
            return null;
        }

        return new BlockStateCondition(block.get());
    }

    public static Codec<BlockStateCondition> codec() {
        return CODEC;
    }
}
