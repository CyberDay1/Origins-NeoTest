package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

/**
 * Scaffold implementation for food datapack conditions.
 */
public final class FoodCondition implements Condition<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "food");
    private static final Codec<FoodCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("min", 0).forGetter(FoodCondition::min),
        Codec.INT.optionalFieldOf("max", 20).forGetter(FoodCondition::max)
    ).apply(instance, FoodCondition::new));

    private final int min;
    private final int max;

    private FoodCondition(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    @Override
    public boolean test(Player player) {
        // TODO: Compare the player's hunger level against the configured bounds.
        return false;
    }

    public static FoodCondition fromJson(ResourceLocation id, JsonObject json) {
        int min = GsonHelper.getAsInt(json, "min", 0);
        int max = GsonHelper.getAsInt(json, "max", 20);
        if (min < 0 || max < 0) {
            Origins.LOGGER.warn("Food condition '{}' has negative hunger bounds ({}, {})", id, min, max);
            return null;
        }
        if (min > max) {
            Origins.LOGGER.warn("Food condition '{}' has inverted hunger bounds ({}, {})", id, min, max);
            return null;
        }
        return new FoodCondition(min, max);
    }

    public static Codec<FoodCondition> codec() {
        return CODEC;
    }
}
