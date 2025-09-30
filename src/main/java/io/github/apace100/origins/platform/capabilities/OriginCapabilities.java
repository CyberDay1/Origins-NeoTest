package io.github.apace100.origins.platform.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class OriginCapabilities {
    public static final EntityCapability<PlayerOrigin, Void> PLAYER_ORIGIN = EntityCapability.createVoid(
        ResourceLocation.fromNamespaceAndPath("origins", "origin"),
        PlayerOrigin.class
    );

    private OriginCapabilities() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(OriginCapabilities::onRegisterCaps);
    }

    private static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.registerEntity(PLAYER_ORIGIN, EntityType.PLAYER, new PlayerOriginProvider());
    }
}
