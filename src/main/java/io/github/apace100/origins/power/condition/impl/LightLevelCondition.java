package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.LightLayer;

import java.util.Optional;

/**
 * Datapack condition that checks the block light level at a configured position.
 */
public final class LightLevelCondition implements Condition<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "light_level");
    private static final Codec<LightLevelCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("min", 0).forGetter(LightLevelCondition::minLight),
        Codec.INT.optionalFieldOf("max", 15).forGetter(LightLevelCondition::maxLight),
        BlockPos.CODEC.optionalFieldOf("pos").forGetter(LightLevelCondition::position)
    ).apply(instance, LightLevelCondition::new));

    private final int minLight;
    private final int maxLight;
    private final Optional<BlockPos> position;

    private LightLevelCondition(int minLight, int maxLight, Optional<BlockPos> position) {
        this.minLight = minLight;
        this.maxLight = maxLight;
        this.position = position;
    }

    private int minLight() {
        return minLight;
    }

    private int maxLight() {
        return maxLight;
    }

    private Optional<BlockPos> position() {
        return position;
    }

    @Override
    public boolean test(ServerLevel level) {
        if (level == null) {
            return false;
        }
        BlockPos pos = position.orElse(level.getSharedSpawnPos());
        int light = level.getBrightness(LightLayer.BLOCK, pos);
        return light >= minLight && light <= maxLight;
    }

    public static LightLevelCondition fromJson(ResourceLocation id, JsonObject json) {
        int min = 0;
        int max = 15;
        if (json.has("min")) {
            try {
                min = GsonHelper.getAsInt(json, "min");
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Light level condition '{}' has invalid 'min': {}", id, exception.getMessage());
                return null;
            }
        }
        if (json.has("max")) {
            try {
                max = GsonHelper.getAsInt(json, "max");
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Light level condition '{}' has invalid 'max': {}", id, exception.getMessage());
                return null;
            }
        }

        if (min < 0 || max > 15) {
            Origins.LOGGER.warn("Light level condition '{}' values must be between 0 and 15 (found {}-{})", id, min, max);
            return null;
        }
        if (max < min) {
            Origins.LOGGER.warn("Light level condition '{}' has max ({}) smaller than min ({})", id, max, min);
            return null;
        }

        Optional<BlockPos> position = Optional.empty();
        if (json.has("pos")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "pos");
            if (array.size() != 3) {
                Origins.LOGGER.warn("Light level condition '{}' position must contain exactly 3 elements", id);
                return null;
            }
            try {
                int x = GsonHelper.convertToInt(array.get(0), "pos[0]");
                int y = GsonHelper.convertToInt(array.get(1), "pos[1]");
                int z = GsonHelper.convertToInt(array.get(2), "pos[2]");
                position = Optional.of(new BlockPos(x, y, z));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Light level condition '{}' has invalid position values: {}", id, exception.getMessage());
                return null;
            }
        }

        return new LightLevelCondition(min, max, position);
    }

    public static Codec<LightLevelCondition> codec() {
        return CODEC;
    }
}
