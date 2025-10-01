package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Datapack action that summons a lightning bolt at a configured position.
 */
public final class LightningAction implements Action<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "lightning");
    private static final Codec<LightningAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.optionalFieldOf("pos").forGetter(LightningAction::position),
        Codec.BOOL.optionalFieldOf("effect_only", false).forGetter(LightningAction::effectOnly)
    ).apply(instance, LightningAction::new));

    private final Optional<Vec3> position;
    private final boolean effectOnly;
    private boolean missingPlayerLogged;

    private LightningAction(Optional<Vec3> position, boolean effectOnly) {
        this.position = position;
        this.effectOnly = effectOnly;
    }

    private Optional<Vec3> position() {
        return position;
    }

    private boolean effectOnly() {
        return effectOnly;
    }

    @Override
    public void execute(ServerLevel level) {
        if (level == null) {
            return;
        }

        Vec3 strikePos = position.orElseGet(() -> locatePlayerPosition(level));
        if (strikePos == null) {
            return;
        }

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) {
            return;
        }

        bolt.moveTo(strikePos.x, strikePos.y, strikePos.z);
        bolt.setVisualOnly(effectOnly);
        level.addFreshEntity(bolt);
    }

    private Vec3 locatePlayerPosition(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            if (!missingPlayerLogged) {
                Origins.LOGGER.warn("Lightning action requested player position but no players were present in level '{}'", level.dimension().location());
                missingPlayerLogged = true;
            }
            return null;
        }
        ServerPlayer player = players.getFirst();
        return new Vec3(player.getX(), player.getY(), player.getZ());
    }

    public static LightningAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<Vec3> position = Optional.empty();
        if (json.has("pos")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "pos");
            if (array.size() != 3) {
                Origins.LOGGER.warn("Lightning action '{}' position must contain exactly 3 elements", id);
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
                Origins.LOGGER.warn("Lightning action '{}' has invalid position values: {}", id, exception.getMessage());
                return null;
            }

            position = Optional.of(new Vec3(x, y, z));
        }

        boolean effectOnly = GsonHelper.getAsBoolean(json, "effect_only", false);
        return new LightningAction(position, effectOnly);
    }

    public static Codec<LightningAction> codec() {
        return CODEC;
    }
}
