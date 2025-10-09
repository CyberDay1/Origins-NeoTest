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
 * Datapack condition that evaluates whether a block state matches a configured block type.
 */
public final class PreventBlockUseCondition implements Condition<BlockState> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_block_use");
    private static final Codec<PreventBlockUseCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(PreventBlockUseCondition::block)
    ).apply(instance, PreventBlockUseCondition::new));

    private final Block block;

    private PreventBlockUseCondition(Block block) {
        this.block = block;
    }

    private Block block() {
        return block;
    }

    @Override
    public boolean test(BlockState state) {
        return state != null && state.is(block);
    }

    public static PreventBlockUseCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("block")) {
            Origins.LOGGER.warn("Prevent block use condition '{}' is missing required 'block' field", id);
            return null;
        }

        String raw = GsonHelper.getAsString(json, "block");
        ResourceLocation blockId;
        try {
            blockId = ResourceLocation.parse(raw);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent block use condition '{}' has invalid block id '{}': {}", id, raw, exception.getMessage());
            return null;
        }

        Optional<Block> resolved = BuiltInRegistries.BLOCK.getOptional(blockId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Prevent block use condition '{}' references unknown block '{}'", id, blockId);
            return null;
        }

        return new PreventBlockUseCondition(resolved.get());
    }

    public static Codec<PreventBlockUseCondition> codec() {
        return CODEC;
    }
}
