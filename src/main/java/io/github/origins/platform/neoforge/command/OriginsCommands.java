package io.github.origins.platform.neoforge.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class OriginsCommands {
    private OriginsCommands() {}

    public static void onRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        // TODO: add /origins test command for smoke test
        // d.register(Commands.literal("origins").executes(ctx -> 1));
    }
}
