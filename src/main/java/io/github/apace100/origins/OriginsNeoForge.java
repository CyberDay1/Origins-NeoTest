package io.github.apace100.origins;

import io.github.apace100.origins.platform.neoforge.event.CommonEvents;
import io.github.apace100.origins.platform.neoforge.init.ModBlocks;
import io.github.apace100.origins.platform.neoforge.init.ModCommands;
import io.github.apace100.origins.platform.neoforge.init.ModConfigs;
import io.github.apace100.origins.platform.neoforge.init.ModDataGen;
import io.github.apace100.origins.platform.neoforge.init.ModItems;
import io.github.apace100.origins.platform.neoforge.init.ModPackets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(OriginsNeoForge.MOD_ID)
public final class OriginsNeoForge {
    public static final String MOD_ID = "origins";

    public OriginsNeoForge(IEventBus modEventBus) {
        ModItems.REGISTER.register(modEventBus);
        ModBlocks.REGISTER.register(modEventBus);

        modEventBus.addListener(ModDataGen::gatherData);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON_SPEC);

        ModPackets.register();

        NeoForge.EVENT_BUS.register(new CommonEvents());
        NeoForge.EVENT_BUS.addListener(ModCommands::register);
    }
}
