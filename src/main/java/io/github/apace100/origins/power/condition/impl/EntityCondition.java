package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * Scaffold implementation for general entity datapack conditions.
 */
public final class EntityCondition implements Condition<Entity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "entity");
    private static final Codec<EntityCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("entity_type").forGetter(EntityCondition::entityType)
    ).apply(instance, EntityCondition::new));

    private final Optional<ResourceLocation> entityType;

    private EntityCondition(Optional<ResourceLocation> entityType) {
        this.entityType = entityType;
    }

    public Optional<ResourceLocation> entityType() {
        return entityType;
    }

    @Override
    public boolean test(Entity entity) {
        // TODO: Validate the entity instance against the configured filters.
        return false;
    }

    public static EntityCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("entity_type")) {
            String raw = GsonHelper.getAsString(json, "entity_type");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Entity condition '{}' has invalid entity type id '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new EntityCondition(parsed);
    }

    public static Codec<EntityCondition> codec() {
        return CODEC;
    }
}
