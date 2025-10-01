package io.github.apace100.origins.capability;

import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class OriginCapabilities {
    public static final EntityCapability<PlayerOrigin, Void> PLAYER_ORIGIN =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "player_origin"), PlayerOrigin.class);

    private OriginCapabilities() {}

    @SubscribeEvent
    public static void onRegisterCaps(RegisterCapabilitiesEvent e) {
        e.registerEntity(PLAYER_ORIGIN, EntityType.PLAYER, (player, ctx) -> new PlayerOrigin());
    }
}
