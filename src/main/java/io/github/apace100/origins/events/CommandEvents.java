package io.github.apace100.origins.events;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.command.OriginsCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Origins.MOD_ID)
public final class CommandEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(OriginsCommand.build());
    }
}
