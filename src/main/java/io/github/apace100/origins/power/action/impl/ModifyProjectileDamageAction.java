package io.github.apace100.origins.power.action.impl;

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

import java.util.Optional;

/**
 * Datapack action that scales the base damage of projectile entities.
 */
public final class ModifyProjectileDamageAction implements Action<Entity> {
    private static final double MIN_MULTIPLIER = 0.0D;

    public static final ResourceLocation TYPE =
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "modify_projectile_damage");
    private static final Codec<ModifyProjectileDamageAction> CODEC =
        RecordCodecBuilder.<ModifyProjectileDamageAction>create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("multiplier").forGetter(ModifyProjectileDamageAction::multiplier)
        ).apply(instance, multiplier -> new ModifyProjectileDamageAction(multiplier)))
            .flatXmap(ModifyProjectileDamageAction::validate, ModifyProjectileDamageAction::validate);

    private final Optional<ResourceLocation> sourceId;
    private final double multiplier;

    private ModifyProjectileDamageAction(double multiplier) {
        this(Optional.empty(), multiplier);
    }

    private ModifyProjectileDamageAction(Optional<ResourceLocation> sourceId, double multiplier) {
        this.sourceId = sourceId;
        this.multiplier = multiplier;
    }

    private double multiplier() {
        return multiplier;
    }

    @Override
    public void execute(Entity entity) {
        if (!(entity instanceof Projectile projectile)) {
            Origins.LOGGER.warn(
                "Modify projectile damage action '{}' attempted to run on non-projectile entity {}",
                sourceId.map(ResourceLocation::toString).orElse("<untracked>"), entity
            );
            return;
        }

        if (projectile instanceof AbstractArrow arrow) {
            arrow.setBaseDamage(arrow.getBaseDamage() * multiplier);
            return;
        }

        Origins.LOGGER.warn(
            "Modify projectile damage action '{}' cannot adjust damage for entity type {}",
            sourceId.map(ResourceLocation::toString).orElse("<untracked>"), entity.getType()
        );
    }

    public static ModifyProjectileDamageAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("multiplier")) {
            Origins.LOGGER.warn(
                "Modify projectile damage action '{}' is missing required 'multiplier' field", id
            );
            return null;
        }

        double multiplier;
        try {
            multiplier = GsonHelper.getAsDouble(json, "multiplier");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn(
                "Modify projectile damage action '{}' has invalid 'multiplier': {}",
                id, exception.getMessage()
            );
            return null;
        }

        ModifyProjectileDamageAction action = new ModifyProjectileDamageAction(Optional.of(id), multiplier);
        DataResult<ModifyProjectileDamageAction> validation = validate(action);
        return validation.resultOrPartial(message ->
            Origins.LOGGER.warn("Modify projectile damage action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<ModifyProjectileDamageAction> codec() {
        return CODEC;
    }

    private static DataResult<ModifyProjectileDamageAction> validate(ModifyProjectileDamageAction action) {
        if (!Double.isFinite(action.multiplier) || action.multiplier <= MIN_MULTIPLIER) {
            return DataResult.error(() -> "multiplier must be greater than " + MIN_MULTIPLIER);
        }
        return DataResult.success(action);
    }
}
