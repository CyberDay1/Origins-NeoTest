package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

/**
 * Datapack condition that checks the current weather state.
 */
public final class WeatherCondition implements Condition<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "weather");
    private static final Codec<WeatherCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("raining").forGetter(condition -> condition.raining),
        Codec.BOOL.optionalFieldOf("thundering").forGetter(condition -> condition.thundering)
    ).apply(instance, WeatherCondition::new));

    private final Optional<Boolean> raining;
    private final Optional<Boolean> thundering;

    private WeatherCondition(Optional<Boolean> raining, Optional<Boolean> thundering) {
        this.raining = raining;
        this.thundering = thundering;
    }

    @Override
    public boolean test(ServerLevel level) {
        if (level == null) {
            return false;
        }
        if (raining.isPresent() && level.isRaining() != raining.get()) {
            return false;
        }
        return thundering.isEmpty() || level.isThundering() == thundering.get();
    }

    public static WeatherCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<Boolean> raining = Optional.empty();
        Optional<Boolean> thundering = Optional.empty();

        if (json.has("raining")) {
            try {
                raining = Optional.of(GsonHelper.getAsBoolean(json, "raining"));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Weather condition '{}' has invalid 'raining' value: {}", id, exception.getMessage());
                return null;
            }
        }

        if (json.has("thundering")) {
            try {
                thundering = Optional.of(GsonHelper.getAsBoolean(json, "thundering"));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Weather condition '{}' has invalid 'thundering' value: {}", id, exception.getMessage());
                return null;
            }
        }

        if (raining.isEmpty() && thundering.isEmpty()) {
            Origins.LOGGER.warn("Weather condition '{}' must specify at least one of 'raining' or 'thundering'", id);
            return null;
        }

        return new WeatherCondition(raining, thundering);
    }

    public static Codec<WeatherCondition> codec() {
        return CODEC;
    }
}
