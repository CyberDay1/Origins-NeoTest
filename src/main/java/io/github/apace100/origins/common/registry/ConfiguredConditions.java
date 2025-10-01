package io.github.apace100.origins.common.registry;

import io.github.apace100.origins.common.condition.ConfiguredCondition;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public final class ConfiguredConditions {
    private static final ConfiguredRegistry<ConfiguredCondition> CONDITIONS = new ConfiguredRegistry<>();

    private ConfiguredConditions() {
    }

    public static void setAll(Map<ResourceLocation, ConfiguredCondition> conditions) {
        CONDITIONS.setAll(conditions);
    }

    public static Optional<ConfiguredCondition> get(ResourceLocation id) {
        return CONDITIONS.get(id);
    }

    public static Iterable<ResourceLocation> ids() {
        return CONDITIONS.ids();
    }
}
