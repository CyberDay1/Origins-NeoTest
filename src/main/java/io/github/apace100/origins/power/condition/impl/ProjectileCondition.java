package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.Optional;

/**
 * Datapack condition that checks whether the supplied entity is a projectile of a configured type.
 */
public final class ProjectileCondition implements Condition<Entity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("projectile");
    private static final Codec<ProjectileCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(ProjectileCondition::entityType)
    ).apply(instance, ProjectileCondition::new));

    private final EntityType<?> entityType;
    private final Optional<ResourceLocation> sourceId;

    private ProjectileCondition(EntityType<?> entityType) {
        this(Optional.empty(), entityType);
    }

    private ProjectileCondition(Optional<ResourceLocation> sourceId, EntityType<?> entityType) {
        this.sourceId = sourceId;
        this.entityType = entityType;
    }

    private EntityType<?> entityType() {
        return entityType;
    }

    @Override
    public boolean test(Entity entity) {
        if (!(entity instanceof Projectile)) {
            Origins.LOGGER.warn("Projectile condition '{}' evaluated a non-projectile entity {}",
                sourceId.map(ResourceLocation::toString).orElse("<untracked>"), entity);
            return false;
        }
        return entity.getType() == entityType;
    }

    public static ProjectileCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("entity")) {
            Origins.LOGGER.warn("Projectile condition '{}' is missing required 'entity' field", id);
            return null;
        }

        String raw = GsonHelper.getAsString(json, "entity");
        ResourceLocation entityId;
        try {
            entityId = ResourceLocation.parse(raw);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Projectile condition '{}' has invalid entity id '{}': {}", id, raw, exception.getMessage());
            return null;
        }

        Optional<EntityType<?>> resolved = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Projectile condition '{}' references unknown entity '{}'", id, entityId);
            return null;
        }

        return new ProjectileCondition(Optional.of(id), resolved.get());
    }

    public static Codec<ProjectileCondition> codec() {
        return CODEC;
    }
}
