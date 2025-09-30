package io.github.origins.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class OriginsCommands {
    private OriginsCommands() {
    }

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(OriginsCommands::onRegisterCommands);
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("origins")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    context.getSource().sendSuccess(
                        () -> Component.translatable("commands.origins.reload_soon"),
                        true
                    );
                    return 1;
                })
        );
    }
}
