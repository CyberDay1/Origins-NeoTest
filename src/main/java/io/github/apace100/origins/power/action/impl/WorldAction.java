package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Scaffold implementation for world level datapack actions.
 */
public final class WorldAction implements Action<Level> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "world");
    private static final Codec<WorldAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("event").forGetter(WorldAction::event)
    ).apply(instance, WorldAction::new));

    private final Optional<String> event;

    private WorldAction(Optional<String> event) {
        this.event = event;
    }

    public Optional<String> event() {
        return event;
    }

    @Override
    public void execute(Level level) {
        // TODO: Implement Fabric parity behaviour for world actions (explosions/weather/etc.).
    }

    public static WorldAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<String> parsed = Optional.empty();
        if (json.has("event")) {
            String raw = GsonHelper.getAsString(json, "event");
            if (raw.isBlank()) {
                Origins.LOGGER.warn("World action '{}' provided a blank event identifier", id);
                return null;
            }
            parsed = Optional.of(raw);
        }
        return new WorldAction(parsed);
    }

    public static Codec<WorldAction> codec() {
        return CODEC;
    }
}
