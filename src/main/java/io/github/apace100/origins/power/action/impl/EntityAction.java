package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * Scaffold implementation for entity based datapack actions.
 */
public final class EntityAction implements Action<Entity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "entity");
    private static final Codec<EntityAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("entity_type").forGetter(EntityAction::entityType)
    ).apply(instance, EntityAction::new));

    private final Optional<ResourceLocation> entityType;

    private EntityAction(Optional<ResourceLocation> entityType) {
        this.entityType = entityType;
    }

    public Optional<ResourceLocation> entityType() {
        return entityType;
    }

    @Override
    public void execute(Entity entity) {
        // TODO: Implement Fabric parity behaviour for entity actions (effects/damage/etc.).
    }

    public static EntityAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("entity_type")) {
            String raw = GsonHelper.getAsString(json, "entity_type");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Failed to parse entity action '{}' entity type '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new EntityAction(parsed);
    }

    public static Codec<EntityAction> codec() {
        return CODEC;
    }
}
