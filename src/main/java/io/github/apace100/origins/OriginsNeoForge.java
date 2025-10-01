package io.github.apace100.origins;

import io.github.apace100.origins.client.OriginsClient;
import io.github.apace100.origins.common.commands.ModCommands;
import io.github.apace100.origins.common.config.ModConfigs;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.registry.ModActions;
import io.github.apace100.origins.common.registry.ModConditions;
import io.github.apace100.origins.common.registry.ModPowers;
import io.github.apace100.origins.datagen.ModDataGen;
import io.github.apace100.origins.neoforge.capability.OriginCapabilities;
import io.github.apace100.origins.neoforge.capability.PlayerOriginProvider;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(OriginsNeoForge.MODID)
public class OriginsNeoForge {
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

        ModConfigs.register(ModLoadingContext.get());

        modBus.addListener(this::setup);
        modBus.addListener(this::registerCapabilities);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            OriginsClient.init();
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        // networking/capability bootstrap here
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(OriginCapabilities.PLAYER_ORIGIN, EntityType.PLAYER, new PlayerOriginProvider());
    }
}
