package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

/**
 * Datapack action that executes a server command with the player as the source.
 */
public final class ExecuteCommandAction implements Action<ServerPlayer> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("execute_command");
    private static final Codec<ExecuteCommandAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("command").forGetter(ExecuteCommandAction::command)
    ).apply(instance, ExecuteCommandAction::new));

    private final String command;

    private ExecuteCommandAction(String command) {
        this.command = command;
    }

    private String command() {
        return command;
    }

    @Override
    public void execute(ServerPlayer player) {
        if (player == null) {
            return;
        }

        MinecraftServer server = player.getServer();
        if (server == null || command.isBlank()) {
            return;
        }

        CommandSourceStack source = player.createCommandSourceStack()
            .withPermission(server.getOperatorUserPermissionLevel())
            .withSuppressedOutput();
        server.getCommands().performPrefixedCommand(source, command);
    }

    public static ExecuteCommandAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("command")) {
            Origins.LOGGER.warn("Execute command action '{}' is missing required 'command' field", id);
            return null;
        }

        String command = GsonHelper.getAsString(json, "command").trim();
        if (command.isEmpty()) {
            Origins.LOGGER.warn("Execute command action '{}' specified an empty command", id);
            return null;
        }

        return new ExecuteCommandAction(command);
    }

    public static Codec<ExecuteCommandAction> codec() {
        return CODEC;
    }
}
