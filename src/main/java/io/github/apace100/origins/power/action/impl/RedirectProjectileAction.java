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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Datapack action that rotates projectile trajectory by yaw/pitch offsets.
 */
public final class RedirectProjectileAction implements Action<Entity> {
    private static final double MINIMUM_SPEED_SQUARED = 1.0E-7D;

    public static final ResourceLocation TYPE =
        ResourceLocationCompat.mod("redirect_projectile");
    private static final Codec<RedirectProjectileAction> CODEC =
        RecordCodecBuilder.<RedirectProjectileAction>create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("yaw_offset", 0.0D).forGetter(RedirectProjectileAction::yawOffset),
            Codec.DOUBLE.optionalFieldOf("pitch_offset", 0.0D).forGetter(RedirectProjectileAction::pitchOffset)
        ).apply(instance, (yaw, pitch) -> new RedirectProjectileAction(yaw, pitch)))
            .flatXmap(RedirectProjectileAction::validate, RedirectProjectileAction::validate);

    private final Optional<ResourceLocation> sourceId;
    private final double yawOffset;
    private final double pitchOffset;

    private RedirectProjectileAction(double yawOffset, double pitchOffset) {
        this(Optional.empty(), yawOffset, pitchOffset);
    }

    private RedirectProjectileAction(Optional<ResourceLocation> sourceId, double yawOffset, double pitchOffset) {
        this.sourceId = sourceId;
        this.yawOffset = yawOffset;
        this.pitchOffset = pitchOffset;
    }

    private double yawOffset() {
        return yawOffset;
    }

    private double pitchOffset() {
        return pitchOffset;
    }

    @Override
    public void execute(Entity entity) {
        if (!(entity instanceof Projectile projectile)) {
            Origins.LOGGER.warn(
                "Redirect projectile action '{}' attempted to run on non-projectile entity {}",
                sourceId.map(ResourceLocation::toString).orElse("<untracked>"), entity
            );
            return;
        }

        float newYaw = (float)(projectile.getYRot() + yawOffset);
        float newPitch = (float)(projectile.getXRot() + pitchOffset);
        newPitch = Mth.clamp(newPitch, -90.0F, 90.0F);

        Vec3 velocity = projectile.getDeltaMovement();
        double speedSquared = velocity.lengthSqr();
        if (speedSquared > MINIMUM_SPEED_SQUARED) {
            double speed = Math.sqrt(speedSquared);
            float yawRadians = newYaw * ((float)Math.PI / 180.0F);
            float pitchRadians = newPitch * ((float)Math.PI / 180.0F);
            double x = -Mth.sin(yawRadians) * Mth.cos(pitchRadians);
            double y = -Mth.sin(pitchRadians);
            double z = Mth.cos(yawRadians) * Mth.cos(pitchRadians);
            projectile.setDeltaMovement(new Vec3(x, y, z).scale(speed));
            projectile.hasImpulse = true;
        }

        projectile.setYRot(newYaw);
        projectile.yRotO = newYaw;
        projectile.setXRot(newPitch);
        projectile.xRotO = newPitch;
        projectile.setYHeadRot(newYaw);
    }

    public static RedirectProjectileAction fromJson(ResourceLocation id, JsonObject json) {
        double yaw = json.has("yaw_offset") ? GsonHelper.getAsDouble(json, "yaw_offset") : 0.0D;
        double pitch = json.has("pitch_offset") ? GsonHelper.getAsDouble(json, "pitch_offset") : 0.0D;
        RedirectProjectileAction action = new RedirectProjectileAction(Optional.of(id), yaw, pitch);
        DataResult<RedirectProjectileAction> validation = validate(action);
        return validation.resultOrPartial(message ->
            Origins.LOGGER.warn("Redirect projectile action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<RedirectProjectileAction> codec() {
        return CODEC;
    }

    private static DataResult<RedirectProjectileAction> validate(RedirectProjectileAction action) {
        if (!Double.isFinite(action.yawOffset) || !Double.isFinite(action.pitchOffset)) {
            return DataResult.error(() -> "yaw_offset and pitch_offset must be finite values");
        }
        return DataResult.success(action);
    }
}
