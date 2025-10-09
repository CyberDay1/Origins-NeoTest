package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Datapack action that triggers an explosion in the world.
 */
public final class ExplosionAction implements Action<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("explosion");
    private static final Codec<ExplosionAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.optionalFieldOf("pos").forGetter(ExplosionAction::position),
        Codec.FLOAT.optionalFieldOf("power", 1.0F).forGetter(ExplosionAction::power),
        Codec.BOOL.optionalFieldOf("fire", false).forGetter(ExplosionAction::fire),
        Codec.BOOL.optionalFieldOf("break_blocks", true).forGetter(ExplosionAction::breakBlocks)
    ).apply(instance, ExplosionAction::new));

    private final Optional<Vec3> position;
    private final float power;
    private final boolean fire;
    private final boolean breakBlocks;
    private boolean missingPlayerLogged;

    private ExplosionAction(Optional<Vec3> position, float power, boolean fire, boolean breakBlocks) {
        this.position = position;
        this.power = power;
        this.fire = fire;
        this.breakBlocks = breakBlocks;
    }

    private Optional<Vec3> position() {
        return position;
    }

    private float power() {
        return power;
    }

    private boolean fire() {
        return fire;
    }

    private boolean breakBlocks() {
        return breakBlocks;
    }

    @Override
    public void execute(ServerLevel level) {
        if (level == null) {
            return;
        }

        Vec3 explosionPos = position.orElseGet(() -> locatePlayerPosition(level));
        if (explosionPos == null) {
            return;
        }

        Level.ExplosionInteraction interaction = breakBlocks ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE;
        level.explode(null, explosionPos.x, explosionPos.y, explosionPos.z, Math.max(0.0F, power), fire, interaction);
    }

    private Vec3 locatePlayerPosition(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            if (!missingPlayerLogged) {
                Origins.LOGGER.warn("Explosion action requested player position but no players were present in level '{}'", level.dimension().location());
                missingPlayerLogged = true;
            }
            return null;
        }

        ServerPlayer player = players.getFirst();
        return new Vec3(player.getX(), player.getY(), player.getZ());
    }

    public static ExplosionAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<Vec3> position = Optional.empty();
        if (json.has("pos")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "pos");
            if (array.size() != 3) {
                Origins.LOGGER.warn("Explosion action '{}' position must contain exactly 3 elements", id);
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
                Origins.LOGGER.warn("Explosion action '{}' has invalid position values: {}", id, exception.getMessage());
                return null;
            }

            position = Optional.of(new Vec3(x, y, z));
        }

        float power = (float) GsonHelper.getAsDouble(json, "power", 1.0);
        if (power < 0.0F) {
            Origins.LOGGER.warn("Explosion action '{}' specified negative power {}", id, power);
            return null;
        }

        boolean fire = GsonHelper.getAsBoolean(json, "fire", false);
        boolean breakBlocks = GsonHelper.getAsBoolean(json, "break_blocks", true);
        return new ExplosionAction(position, power, fire, breakBlocks);
    }

    public static Codec<ExplosionAction> codec() {
        return CODEC;
    }
}
