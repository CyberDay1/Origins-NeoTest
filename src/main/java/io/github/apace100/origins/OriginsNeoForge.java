package io.github.apace100.origins;

import io.github.apace100.origins.init.OriginsBlocks;
import io.github.apace100.origins.init.OriginsCommands;
import io.github.apace100.origins.init.OriginsConfigs;
import io.github.apace100.origins.init.OriginsDatagen;
import io.github.apace100.origins.init.OriginsItems;
import io.github.apace100.origins.init.OriginsNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Origins.MOD_ID)
public class OriginsNeoForge {

    public OriginsNeoForge(IEventBus modEventBus) {
        OriginsBlocks.BLOCKS.register(modEventBus);
        OriginsItems.ITEMS.register(modEventBus);
        OriginsConfigs.register(modEventBus);
        OriginsDatagen.register(modEventBus);
        OriginsNetworking.register();
        OriginsCommands.register(modEventBus);
    }
}
