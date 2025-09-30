package io.github.apace100.origins.neoforge;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.commands.ModCommands;
import io.github.apace100.origins.common.config.ModConfigs;
import io.github.apace100.origins.common.network.ModNetworking;
import io.github.apace100.origins.common.registry.ModActions;
import io.github.apace100.origins.common.registry.ModBlocks;
import io.github.apace100.origins.common.registry.ModConditions;
import io.github.apace100.origins.common.registry.ModItems;
import io.github.apace100.origins.common.registry.ModPowers;
import io.github.apace100.origins.neoforge.capability.OriginCapabilities;
import io.github.apace100.origins.neoforge.capability.PlayerOriginProvider;
import io.github.apace100.origins.datagen.ModDataGen;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;

@Mod(Origins.MOD_ID)
public final class OriginsNeoForge {
    public static final String MODID = Origins.MOD_ID;

    public OriginsNeoForge(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModPowers.register(modEventBus);
        ModActions.register(modEventBus);
        ModConditions.register(modEventBus);

        ModConfigs.register(ModLoadingContext.get(), modEventBus);
        ModNetworking.register(modEventBus);
        ModCommands.register();
        ModDataGen.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(OriginCapabilities.PLAYER_ORIGIN, EntityType.PLAYER, new PlayerOriginProvider());
    }
}
