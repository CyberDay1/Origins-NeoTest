package io.github.apace100.origins.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.apace100.origins.Origins;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ModCommands {
    private ModCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal(Origins.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload").executes(context -> {
                    CommandSourceStack source = context.getSource();
                    source.sendSuccess(() -> Component.translatable("commands.origins.reload_soon"), true);
                    return 1;
                }))
        );
    }
}
