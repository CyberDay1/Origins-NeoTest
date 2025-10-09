package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Datapack condition that checks whether a player is attempting to sleep
 * under a configured set of constraints.
 */
public final class SleepCondition implements Condition<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("sleep");
    private static final ThreadLocal<SleepAttempt> CONTEXT = new ThreadLocal<>();
    private static final Codec<SleepCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("dimension")
            .forGetter(condition -> condition.dimension.map(ResourceKey::location)),
        Codec.INT.optionalFieldOf("min_y").forGetter(SleepCondition::minY)
    ).apply(instance, (dimension, minY) -> new SleepCondition(
        dimension.map(id -> ResourceKey.create(Registries.DIMENSION, id)),
        minY
    )));

    private final Optional<ResourceKey<Level>> dimension;
    private final Optional<Integer> minY;

    private SleepCondition(Optional<ResourceKey<Level>> dimension, Optional<Integer> minY) {
        this.dimension = dimension;
        this.minY = minY;
    }

    private Optional<ResourceKey<Level>> dimension() {
        return dimension;
    }

    private Optional<Integer> minY() {
        return minY;
    }

    @Override
    public boolean test(Player player) {
        if (player == null) {
            return false;
        }

        SleepAttempt attempt = CONTEXT.get();
        if (attempt == null) {
            return false;
        }

        ResourceKey<Level> dimension = attempt.dimension();
        if (dimension == null) {
            dimension = player.level().dimension();
        }
        if (this.dimension.isPresent() && !this.dimension.get().equals(dimension)) {
            return false;
        }

        int y = attempt.position().map(BlockPos::getY).orElse(player.blockPosition().getY());
        if (minY.isPresent() && y < minY.get()) {
            return false;
        }

        return true;
    }

    public static SleepCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceKey<Level>> dimension = Optional.empty();
        if (json.has("dimension")) {
            String raw = GsonHelper.getAsString(json, "dimension", "");
            if (!raw.isEmpty()) {
                try {
                    ResourceLocation dimensionId = ResourceLocation.parse(raw);
                    dimension = Optional.of(ResourceKey.create(Registries.DIMENSION, dimensionId));
                } catch (IllegalArgumentException exception) {
                    Origins.LOGGER.warn("Sleep condition '{}' has invalid dimension '{}': {}", id, raw, exception.getMessage());
                    return null;
                }
            }
        }

        Optional<Integer> minY = Optional.empty();
        if (json.has("min_y")) {
            try {
                minY = Optional.of(GsonHelper.getAsInt(json, "min_y"));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Sleep condition '{}' has invalid 'min_y': {}", id, exception.getMessage());
                return null;
            }
        }

        return new SleepCondition(dimension, minY);
    }

    public static Codec<SleepCondition> codec() {
        return CODEC;
    }

    public static void withContext(ResourceKey<Level> dimension, BlockPos pos, Runnable runnable) {
        CONTEXT.set(new SleepAttempt(dimension, Optional.ofNullable(pos)));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    private record SleepAttempt(ResourceKey<Level> dimension, Optional<BlockPos> position) {
    }
}
