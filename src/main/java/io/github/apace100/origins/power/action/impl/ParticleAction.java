package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Datapack action that spawns particles in the world.
 */
public final class ParticleAction implements Action<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "particle");
    private static final Codec<ParticleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.PARTICLE_TYPE.byNameCodec()
            .comapFlatMap(ParticleAction::validateParticleType, Function.identity())
            .fieldOf("particle").forGetter(ParticleAction::particleType),
        Vec3.CODEC.optionalFieldOf("pos").forGetter(ParticleAction::position),
        Codec.INT.optionalFieldOf("count", 1).forGetter(ParticleAction::count),
        Codec.DOUBLE.optionalFieldOf("speed", 0.0).forGetter(ParticleAction::speed)
    ).apply(instance, ParticleAction::new));

    private final SimpleParticleType particleType;
    private final Optional<Vec3> position;
    private final int count;
    private final double speed;
    private boolean missingPlayerLogged;

    private ParticleAction(SimpleParticleType particleType, Optional<Vec3> position, int count, double speed) {
        this.particleType = particleType;
        this.position = position;
        this.count = count;
        this.speed = speed;
    }

    private SimpleParticleType particleType() {
        return particleType;
    }

    private Optional<Vec3> position() {
        return position;
    }

    private int count() {
        return count;
    }

    private double speed() {
        return speed;
    }

    @Override
    public void execute(ServerLevel level) {
        if (level == null) {
            return;
        }

        position.ifPresentOrElse(pos ->
            level.sendParticles(particleType, pos.x, pos.y, pos.z, Math.max(0, count), 0.0, 0.0, 0.0, speed),
            () -> spawnAtPlayer(level)
        );
    }

    private void spawnAtPlayer(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            if (!missingPlayerLogged) {
                Origins.LOGGER.warn("Particle action requested player position but no players were present in level '{}'", level.dimension().location());
                missingPlayerLogged = true;
            }
            return;
        }

        ServerPlayer player = players.getFirst();
        level.sendParticles(particleType, player.getX(), player.getY(), player.getZ(), Math.max(0, count), 0.0, 0.0, 0.0, speed);
    }

    public static ParticleAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("particle")) {
            Origins.LOGGER.warn("Particle action '{}' is missing required 'particle' field", id);
            return null;
        }

        String rawParticle = GsonHelper.getAsString(json, "particle");
        ResourceLocation particleId = parseId(rawParticle);
        if (particleId == null) {
            Origins.LOGGER.warn("Particle action '{}' has invalid particle id '{}'", id, rawParticle);
            return null;
        }

        Optional<SimpleParticleType> particleType = BuiltInRegistries.PARTICLE_TYPE.getOptional(particleId)
            .filter(SimpleParticleType.class::isInstance)
            .map(SimpleParticleType.class::cast);
        if (particleType.isEmpty()) {
            Origins.LOGGER.warn("Particle action '{}' references unsupported particle '{}'", id, particleId);
            return null;
        }

        Optional<Vec3> position = Optional.empty();
        if (json.has("pos")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "pos");
            if (array.size() != 3) {
                Origins.LOGGER.warn("Particle action '{}' position must contain exactly 3 elements", id);
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
                Origins.LOGGER.warn("Particle action '{}' has invalid position values: {}", id, exception.getMessage());
                return null;
            }

            position = Optional.of(new Vec3(x, y, z));
        }

        int count = GsonHelper.getAsInt(json, "count", 1);
        if (count < 0) {
            Origins.LOGGER.warn("Particle action '{}' specified negative particle count {}", id, count);
            return null;
        }

        double speed = GsonHelper.getAsDouble(json, "speed", 0.0);
        if (speed < 0.0) {
            Origins.LOGGER.warn("Particle action '{}' specified negative particle speed {}", id, speed);
            return null;
        }

        return new ParticleAction(particleType.get(), position, count, speed);
    }

    public static Codec<ParticleAction> codec() {
        return CODEC;
    }

    private static DataResult<SimpleParticleType> validateParticleType(ParticleType<?> type) {
        if (type instanceof SimpleParticleType simple) {
            return DataResult.success(simple);
        }
        return DataResult.error(() -> "Particle action only supports simple particle types");
    }

    private static ResourceLocation parseId(String raw) {
        ResourceLocation parsed = ResourceLocation.tryParse(raw);
        if (parsed == null && !raw.contains(":")) {
            parsed = ResourceLocation.tryParse("minecraft:" + raw);
        }
        return parsed;
    }
}
