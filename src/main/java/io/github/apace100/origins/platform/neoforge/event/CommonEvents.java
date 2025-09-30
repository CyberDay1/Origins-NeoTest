package io.github.apace100.origins.platform.neoforge.event;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

public final class CommonEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("Origins server lifecycle hook reached: server started.");
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        LOGGER.info("Origins registering data reload listeners (stub).");
    }
}
