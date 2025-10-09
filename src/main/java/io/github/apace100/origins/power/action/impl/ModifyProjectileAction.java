package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Datapack action that scales projectile velocity and base damage.
 */
public final class ModifyProjectileAction implements Action<Entity> {
    private static final double MIN_MULTIPLIER = 0.0D;

    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("modify_projectile");
    private static final Codec<ModifyProjectileAction> CODEC = RecordCodecBuilder.<ModifyProjectileAction>create(instance -> instance.group(
        Codec.DOUBLE.optionalFieldOf("speed_multiplier", 1.0D).forGetter(ModifyProjectileAction::speedMultiplier),
        Codec.DOUBLE.optionalFieldOf("damage_multiplier", 1.0D).forGetter(ModifyProjectileAction::damageMultiplier)
    ).apply(instance, (speed, damage) -> new ModifyProjectileAction(speed, damage))).flatXmap(ModifyProjectileAction::validate, ModifyProjectileAction::validate);

    private final double speedMultiplier;
    private final double damageMultiplier;
    private final Optional<ResourceLocation> sourceId;

    private ModifyProjectileAction(double speedMultiplier, double damageMultiplier) {
        this(Optional.empty(), speedMultiplier, damageMultiplier);
    }

    private ModifyProjectileAction(Optional<ResourceLocation> sourceId, double speedMultiplier, double damageMultiplier) {
        this.sourceId = sourceId;
        this.speedMultiplier = speedMultiplier;
        this.damageMultiplier = damageMultiplier;
    }

    private double speedMultiplier() {
        return speedMultiplier;
    }

    private double damageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public void execute(Entity entity) {
        if (!(entity instanceof Projectile projectile)) {
            Origins.LOGGER.warn("Modify projectile action '{}' attempted to run on non-projectile entity {}",
                sourceId.map(ResourceLocation::toString).orElse("<untracked>"), entity);
            return;
        }

        if (speedMultiplier != 1.0D) {
            Vec3 velocity = projectile.getDeltaMovement();
            projectile.setDeltaMovement(velocity.scale(speedMultiplier));
            projectile.hasImpulse = true;
        }

        if (damageMultiplier != 1.0D) {
            if (projectile instanceof AbstractArrow arrow) {
                arrow.setBaseDamage(arrow.getBaseDamage() * damageMultiplier);
            } else {
                Origins.LOGGER.warn("Modify projectile action '{}' cannot adjust damage for entity type {}",
                    sourceId.map(ResourceLocation::toString).orElse("<untracked>"), entity.getType());
            }
        }
    }

    public static ModifyProjectileAction fromJson(ResourceLocation id, JsonObject json) {
        double speed = json.has("speed_multiplier") ? GsonHelper.getAsDouble(json, "speed_multiplier") : 1.0D;
        double damage = json.has("damage_multiplier") ? GsonHelper.getAsDouble(json, "damage_multiplier") : 1.0D;
        ModifyProjectileAction action = new ModifyProjectileAction(Optional.of(id), speed, damage);
        DataResult<ModifyProjectileAction> validation = validate(action);
        return validation.resultOrPartial(message -> Origins.LOGGER.warn("Modify projectile action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<ModifyProjectileAction> codec() {
        return CODEC;
    }

    private static DataResult<ModifyProjectileAction> validate(ModifyProjectileAction action) {
        if (!Double.isFinite(action.speedMultiplier) || action.speedMultiplier <= MIN_MULTIPLIER) {
            return DataResult.error(() -> "speed multiplier must be greater than " + MIN_MULTIPLIER);
        }
        if (!Double.isFinite(action.damageMultiplier) || action.damageMultiplier <= MIN_MULTIPLIER) {
            return DataResult.error(() -> "damage multiplier must be greater than " + MIN_MULTIPLIER);
        }
        return DataResult.success(action);
    }
}
