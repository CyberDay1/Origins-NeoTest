package io.github.apace100.origins.common.data;

import io.github.apace100.origins.Origins;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = Origins.MOD_ID)
public final class OriginsDataReloaders {
    private static final ConfiguredActionLoader ACTION_LOADER = new ConfiguredActionLoader();
    private static final ConfiguredConditionLoader CONDITION_LOADER = new ConfiguredConditionLoader();

    private OriginsDataReloaders() {
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(ACTION_LOADER);
        event.addListener(CONDITION_LOADER);
    }
}
