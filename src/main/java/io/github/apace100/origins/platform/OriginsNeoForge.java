package io.github.apace100.origins.platform;

import io.github.apace100.origins.platform.capabilities.OriginCapabilities;
import io.github.apace100.origins.platform.capabilities.PlayerOriginProvider;
import io.github.apace100.origins.platform.network.OriginsNetworking;
import io.github.apace100.origins.platform.registry.ModBlocks;
import io.github.apace100.origins.platform.registry.ModItems;
import io.github.apace100.origins.platform.config.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("origins")
public class OriginsNeoForge {

    public OriginsNeoForge(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        OriginCapabilities.register(modEventBus);
        ModConfig.register();
        OriginsNetworking.register(modEventBus);
        OriginEvents.register(modEventBus);
    }
}
