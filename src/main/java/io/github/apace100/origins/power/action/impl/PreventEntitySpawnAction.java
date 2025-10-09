package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * Datapack action that cancels spawn attempts for the configured entity type.
 */
public final class PreventEntitySpawnAction implements Action<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_entity_spawn");
    private static final ThreadLocal<SpawnContext> CONTEXT = new ThreadLocal<>();
    private static final Codec<PreventEntitySpawnAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(PreventEntitySpawnAction::entityType)
    ).apply(instance, PreventEntitySpawnAction::new));

    private final EntityType<?> entityType;

    private PreventEntitySpawnAction(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    private EntityType<?> entityType() {
        return entityType;
    }

    @Override
    public void execute(ServerLevel level) {
        if (level == null) {
            return;
        }

        SpawnContext context = CONTEXT.get();
        if (context == null) {
            return;
        }

        if (context.entityType.equals(entityType)) {
            context.cancel();
        }
    }

    public static PreventEntitySpawnAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("entity")) {
            Origins.LOGGER.warn("Prevent entity spawn action '{}' is missing required 'entity' field", id);
            return null;
        }

        String rawEntity = GsonHelper.getAsString(json, "entity");
        ResourceLocation entityId;
        try {
            entityId = ResourceLocation.parse(rawEntity);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent entity spawn action '{}' has invalid entity id '{}': {}", id, rawEntity, exception.getMessage());
            return null;
        }

        Optional<EntityType<?>> entity = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
        if (entity.isEmpty()) {
            Origins.LOGGER.warn("Prevent entity spawn action '{}' references unknown entity '{}'", id, entityId);
            return null;
        }

        return new PreventEntitySpawnAction(entity.get());
    }

    public static Codec<PreventEntitySpawnAction> codec() {
        return CODEC;
    }

    public static boolean withContext(EntityType<?> entityType, Runnable runnable) {
        if (entityType == null) {
            runnable.run();
            return false;
        }

        SpawnContext previous = CONTEXT.get();
        SpawnContext context = new SpawnContext(entityType);
        CONTEXT.set(context);
        try {
            runnable.run();
            return context.cancelled;
        } finally {
            if (previous == null) {
                CONTEXT.remove();
            } else {
                CONTEXT.set(previous);
            }
        }
    }

    private static final class SpawnContext {
        private final EntityType<?> entityType;
        private boolean cancelled;

        private SpawnContext(EntityType<?> entityType) {
            this.entityType = entityType;
        }

        private void cancel() {
            this.cancelled = true;
        }
    }
}
