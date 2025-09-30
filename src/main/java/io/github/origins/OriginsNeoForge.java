package io.github.origins;

import io.github.origins.command.OriginsCommands;
import io.github.origins.config.ModConfigs;
import io.github.origins.datagen.OriginsDataGenerators;
import io.github.origins.network.OriginsNetworking;
import io.github.origins.registry.ModBlocks;
import io.github.origins.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Origins.MOD_ID)
public class OriginsNeoForge {
    public OriginsNeoForge(IEventBus modBus) {
        ModItems.REGISTRY.register(modBus);
        ModBlocks.REGISTRY.register(modBus);
        ModConfigs.register(modBus);
        OriginsDataGenerators.register(modBus);
        OriginsCommands.register(modBus);
        OriginsNetworking.bootstrap();
    }
}
