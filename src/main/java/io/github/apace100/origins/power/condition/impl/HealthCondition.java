package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

/**
 * Datapack condition that validates a living entity's health against a configured range.
 */
public final class HealthCondition implements Condition<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("health");

    private final float minHealth;
    private final float maxHealth;

    private HealthCondition(float minHealth, float maxHealth) {
        this.minHealth = minHealth;
        this.maxHealth = maxHealth;
    }

    @Override
    public boolean test(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        float health = entity.getHealth();
        return health >= minHealth && health <= maxHealth;
    }

    public static HealthCondition fromJson(ResourceLocation id, JsonObject json) {
        float min = 0.0F;
        float max = Float.MAX_VALUE;

        if (json.has("min")) {
            try {
                min = GsonHelper.getAsFloat(json, "min");
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Health condition '{}' has invalid 'min' value: {}", id, exception.getMessage());
                return null;
            }
        }

        if (json.has("max")) {
            try {
                max = GsonHelper.getAsFloat(json, "max");
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Health condition '{}' has invalid 'max' value: {}", id, exception.getMessage());
                return null;
            }
        }

        if (max < min) {
            Origins.LOGGER.warn("Health condition '{}' has max ({}) smaller than min ({})", id, max, min);
            return null;
        }

        return new HealthCondition(min, max);
    }
}
