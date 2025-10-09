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
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * Datapack condition that matches a spawn attempt for a specific entity type.
 */
public final class PreventEntitySpawnCondition implements Condition<EntityType<?>> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_entity_spawn");
    private static final Codec<PreventEntitySpawnCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(PreventEntitySpawnCondition::entityType)
    ).apply(instance, PreventEntitySpawnCondition::new));

    private final EntityType<?> entityType;

    private PreventEntitySpawnCondition(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    private EntityType<?> entityType() {
        return entityType;
    }

    @Override
    public boolean test(EntityType<?> type) {
        return type != null && type.equals(entityType);
    }

    public static PreventEntitySpawnCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("entity")) {
            Origins.LOGGER.warn("Prevent entity spawn condition '{}' is missing required 'entity' field", id);
            return null;
        }

        String rawEntity = GsonHelper.getAsString(json, "entity");
        ResourceLocation entityId;
        try {
            entityId = ResourceLocation.parse(rawEntity);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent entity spawn condition '{}' has invalid entity id '{}': {}", id, rawEntity, exception.getMessage());
            return null;
        }

        Optional<EntityType<?>> entity = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
        if (entity.isEmpty()) {
            Origins.LOGGER.warn("Prevent entity spawn condition '{}' references unknown entity '{}'", id, entityId);
            return null;
        }

        return new PreventEntitySpawnCondition(entity.get());
    }

    public static Codec<PreventEntitySpawnCondition> codec() {
        return CODEC;
    }
}
