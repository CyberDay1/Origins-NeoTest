package io.github.apace100.origins.platform;

import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.registry.OriginsActions;
import io.github.apace100.origins.registry.OriginsBlocks;
import io.github.apace100.origins.registry.OriginsConditions;
import io.github.apace100.origins.registry.OriginsItems;
import io.github.apace100.origins.registry.OriginsPowers;
import io.github.apace100.origins.util.OriginsConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(OriginsConstants.MODID)
public final class OriginsNeoForge {
    public OriginsNeoForge(IEventBus modEventBus, ModContainer container) {
        OriginsBlocks.BLOCKS.register(modEventBus);
        OriginsItems.ITEMS.register(modEventBus);
        OriginsPowers.POWERS.register(modEventBus);
        OriginsActions.ACTIONS.register(modEventBus);
        OriginsConditions.CONDITIONS.register(modEventBus);

        container.registerConfig(ModConfig.Type.COMMON, OriginsConfig.COMMON_SPEC);
    }
}
