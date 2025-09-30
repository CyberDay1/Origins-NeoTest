package io.github.origins.platform.neoforge;

import io.github.origins.platform.neoforge.command.OriginsCommands;
import io.github.origins.platform.neoforge.net.OriginsNetworking;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class OriginsLifecycle {
    private OriginsLifecycle() {}

    public static void onCommonSetup(final FMLCommonSetupEvent e) {
        // Networking registration and other cross-thread bootstrap
        OriginsNetworking.register();
    }

    public static void onRegisterCommands(final RegisterCommandsEvent e) {
        OriginsCommands.onRegister(e);
    }
}
