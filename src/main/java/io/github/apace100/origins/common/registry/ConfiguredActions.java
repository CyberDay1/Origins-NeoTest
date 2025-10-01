package io.github.apace100.origins.common.registry;

import io.github.apace100.origins.common.action.ConfiguredAction;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public final class ConfiguredActions {
    private static final ConfiguredRegistry<ConfiguredAction> ACTIONS = new ConfiguredRegistry<>();

    private ConfiguredActions() {
    }

    public static void setAll(Map<ResourceLocation, ConfiguredAction> actions) {
        ACTIONS.setAll(actions);
    }

    public static Optional<ConfiguredAction> get(ResourceLocation id) {
        return ACTIONS.get(id);
    }

    public static Iterable<ResourceLocation> ids() {
        return ACTIONS.ids();
    }
}
