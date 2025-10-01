package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

/**
 * Scaffold implementation for fluid datapack conditions.
 */
public final class FluidCondition implements Condition<FluidState> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "fluid");
    private static final Codec<FluidCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("fluid").forGetter(FluidCondition::fluidId)
    ).apply(instance, FluidCondition::new));

    private final Optional<ResourceLocation> fluidId;

    private FluidCondition(Optional<ResourceLocation> fluidId) {
        this.fluidId = fluidId;
    }

    public Optional<ResourceLocation> fluidId() {
        return fluidId;
    }

    @Override
    public boolean test(FluidState state) {
        // TODO: Inspect the supplied fluid state against the configured identifier or tag.
        return false;
    }

    public static FluidCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("fluid")) {
            String raw = GsonHelper.getAsString(json, "fluid");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Fluid condition '{}' has invalid fluid id '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new FluidCondition(parsed);
    }

    public static Codec<FluidCondition> codec() {
        return CODEC;
    }
}
