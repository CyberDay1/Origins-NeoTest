package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Datapack action that spawns an entity at the configured position.
 */
public final class SpawnEntityAction implements Action<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "spawn_entity");
    private static final Codec<SpawnEntityAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(SpawnEntityAction::type),
        Vec3.CODEC.fieldOf("pos").forGetter(SpawnEntityAction::position)
    ).apply(instance, SpawnEntityAction::new));

    private final EntityType<?> type;
    private final Vec3 position;

    private SpawnEntityAction(EntityType<?> type, Vec3 position) {
        this.type = type;
        this.position = position;
    }

    private EntityType<?> type() {
        return type;
    }

    private Vec3 position() {
        return position;
    }

    @Override
    public void execute(ServerLevel level) {
        if (level == null) {
            return;
        }

        Entity entity = type.create(level);
        if (entity == null) {
            return;
        }

        entity.moveTo(position.x, position.y, position.z, entity.getYRot(), entity.getXRot());
        level.addFreshEntity(entity);
    }

    public static SpawnEntityAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("entity")) {
            Origins.LOGGER.warn("Spawn entity action '{}' is missing required 'entity' field", id);
            return null;
        }
        if (!json.has("pos")) {
            Origins.LOGGER.warn("Spawn entity action '{}' is missing required 'pos' field", id);
            return null;
        }

        String rawEntity = GsonHelper.getAsString(json, "entity");
        ResourceLocation entityId;
        try {
            entityId = ResourceLocation.parse(rawEntity);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Spawn entity action '{}' has invalid entity id '{}': {}", id, rawEntity, exception.getMessage());
            return null;
        }

        Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
        if (entityType.isEmpty()) {
            Origins.LOGGER.warn("Spawn entity action '{}' references unknown entity '{}'", id, entityId);
            return null;
        }

        JsonArray array = GsonHelper.getAsJsonArray(json, "pos");
        if (array.size() != 3) {
            Origins.LOGGER.warn("Spawn entity action '{}' position must contain exactly 3 elements", id);
            return null;
        }

        double x;
        double y;
        double z;
        try {
            x = GsonHelper.convertToDouble(array.get(0), "pos[0]");
            y = GsonHelper.convertToDouble(array.get(1), "pos[1]");
            z = GsonHelper.convertToDouble(array.get(2), "pos[2]");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Spawn entity action '{}' has invalid position values: {}", id, exception.getMessage());
            return null;
        }

        return new SpawnEntityAction(entityType.get(), new Vec3(x, y, z));
    }

    public static Codec<SpawnEntityAction> codec() {
        return CODEC;
    }
}
