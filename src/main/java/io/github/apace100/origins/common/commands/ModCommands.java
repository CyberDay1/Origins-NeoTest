package io.github.apace100.origins.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.registry.OriginRegistry;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Origins.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ModCommands {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ORIGIN = new DynamicCommandExceptionType(id ->
        Component.translatable("commands.origins.error.unknown", id)
    );

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
                .then(Commands.literal("list").executes(context -> listOrigins(context.getSource())))
                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("origin", ResourceLocationArgument.id())
                            .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                OriginRegistry.ids().stream().map(ResourceLocation::toString).toList(), builder
                            ))
                            .executes(context -> setOrigin(
                                context.getSource(),
                                EntityArgument.getPlayer(context, "player"),
                                ResourceLocationArgument.getId(context, "origin")
                            )))))
                .then(Commands.literal("reset")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> resetOrigin(
                            context.getSource(),
                            EntityArgument.getPlayer(context, "player")
                        ))))
        );
    }

    private static int listOrigins(CommandSourceStack source) {
        var origins = OriginRegistry.values();
        if (origins.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.origins.list.empty"), false);
            return 0;
        }

        source.sendSuccess(() -> Component.translatable("commands.origins.list.header", origins.size()), false);
        origins.stream()
            .sorted((a, b) -> a.id().compareTo(b.id()))
            .forEach(origin -> source.sendSuccess(() -> describeOrigin(origin), false));
        return origins.size();
    }

    private static Component describeOrigin(Origin origin) {
        return Component.translatable(
            "commands.origins.list.entry",
            origin.name(),
            origin.id().toString(),
            origin.powers().size()
        );
    }

    private static int setOrigin(CommandSourceStack source, ServerPlayer player, ResourceLocation originId) throws CommandSyntaxException {
        Origin origin = OriginRegistry.get(originId).orElseThrow(() -> ERROR_UNKNOWN_ORIGIN.create(originId));
        PlayerOriginManager.set(player, origin.id());
        source.sendSuccess(() -> Component.translatable("commands.origins.set", player.getDisplayName(), origin.name()), true);
        return 1;
    }

    private static int resetOrigin(CommandSourceStack source, ServerPlayer player) {
        PlayerOriginManager.clear(player);
        source.sendSuccess(() -> Component.translatable("commands.origins.reset", player.getDisplayName()), true);
        return 1;
    }
}
