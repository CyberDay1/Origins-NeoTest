package io.github.apace100.origins.common.data;

import io.github.apace100.origins.Origins;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class OriginsDataReloaders {
    private static final ConfiguredActionLoader ACTION_LOADER = new ConfiguredActionLoader();
    private static final ConfiguredConditionLoader CONDITION_LOADER = new ConfiguredConditionLoader();
    private static final ConfiguredPowerLoader POWER_LOADER = new ConfiguredPowerLoader();
    private static final OriginLoader ORIGIN_LOADER = new OriginLoader();

    private OriginsDataReloaders() {
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(ACTION_LOADER);
        event.addListener(CONDITION_LOADER);
        event.addListener(POWER_LOADER);
        event.addListener(ORIGIN_LOADER);
    }
}
