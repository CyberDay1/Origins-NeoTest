package io.github.apace100.origins.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.registry.OriginRegistry;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ModCommands {
    private ModCommands() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModCommands::onRegisterCommands);
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(Origins.MOD_ID)
            .then(Commands.literal("list").executes(ModCommands::list))
            .then(Commands.literal("set")
                .then(Commands.argument("origin", ResourceLocationArgument.id())
                    .executes(ModCommands::set)))
            .then(Commands.literal("clear")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ModCommands::clear))));
    }

    private static int list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerOrigin origin = PlayerOriginManager.get(player);
        if (origin != null && origin.getOriginIdOptional().isPresent()) {
            ResourceLocation originId = origin.getOriginIdOptional().get();
            Component originName = OriginRegistry.get(originId)
                .map(Origin::name)
                .orElse(Component.literal(originId.toString()));
            context.getSource().sendSuccess(() -> Component.translatable("commands.origins.list", originName, originId), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("commands.origins.list.empty"), false);
        }
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ResourceLocation originId = ResourceLocationArgument.getId(context, "origin");
        Origin definition = OriginRegistry.get(originId).orElse(null);
        if (definition == null) {
            context.getSource().sendFailure(Component.translatable("commands.origins.error.unknown", originId));
            return 0;
        }
        if (!PlayerOriginManager.set(player, originId)) {
            context.getSource().sendFailure(Component.translatable("commands.origins.error.unavailable"));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.origins.set", player.getDisplayName(), definition.name()), true);
        return 1;
    }

    private static int clear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        PlayerOriginManager.clear(target);
        context.getSource().sendSuccess(() -> Component.translatable("commands.origins.clear", target.getDisplayName()), true);
        return 1;
    }
}
