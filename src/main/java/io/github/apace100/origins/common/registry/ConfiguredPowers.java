package io.github.apace100.origins.common.registry;

import io.github.apace100.origins.common.power.ConfiguredPower;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public final class ConfiguredPowers {
    private static final ConfiguredRegistry<ConfiguredPower> POWERS = new ConfiguredRegistry<>();

    private ConfiguredPowers() {
    }

    public static void setAll(Map<ResourceLocation, ConfiguredPower> powers) {
        POWERS.setAll(powers);
    }

    public static Optional<ConfiguredPower> get(ResourceLocation id) {
        return POWERS.get(id);
    }

    public static Iterable<ResourceLocation> ids() {
        return POWERS.ids();
    }
}
