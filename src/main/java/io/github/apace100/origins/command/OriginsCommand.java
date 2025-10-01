package io.github.apace100.origins.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.apace100.origins.network.NetUtil;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;

public final class OriginsCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("origins")
            .then(Commands.literal("list")
                .executes(OriginsCommand::listSelf))
            .then(Commands.literal("set")
                .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("origin", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                    .executes(OriginsCommand::setOrigin))))
            .then(Commands.literal("clear")
                .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                    .executes(OriginsCommand::clearOrigin)));
    }

    private static int listSelf(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        var player = src.getPlayer();
        if (player == null) return 0;
        PlayerOrigin origin = PlayerOriginManager.get(player);
        String id = origin != null
            ? origin.getOriginIdOptional().map(ResourceLocation::toString).orElse("")
            : "";
        src.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Origin: " + (id.isEmpty() ? "<none>" : id)), false);
        return 1;
    }

    private static int setOrigin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var target = net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player");
        ResourceLocation originId = net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "origin");
        if (!PlayerOriginManager.set(target, originId)) {
            ctx.getSource().sendFailure(net.minecraft.network.chat.Component.literal("Failed to set origin."));
            return 0;
        }
        NetUtil.syncOriginTo(target, originId.toString());
        ctx.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Set origin to " + originId), true);
        return 1;
    }

    private static int clearOrigin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var target = net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player");
        PlayerOriginManager.clear(target);
        NetUtil.syncOriginTo(target, "");
        ctx.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Cleared origin"), true);
        return 1;
    }
}
