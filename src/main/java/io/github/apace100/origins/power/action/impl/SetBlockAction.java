package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Datapack action that replaces a block at the configured position.
 */
public final class SetBlockAction implements Action<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "set_block");
    private static final Codec<SetBlockAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.BLOCK.byNameCodec().xmap(Block::defaultBlockState, BlockState::getBlock).fieldOf("block").forGetter(SetBlockAction::state),
        BlockPos.CODEC.fieldOf("pos").forGetter(SetBlockAction::pos)
    ).apply(instance, SetBlockAction::new));

    private final BlockState state;
    private final BlockPos pos;

    private SetBlockAction(BlockState state, BlockPos pos) {
        this.state = state;
        this.pos = pos;
    }

    private BlockState state() {
        return state;
    }

    private BlockPos pos() {
        return pos;
    }

    @Override
    public void execute(ServerLevel level) {
        if (level == null) {
            return;
        }

        level.setBlockAndUpdate(pos, state);
    }

    public static SetBlockAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("block")) {
            Origins.LOGGER.warn("Set block action '{}' is missing required 'block' field", id);
            return null;
        }
        if (!json.has("pos")) {
            Origins.LOGGER.warn("Set block action '{}' is missing required 'pos' field", id);
            return null;
        }

        String rawBlock = GsonHelper.getAsString(json, "block");
        ResourceLocation blockId;
        try {
            blockId = ResourceLocation.parse(rawBlock);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Set block action '{}' has invalid block id '{}': {}", id, rawBlock, exception.getMessage());
            return null;
        }

        Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(blockId);
        if (block.isEmpty()) {
            Origins.LOGGER.warn("Set block action '{}' references unknown block '{}'", id, blockId);
            return null;
        }

        JsonArray array = GsonHelper.getAsJsonArray(json, "pos");
        if (array.size() != 3) {
            Origins.LOGGER.warn("Set block action '{}' position must contain exactly 3 elements", id);
            return null;
        }

        int x;
        int y;
        int z;
        try {
            x = GsonHelper.convertToInt(array.get(0), "pos[0]");
            y = GsonHelper.convertToInt(array.get(1), "pos[1]");
            z = GsonHelper.convertToInt(array.get(2), "pos[2]");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Set block action '{}' has invalid position values: {}", id, exception.getMessage());
            return null;
        }

        return new SetBlockAction(block.get().defaultBlockState(), new BlockPos(x, y, z));
    }

    public static Codec<SetBlockAction> codec() {
        return CODEC;
    }
}
