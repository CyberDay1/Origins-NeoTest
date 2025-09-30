package io.github.apace100.origins.neoforge.capability;

import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

public final class OriginCapabilities {
    public static final EntityCapability<PlayerOrigin, Void> PLAYER_ORIGIN = EntityCapability.createVoid(
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "origin"), PlayerOrigin.class
    );

    private OriginCapabilities() {
    }
}
