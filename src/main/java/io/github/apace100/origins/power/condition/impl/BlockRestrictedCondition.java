package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Datapack condition that checks whether a block state belongs to the specified
 * restricted block tag.
 */
public final class BlockRestrictedCondition implements Condition<BlockState> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "block_restricted");
    private static final Codec<BlockRestrictedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("tag").forGetter(condition -> condition.tag.location())
    ).apply(instance, id -> new BlockRestrictedCondition(TagKey.create(Registries.BLOCK, id))));

    private final TagKey<Block> tag;

    private BlockRestrictedCondition(TagKey<Block> tag) {
        this.tag = tag;
    }

    private TagKey<Block> tag() {
        return tag;
    }

    @Override
    public boolean test(BlockState state) {
        return state != null && state.is(tag);
    }

    public static BlockRestrictedCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("tag")) {
            Origins.LOGGER.warn("Block restricted condition '{}' is missing required 'tag' field", id);
            return null;
        }

        String rawTag = GsonHelper.getAsString(json, "tag");
        ResourceLocation tagId;
        try {
            tagId = ResourceLocation.parse(rawTag);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Block restricted condition '{}' has invalid tag id '{}': {}", id, rawTag, exception.getMessage());
            return null;
        }

        return new BlockRestrictedCondition(TagKey.create(Registries.BLOCK, tagId));
    }

    public static Codec<BlockRestrictedCondition> codec() {
        return CODEC;
    }
}
