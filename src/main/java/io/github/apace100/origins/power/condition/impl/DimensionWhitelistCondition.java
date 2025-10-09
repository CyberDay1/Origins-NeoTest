package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Datapack condition that checks whether a world is part of a dimension whitelist.
 */
public final class DimensionWhitelistCondition implements Condition<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("dimension_whitelist");
    private static final Codec<DimensionWhitelistCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("dimensions")
            .forGetter(condition -> List.copyOf(condition.dimensions))
    ).apply(instance, keys -> new DimensionWhitelistCondition(new HashSet<>(keys))));

    private final Set<ResourceKey<Level>> dimensions;

    private DimensionWhitelistCondition(Set<ResourceKey<Level>> dimensions) {
        this.dimensions = dimensions;
    }

    private Set<ResourceKey<Level>> dimensions() {
        return dimensions;
    }

    @Override
    public boolean test(ServerLevel level) {
        return level != null && dimensions.contains(level.dimension());
    }

    public static DimensionWhitelistCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("dimensions")) {
            Origins.LOGGER.warn("Dimension whitelist condition '{}' is missing required 'dimensions' field", id);
            return null;
        }

        JsonArray array = GsonHelper.getAsJsonArray(json, "dimensions");
        if (array.isEmpty()) {
            Origins.LOGGER.warn("Dimension whitelist condition '{}' provided an empty 'dimensions' array", id);
            return null;
        }

        Set<ResourceKey<Level>> keys = new HashSet<>();
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                Origins.LOGGER.warn("Dimension whitelist condition '{}' has non-string dimension entry {}", id, element);
                return null;
            }

            String raw = element.getAsString();
            ResourceLocation dimensionId;
            try {
                dimensionId = ResourceLocation.parse(raw);
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Dimension whitelist condition '{}' has invalid dimension id '{}': {}", id, raw, exception.getMessage());
                return null;
            }

            keys.add(ResourceKey.create(Registries.DIMENSION, dimensionId));
        }

        return new DimensionWhitelistCondition(keys);
    }

    public static Codec<DimensionWhitelistCondition> codec() {
        return CODEC;
    }
}
