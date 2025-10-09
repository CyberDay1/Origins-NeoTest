package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

import java.util.List;

/**
 * Scaffold implementation for time of day datapack conditions.
 */
public final class TimeOfDayCondition implements Condition<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("time_of_day");
    private static final Codec<List<Integer>> RANGE_CODEC = Codec.INT.listOf().flatXmap(TimeOfDayCondition::validateRange, TimeOfDayCondition::validateRange);
    private static final Codec<TimeOfDayCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RANGE_CODEC.fieldOf("range").forGetter(TimeOfDayCondition::range)
    ).apply(instance, range -> new TimeOfDayCondition(range.get(0), range.get(1))));

    private final int startTime;
    private final int endTime;

    private TimeOfDayCondition(int startTime, int endTime) {
        this.startTime = normalize(startTime);
        this.endTime = normalize(endTime);
    }

    public int startTime() {
        return startTime;
    }

    public int endTime() {
        return endTime;
    }

    @Override
    public boolean test(ServerLevel level) {
        if (level == null) {
            return false;
        }

        int time = (int) (level.getDayTime() % 24000L);
        if (time < 0) {
            time += 24000;
        }

        if (startTime <= endTime) {
            return time >= startTime && time <= endTime;
        }
        return time >= startTime || time <= endTime;
    }

    public static TimeOfDayCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("range")) {
            Origins.LOGGER.warn("Time of day condition '{}' is missing required 'range' field", id);
            return null;
        }

        JsonArray range = GsonHelper.getAsJsonArray(json, "range");
        if (range.size() != 2) {
            Origins.LOGGER.warn("Time of day condition '{}' range must contain exactly 2 elements", id);
            return null;
        }

        int start;
        int end;
        try {
            start = GsonHelper.convertToInt(range.get(0), "range[0]");
            end = GsonHelper.convertToInt(range.get(1), "range[1]");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Time of day condition '{}' has invalid range values: {}", id, exception.getMessage());
            return null;
        }

        if (start < 0 || end < 0) {
            Origins.LOGGER.warn("Time of day condition '{}' has negative range ({}, {})", id, start, end);
            return null;
        }

        return new TimeOfDayCondition(start, end);
    }

    public static Codec<TimeOfDayCondition> codec() {
        return CODEC;
    }

    private List<Integer> range() {
        return List.of(startTime, endTime);
    }

    private static DataResult<List<Integer>> validateRange(List<Integer> values) {
        if (values.size() != 2) {
            return DataResult.error(() -> "Time of day range must have exactly 2 values");
        }
        if (values.get(0) < 0 || values.get(1) < 0) {
            return DataResult.error(() -> "Time of day range values must be non-negative");
        }
        return DataResult.success(values);
    }

    private static int normalize(int value) {
        int wrapped = value % 24000;
        if (wrapped < 0) {
            wrapped += 24000;
        }
        return wrapped;
    }
}
