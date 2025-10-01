package io.github.apace100.origins.common.registry;

import io.github.apace100.origins.common.origin.Origin;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class OriginRegistry {
    private static final ConfiguredRegistry<Origin> ORIGINS = new ConfiguredRegistry<>();

    private OriginRegistry() {
    }

    public static void setAll(Map<ResourceLocation, Origin> origins) {
        ORIGINS.setAll(origins);
    }

    public static Optional<Origin> get(ResourceLocation id) {
        return ORIGINS.get(id);
    }

    public static Collection<ResourceLocation> ids() {
        return ORIGINS.ids();
    }

    public static Collection<Origin> values() {
        return ORIGINS.values();
    }
}
