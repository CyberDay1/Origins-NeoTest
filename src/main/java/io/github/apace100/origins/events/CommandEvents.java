package io.github.apace100.origins.events;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.command.OriginsCommand;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class CommandEvents {
    public static void registerCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(OriginsCommand.build());
    }
}
