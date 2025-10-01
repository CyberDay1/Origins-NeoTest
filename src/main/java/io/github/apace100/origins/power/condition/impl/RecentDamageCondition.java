package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Datapack condition that checks if a living entity has taken damage recently.
 */
public final class RecentDamageCondition implements Condition<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "recent_damage");
    private static final Map<LivingEntity, Long> LAST_DAMAGE_TICKS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Codec<RecentDamageCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("seconds").forGetter(RecentDamageCondition::seconds)
    ).apply(instance, RecentDamageCondition::new));

    private final int seconds;

    private RecentDamageCondition(int seconds) {
        this.seconds = seconds;
    }

    private int seconds() {
        return seconds;
    }

    @Override
    public boolean test(LivingEntity entity) {
        if (entity == null || seconds <= 0) {
            return false;
        }
        Level level = entity.level();
        if (level == null) {
            return false;
        }

        long gameTime = level.getGameTime();
        if (entity.getLastDamageSource() != null) {
            LAST_DAMAGE_TICKS.put(entity, gameTime);
        }

        Long lastTick = LAST_DAMAGE_TICKS.get(entity);
        if (lastTick == null) {
            return false;
        }
        long threshold = seconds * 20L;
        return gameTime - lastTick <= threshold;
    }

    public static RecentDamageCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("seconds")) {
            Origins.LOGGER.warn("Recent damage condition '{}' is missing required 'seconds' field", id);
            return null;
        }

        int seconds;
        try {
            seconds = GsonHelper.getAsInt(json, "seconds");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Recent damage condition '{}' has invalid 'seconds': {}", id, exception.getMessage());
            return null;
        }

        if (seconds <= 0) {
            Origins.LOGGER.warn("Recent damage condition '{}' specified non-positive seconds {}", id, seconds);
            return null;
        }

        return new RecentDamageCondition(seconds);
    }

    public static Codec<RecentDamageCondition> codec() {
        return CODEC;
    }
}
