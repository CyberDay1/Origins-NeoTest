package io.github.apace100.origins;

import io.github.apace100.origins.platform.neoforge.init.ModBlocks;
import io.github.apace100.origins.platform.neoforge.init.ModItems;
import io.github.origins.core.action.Actions;
import io.github.origins.core.condition.Conditions;
import io.github.origins.core.power.Powers;
import io.github.origins.platform.neoforge.OriginsLifecycle;
import io.github.origins.platform.neoforge.config.OriginsConfig;
import io.github.origins.platform.neoforge.datagen.OriginsDataGen;
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

        Actions.ACTIONS.register(modEventBus);
        Conditions.CONDITIONS.register(modEventBus);
        Powers.POWERS.register(modEventBus);

        modEventBus.addListener(OriginsLifecycle::onCommonSetup);
        modEventBus.addListener(OriginsDataGen::onGatherData);
        modEventBus.addListener(OriginsConfig::onReload);

        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, OriginsConfig.COMMON_SPEC);

        NeoForge.EVENT_BUS.addListener(OriginsLifecycle::onRegisterCommands);
    }
}
