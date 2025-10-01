package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Scaffold implementation for block state datapack conditions.
 */
public final class BlockStateCondition implements Condition<BlockState> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "block_state");
    private static final Codec<BlockStateCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("block").forGetter(BlockStateCondition::blockId)
    ).apply(instance, BlockStateCondition::new));

    private final Optional<ResourceLocation> blockId;

    private BlockStateCondition(Optional<ResourceLocation> blockId) {
        this.blockId = blockId;
    }

    public Optional<ResourceLocation> blockId() {
        return blockId;
    }

    @Override
    public boolean test(BlockState state) {
        // TODO: Inspect the supplied block state for registry and property matches.
        return false;
    }

    public static BlockStateCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("block")) {
            String raw = GsonHelper.getAsString(json, "block");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Block state condition '{}' has invalid block id '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new BlockStateCondition(parsed);
    }

    public static Codec<BlockStateCondition> codec() {
        return CODEC;
    }
}
