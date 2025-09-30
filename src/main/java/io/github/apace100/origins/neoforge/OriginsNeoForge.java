package io.github.apace100.origins.neoforge;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.commands.OriginsCommands;
import io.github.apace100.origins.common.config.OriginsConfig;
import io.github.apace100.origins.common.network.Network;
import io.github.apace100.origins.common.registry.ModBlocks;
import io.github.apace100.origins.common.registry.ModItems;
import io.github.apace100.origins.datagen.OriginsDataGeneration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Origins.MOD_ID)
public final class OriginsNeoForge {
    public OriginsNeoForge(IEventBus modEventBus) {
        ModBlocks.REGISTER.register(modEventBus);
        ModItems.REGISTER.register(modEventBus);

        OriginsConfig.register(ModLoadingContext.get(), modEventBus);
        OriginsCommands.register(modEventBus);
        OriginsDataGeneration.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Network::init);
    }
}
