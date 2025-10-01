package io.github.apace100.origins.power;

import net.minecraft.resources.ResourceLocation;

/**
 * Minimal placeholder for data-driven power types. The full logic will be
 * implemented in later milestones.
 */
public class PowerType<T extends Power> {
    private final ResourceLocation id;

    public PowerType(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation id() {
        return id;
    }
}
