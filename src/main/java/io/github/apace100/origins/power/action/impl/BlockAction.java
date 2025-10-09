package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Scaffold implementation for block based datapack actions.
 */
public final class BlockAction implements Action<BlockAction.BlockActionContext> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("block");
    private static final Codec<BlockAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("block").forGetter(BlockAction::blockId)
    ).apply(instance, BlockAction::new));

    private final Optional<ResourceLocation> blockId;

    private BlockAction(Optional<ResourceLocation> blockId) {
        this.blockId = blockId;
    }

    public Optional<ResourceLocation> blockId() {
        return blockId;
    }

    @Override
    public void execute(BlockActionContext context) {
        // TODO: Implement Fabric parity behaviour for block actions (set/break/place).
    }

    public static BlockAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("block")) {
            String raw = GsonHelper.getAsString(json, "block");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Failed to parse block action '{}' block id '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new BlockAction(parsed);
    }

    public static Codec<BlockAction> codec() {
        return CODEC;
    }

    /**
     * Simple context wrapper bundling the world and target position.
     */
    public record BlockActionContext(Level level, BlockPos position) {
    }
}
