package io.github.apace100.origins.neoforge.capability;
import io.github.apace100.origins.util.ResourceLocationCompat;

import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

public final class OriginCapabilities {
    public static final EntityCapability<PlayerOrigin, Void> PLAYER_ORIGIN = EntityCapability.createVoid(
        ResourceLocationCompat.mod("origin"), PlayerOrigin.class
    );

    private OriginCapabilities() {
    }
}
