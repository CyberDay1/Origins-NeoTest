package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

import java.util.Optional;

/**
 * Datapack condition that evaluates a player's hunger and saturation values.
 */
public final class FoodCondition implements Condition<Player> {
    private static final double DEFAULT_HUNGER_MIN = 0.0D;
    private static final double DEFAULT_HUNGER_MAX = 20.0D;
    private static final double DEFAULT_SATURATION_MIN = 0.0D;
    private static final double DEFAULT_SATURATION_MAX = 20.0D;

    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "food");
    private static final Range DEFAULT_RANGE = new Range(Optional.empty(), Optional.empty());
    private static final Codec<Range> HUNGER_RANGE_CODEC = createRangeCodec(DEFAULT_HUNGER_MIN, DEFAULT_HUNGER_MAX);
    private static final Codec<Range> SATURATION_RANGE_CODEC = createRangeCodec(DEFAULT_SATURATION_MIN, DEFAULT_SATURATION_MAX);
    private static final Codec<FoodCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        HUNGER_RANGE_CODEC.optionalFieldOf("hunger", DEFAULT_RANGE).forGetter(FoodCondition::hungerRange),
        SATURATION_RANGE_CODEC.optionalFieldOf("saturation", DEFAULT_RANGE).forGetter(FoodCondition::saturationRange)
    ).apply(instance, FoodCondition::new));

    private final Range hungerRange;
    private final Range saturationRange;

    private FoodCondition(Range hungerRange, Range saturationRange) {
        this.hungerRange = hungerRange;
        this.saturationRange = saturationRange;
    }

    private Range hungerRange() {
        return hungerRange;
    }

    private Range saturationRange() {
        return saturationRange;
    }

    @Override
    public boolean test(Player player) {
        if (player == null) {
            return false;
        }
        FoodData data = player.getFoodData();
        int hunger = data.getFoodLevel();
        float saturation = data.getSaturationLevel();
        return hungerRange.contains(hunger, DEFAULT_HUNGER_MIN, DEFAULT_HUNGER_MAX)
            && saturationRange.contains(saturation, DEFAULT_SATURATION_MIN, DEFAULT_SATURATION_MAX);
    }

    public static FoodCondition fromJson(ResourceLocation id, JsonObject json) {
        Range hunger = parseRange(id, json, "hunger", DEFAULT_HUNGER_MIN, DEFAULT_HUNGER_MAX);
        if (hunger == null) {
            return null;
        }
        Range saturation = parseRange(id, json, "saturation", DEFAULT_SATURATION_MIN, DEFAULT_SATURATION_MAX);
        if (saturation == null) {
            return null;
        }
        return new FoodCondition(hunger, saturation);
    }

    public static Codec<FoodCondition> codec() {
        return CODEC;
    }

    private static Range parseRange(ResourceLocation id, JsonObject json, String key, double defaultMin, double defaultMax) {
        if (!json.has(key)) {
            return DEFAULT_RANGE;
        }
        JsonObject rangeJson;
        try {
            rangeJson = GsonHelper.getAsJsonObject(json, key);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Food condition '{}' has malformed '{}' object: {}", id, key, exception.getMessage());
            return null;
        }

        Optional<Double> min = rangeJson.has("min") ? Optional.of(GsonHelper.getAsDouble(rangeJson, "min")) : Optional.empty();
        Optional<Double> max = rangeJson.has("max") ? Optional.of(GsonHelper.getAsDouble(rangeJson, "max")) : Optional.empty();

        if (min.isPresent() && min.get() < defaultMin) {
            Origins.LOGGER.warn("Food condition '{}' {} minimum {} is below allowed minimum {}", id, key, min.get(), defaultMin);
            return null;
        }
        if (max.isPresent() && max.get() > defaultMax) {
            Origins.LOGGER.warn("Food condition '{}' {} maximum {} exceeds allowed maximum {}", id, key, max.get(), defaultMax);
            return null;
        }
        if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
            Origins.LOGGER.warn("Food condition '{}' {} range is inverted ({}, {})", id, key, min.get(), max.get());
            return null;
        }

        return new Range(min, max);
    }

    private static Codec<Range> createRangeCodec(double defaultMin, double defaultMax) {
        return RecordCodecBuilder.<Range>create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("min").forGetter(Range::min),
            Codec.DOUBLE.optionalFieldOf("max").forGetter(Range::max)
        ).apply(instance, Range::new)).flatXmap(
            range -> validateRange(range, defaultMin, defaultMax),
            range -> validateRange(range, defaultMin, defaultMax)
        );
    }

    private static DataResult<Range> validateRange(Range range, double defaultMin, double defaultMax) {
        Optional<Double> min = range.min();
        Optional<Double> max = range.max();
        if (min.isPresent() && min.get() < defaultMin) {
            return DataResult.error(() -> "Range minimum " + min.get() + " is below allowed minimum " + defaultMin);
        }
        if (max.isPresent() && max.get() > defaultMax) {
            return DataResult.error(() -> "Range maximum " + max.get() + " exceeds allowed maximum " + defaultMax);
        }
        if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
            return DataResult.error(() -> "Range minimum " + min.get() + " is greater than maximum " + max.get());
        }
        return DataResult.success(range);
    }

    private record Range(Optional<Double> min, Optional<Double> max) {
        boolean contains(double value, double defaultMin, double defaultMax) {
            double lower = min.orElse(defaultMin);
            double upper = max.orElse(defaultMax);
            return value >= lower && value <= upper;
        }
    }
}
