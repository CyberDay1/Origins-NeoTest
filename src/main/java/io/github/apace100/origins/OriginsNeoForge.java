package io.github.apace100.origins;

import io.github.apace100.origins.common.commands.ModCommands;
import io.github.apace100.origins.common.config.ModConfigs;
import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.registry.ModActions;
import io.github.apace100.origins.common.registry.ModConditions;
import io.github.apace100.origins.common.registry.ModPowers;
import io.github.apace100.origins.datagen.ModDataGen;
import io.github.apace100.origins.datapack.DataValidationLogger;
import io.github.apace100.origins.datapack.OriginsDataLoader;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModItems;
import io.github.apace100.origins.power.OriginPowerManager;
import io.github.apace100.origins.power.action.impl.CombatActionHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@Mod(OriginsNeoForge.MODID)
public final class OriginsNeoForge {
    public static final String MODID = Origins.MOD_ID;

    public OriginsNeoForge(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModPowers.register(modBus);
        ModActions.register(modBus);
        ModConditions.register(modBus);
        ModNetworking.register(modBus);
        ModCommands.register(modBus);
        ModDataGen.register(modBus);

        OriginPowerManager.init();
        CombatActionHandler.init();

        ModConfigs.register(ModLoadingContext.get());
        OriginsConfig.register(ModLoadingContext.get());
        OriginsConfig.registerListeners(modBus);

        NeoForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> {
            event.addListener(new OriginsDataLoader());
            event.addListener(new DataValidationLogger());
        });

        if (FMLEnvironment.dist == Dist.CLIENT) {
            io.github.apace100.origins.client.OriginsClient.init();
        }
    }
}
