package io.github.apace100.origins.common.action;

import io.github.apace100.origins.api.Action;
import net.minecraft.resources.ResourceLocation;

public record ConfiguredAction(ResourceLocation type, Action<Void> action) {
    public void run() {
        action.run(null);
    }
}
