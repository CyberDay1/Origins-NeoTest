package io.github.apace100.origins.common.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.registry.ConfiguredPowers;
import io.github.apace100.origins.common.registry.OriginRegistry;
import io.github.apace100.origins.common.config.ModConfigs;
import io.github.apace100.origins.config.OriginsConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public final class DebugCommand {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final SimpleCommandExceptionType WRITE_ERROR =
        new SimpleCommandExceptionType(Component.literal("Failed to write origins_debug.json"));

    private DebugCommand() {
    }

    public static void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("debug")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("dump").executes(DebugCommand::dump)));
    }

    private static int dump(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Path output = source.getServer().getFile("origins_debug.json");

        JsonObject payload = new JsonObject();
        JsonArray originsArray = new JsonArray();
        OriginRegistry.ids().stream()
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .map(ResourceLocation::toString)
            .forEach(originsArray::add);
        payload.add("origins", originsArray);

        JsonArray powersArray = new JsonArray();
        StreamSupport.stream(ConfiguredPowers.ids().spliterator(), false)
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .map(ResourceLocation::toString)
            .forEach(powersArray::add);
        payload.add("powers", powersArray);

        JsonObject configRoot = new JsonObject();
        JsonElement originsConfigJson = GSON.toJsonTree(OriginsConfig.get());
        configRoot.add("origins", originsConfigJson);
        JsonObject modConfig = new JsonObject();
        modConfig.addProperty("allowOrbReuse", ModConfigs.SERVER.allowOrbReuse.get());
        modConfig.addProperty("showOriginReminder", ModConfigs.CLIENT.showOriginReminder.get());
        configRoot.add("mod", modConfig);
        payload.add("config", configRoot);

        try {
            Files.writeString(output, GSON.toJson(payload), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            Origins.LOGGER.error("Failed to write debug dump", exception);
            throw WRITE_ERROR.create();
        }

        source.sendSuccess(() -> Component.literal("Origins debug dump written to " + output.toAbsolutePath()), false);
        return 1;
    }
}
