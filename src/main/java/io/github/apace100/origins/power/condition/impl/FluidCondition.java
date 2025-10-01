package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

/**
 * Datapack condition that checks the fluid state at a block position.
 */
public final class FluidCondition implements Condition<BlockState> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "fluid");
    private static final Codec<FluidCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidCondition::fluid)
    ).apply(instance, FluidCondition::new));

    private final Fluid fluid;

    private FluidCondition(Fluid fluid) {
        this.fluid = fluid;
    }

    private Fluid fluid() {
        return fluid;
    }

    @Override
    public boolean test(BlockState state) {
        if (state == null) {
            return false;
        }
        return state.getFluidState().is(fluid);
    }

    public static FluidCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("fluid")) {
            Origins.LOGGER.warn("Fluid condition '{}' is missing required 'fluid' field", id);
            return null;
        }

        String rawFluid = GsonHelper.getAsString(json, "fluid");
        ResourceLocation fluidId;
        try {
            fluidId = ResourceLocation.parse(rawFluid);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Fluid condition '{}' has invalid fluid id '{}': {}", id, rawFluid, exception.getMessage());
            return null;
        }

        Optional<Fluid> fluid = BuiltInRegistries.FLUID.getOptional(fluidId);
        if (fluid.isEmpty()) {
            Origins.LOGGER.warn("Fluid condition '{}' references unknown fluid '{}'", id, fluidId);
            return null;
        }

        return new FluidCondition(fluid.get());
    }

    public static Codec<FluidCondition> codec() {
        return CODEC;
    }
}
