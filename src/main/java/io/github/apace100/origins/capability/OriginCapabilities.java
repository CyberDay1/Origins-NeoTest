package io.github.apace100.origins.capability;

import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = OriginsConstants.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class OriginCapabilities {
    public static final EntityCapability<PlayerOrigin, Void> PLAYER_ORIGIN = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(OriginsConstants.MODID, "player_origin"), PlayerOrigin.class);

    private OriginCapabilities() {
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(PLAYER_ORIGIN, EntityType.PLAYER, (player, context) -> PlayerOriginProvider.get(player));
    }
}
