package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

/**
 * Scaffold implementation for time of day datapack conditions.
 */
public final class TimeOfDayCondition implements Condition<Level> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "time_of_day");
    private static final Codec<TimeOfDayCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("start", 0).forGetter(TimeOfDayCondition::startTime),
        Codec.INT.optionalFieldOf("end", 24000).forGetter(TimeOfDayCondition::endTime)
    ).apply(instance, TimeOfDayCondition::new));

    private final int startTime;
    private final int endTime;

    private TimeOfDayCondition(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int startTime() {
        return startTime;
    }

    public int endTime() {
        return endTime;
    }

    @Override
    public boolean test(Level level) {
        // TODO: Compare against world time respecting wrap-around semantics.
        return false;
    }

    public static TimeOfDayCondition fromJson(ResourceLocation id, JsonObject json) {
        int start = GsonHelper.getAsInt(json, "start", 0);
        int end = GsonHelper.getAsInt(json, "end", 24000);
        if (start < 0 || end < 0) {
            Origins.LOGGER.warn("Time of day condition '{}' has negative range ({}, {})", id, start, end);
            return null;
        }
        return new TimeOfDayCondition(start, end);
    }

    public static Codec<TimeOfDayCondition> codec() {
        return CODEC;
    }
}
