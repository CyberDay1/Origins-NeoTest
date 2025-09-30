package io.github.origins.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;

public final class OriginsCommands {
    private OriginsCommands() {
    }

    public static void register(IEventBus modBus) {
        // Command registration hooks will be added in a future phase.
    }

    public static void registerCommands(Commands.CommandSelection selection, CommandSourceStack source) {
        // Commands will be registered in a future phase.
    }
}
