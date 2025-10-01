package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Scaffold implementation for dimension datapack conditions.
 */
public final class DimensionCondition implements Condition<ResourceKey<Level>> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "dimension");
    private static final Codec<DimensionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("dimension").forGetter(DimensionCondition::dimensionId)
    ).apply(instance, DimensionCondition::new));

    private final Optional<ResourceLocation> dimensionId;

    private DimensionCondition(Optional<ResourceLocation> dimensionId) {
        this.dimensionId = dimensionId;
    }

    public Optional<ResourceLocation> dimensionId() {
        return dimensionId;
    }

    @Override
    public boolean test(ResourceKey<Level> levelKey) {
        // TODO: Validate the dimension key against the configured id once registries are wired.
        return dimensionId.isEmpty();
    }

    public static DimensionCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("dimension")) {
            String raw = GsonHelper.getAsString(json, "dimension");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Dimension condition '{}' has invalid dimension id '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new DimensionCondition(parsed);
    }

    public static Codec<DimensionCondition> codec() {
        return CODEC;
    }
}
