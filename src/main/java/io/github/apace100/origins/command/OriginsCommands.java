package io.github.apace100.origins.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = OriginsConstants.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class OriginsCommands {
    private OriginsCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("origins").executes(ctx -> 0);
        event.getDispatcher().register(root);
    }
}
