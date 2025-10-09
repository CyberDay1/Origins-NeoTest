package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

/**
 * Scaffold implementation for dimension datapack conditions.
 */
public final class DimensionCondition implements Condition<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("dimension");
    private static final Codec<DimensionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DimensionCondition::dimensionKey)
    ).apply(instance, DimensionCondition::new));

    private final ResourceKey<Level> dimensionKey;

    private DimensionCondition(ResourceKey<Level> dimensionKey) {
        this.dimensionKey = dimensionKey;
    }

    public ResourceKey<Level> dimensionKey() {
        return dimensionKey;
    }

    @Override
    public boolean test(ServerLevel level) {
        return level != null && level.dimension().equals(dimensionKey);
    }

    public static DimensionCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("dimension")) {
            Origins.LOGGER.warn("Dimension condition '{}' is missing required 'dimension' field", id);
            return null;
        }

        String raw = GsonHelper.getAsString(json, "dimension");
        ResourceLocation dimensionId;
        try {
            dimensionId = ResourceLocation.parse(raw);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Dimension condition '{}' has invalid dimension id '{}': {}", id, raw, exception.getMessage());
            return null;
        }

        return new DimensionCondition(ResourceKey.create(Registries.DIMENSION, dimensionId));
    }

    public static Codec<DimensionCondition> codec() {
        return CODEC;
    }
}
