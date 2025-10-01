package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

/**
 * Datapack condition that checks if an entity is within a configured Y-level range.
 */
public final class YLevelCondition implements Condition<Entity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "y_level");
    private static final Codec<YLevelCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.optionalFieldOf("min", Double.NEGATIVE_INFINITY).forGetter(YLevelCondition::minY),
        Codec.DOUBLE.optionalFieldOf("max", Double.POSITIVE_INFINITY).forGetter(YLevelCondition::maxY)
    ).apply(instance, YLevelCondition::new));

    private final double minY;
    private final double maxY;

    private YLevelCondition(double minY, double maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    private double minY() {
        return minY;
    }

    private double maxY() {
        return maxY;
    }

    @Override
    public boolean test(Entity entity) {
        if (entity == null) {
            return false;
        }
        double y = entity.getY();
        return y >= minY && y <= maxY;
    }

    public static YLevelCondition fromJson(ResourceLocation id, JsonObject json) {
        double min = Double.NEGATIVE_INFINITY;
        double max = Double.POSITIVE_INFINITY;

        if (json.has("min")) {
            try {
                min = GsonHelper.getAsDouble(json, "min");
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Y-level condition '{}' has invalid 'min': {}", id, exception.getMessage());
                return null;
            }
        }

        if (json.has("max")) {
            try {
                max = GsonHelper.getAsDouble(json, "max");
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Y-level condition '{}' has invalid 'max': {}", id, exception.getMessage());
                return null;
            }
        }

        if (max < min) {
            Origins.LOGGER.warn("Y-level condition '{}' has max ({}) smaller than min ({})", id, max, min);
            return null;
        }

        return new YLevelCondition(min, max);
    }

    public static Codec<YLevelCondition> codec() {
        return CODEC;
    }
}
