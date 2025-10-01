package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Datapack action that applies a knockback impulse to an entity.
 */
public final class KnockbackAction implements Action<Entity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "knockback");
    private static final Codec<Vec3> DIRECTION_CODEC = Vec3.CODEC.flatXmap(KnockbackAction::validateDirection, KnockbackAction::validateDirection);
    private static final Codec<KnockbackAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("strength").forGetter(KnockbackAction::strength),
        DIRECTION_CODEC.fieldOf("direction").forGetter(KnockbackAction::direction)
    ).apply(instance, KnockbackAction::new));

    private final float strength;
    private final Vec3 direction;

    private KnockbackAction(float strength, Vec3 direction) {
        this.strength = strength;
        this.direction = direction.normalize();
    }

    private float strength() {
        return strength;
    }

    private Vec3 direction() {
        return direction;
    }

    @Override
    public void execute(Entity entity) {
        if (entity == null || strength <= 0) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel)) {
            return;
        }

        Vec3 impulse = direction.scale(strength);
        entity.push(impulse.x, impulse.y, impulse.z);
    }

    public static KnockbackAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("strength")) {
            Origins.LOGGER.warn("Knockback action '{}' is missing required 'strength' field", id);
            return null;
        }
        if (!json.has("direction")) {
            Origins.LOGGER.warn("Knockback action '{}' is missing required 'direction' array", id);
            return null;
        }

        float strength;
        try {
            strength = GsonHelper.getAsFloat(json, "strength");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Knockback action '{}' has invalid 'strength': {}", id, exception.getMessage());
            return null;
        }

        if (strength <= 0) {
            Origins.LOGGER.warn("Knockback action '{}' specified non-positive strength {}", id, strength);
            return null;
        }

        JsonArray array = GsonHelper.getAsJsonArray(json, "direction");
        if (array.size() != 3) {
            Origins.LOGGER.warn("Knockback action '{}' direction must contain exactly 3 elements", id);
            return null;
        }

        double x;
        double y;
        double z;
        try {
            x = GsonHelper.convertToDouble(array.get(0), "direction[0]");
            y = GsonHelper.convertToDouble(array.get(1), "direction[1]");
            z = GsonHelper.convertToDouble(array.get(2), "direction[2]");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Knockback action '{}' has invalid direction values: {}", id, exception.getMessage());
            return null;
        }

        Vec3 direction = new Vec3(x, y, z);
        if (direction.lengthSqr() == 0) {
            Origins.LOGGER.warn("Knockback action '{}' specified a zero-length direction", id);
            return null;
        }

        return new KnockbackAction(strength, direction);
    }

    public static Codec<KnockbackAction> codec() {
        return CODEC;
    }

    private static DataResult<Vec3> validateDirection(Vec3 direction) {
        if (direction == null || direction.lengthSqr() == 0) {
            return DataResult.error(() -> "Knockback direction must not be zero");
        }
        return DataResult.success(direction.normalize());
    }
}
