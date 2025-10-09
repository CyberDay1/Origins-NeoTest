package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Datapack action that plays a configured sound in the world.
 */
public final class PlaySoundAction implements Action<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("play_sound");
    private static final Codec<PlaySoundAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(PlaySoundAction::sound),
        Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(PlaySoundAction::volume),
        Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(PlaySoundAction::pitch),
        Vec3.CODEC.optionalFieldOf("pos").forGetter(PlaySoundAction::position)
    ).apply(instance, PlaySoundAction::new));

    private final SoundEvent sound;
    private final float volume;
    private final float pitch;
    private final Optional<Vec3> position;
    private boolean missingPlayerLogged;

    private PlaySoundAction(SoundEvent sound, float volume, float pitch, Optional<Vec3> position) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.position = position;
    }

    private SoundEvent sound() {
        return sound;
    }

    private float volume() {
        return volume;
    }

    private float pitch() {
        return pitch;
    }

    private Optional<Vec3> position() {
        return position;
    }

    @Override
    public void execute(ServerLevel level) {
        if (level == null) {
            return;
        }

        position.ifPresentOrElse(pos ->
            level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, pitch),
            () -> playAtPlayerPosition(level)
        );
    }

    private void playAtPlayerPosition(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            if (!missingPlayerLogged) {
                Origins.LOGGER.warn("Play sound action requested player position but no players were present in level '{}'", level.dimension().location());
                missingPlayerLogged = true;
            }
            return;
        }

        ServerPlayer player = players.getFirst();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }

    public static PlaySoundAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("sound")) {
            Origins.LOGGER.warn("Play sound action '{}' is missing required 'sound' field", id);
            return null;
        }

        String rawSound = GsonHelper.getAsString(json, "sound");
        ResourceLocation soundId = parseId(rawSound);
        if (soundId == null) {
            Origins.LOGGER.warn("Play sound action '{}' has invalid sound id '{}'", id, rawSound);
            return null;
        }

        Optional<SoundEvent> sound = BuiltInRegistries.SOUND_EVENT.getOptional(soundId);
        if (sound.isEmpty()) {
            Origins.LOGGER.warn("Play sound action '{}' references unknown sound '{}'", id, soundId);
            return null;
        }

        float volume = (float) GsonHelper.getAsDouble(json, "volume", 1.0);
        if (volume < 0.0F) {
            Origins.LOGGER.warn("Play sound action '{}' specified negative volume {}", id, volume);
            return null;
        }

        float pitch = (float) GsonHelper.getAsDouble(json, "pitch", 1.0);
        if (pitch <= 0.0F) {
            Origins.LOGGER.warn("Play sound action '{}' specified non-positive pitch {}", id, pitch);
            return null;
        }

        Optional<Vec3> position = Optional.empty();
        if (json.has("pos")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "pos");
            if (array.size() != 3) {
                Origins.LOGGER.warn("Play sound action '{}' position must contain exactly 3 elements", id);
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
                Origins.LOGGER.warn("Play sound action '{}' has invalid position values: {}", id, exception.getMessage());
                return null;
            }

            position = Optional.of(new Vec3(x, y, z));
        }

        return new PlaySoundAction(sound.get(), volume, pitch, position);
    }

    public static Codec<PlaySoundAction> codec() {
        return CODEC;
    }

    private static ResourceLocation parseId(String raw) {
        ResourceLocation parsed = ResourceLocation.tryParse(raw);
        if (parsed == null && !raw.contains(":")) {
            parsed = ResourceLocation.tryParse("minecraft:" + raw);
        }
        return parsed;
    }

}
