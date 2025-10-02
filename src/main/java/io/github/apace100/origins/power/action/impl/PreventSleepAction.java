package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Datapack action that cancels the current sleep attempt for the invoking player.
 */
public final class PreventSleepAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "prevent_sleep");
    private static final ThreadLocal<SleepContext> CONTEXT = new ThreadLocal<>();
    private static final Codec<PreventSleepAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("message").forGetter(PreventSleepAction::message)
    ).apply(instance, PreventSleepAction::new));

    private final Optional<String> message;

    private PreventSleepAction(Optional<String> message) {
        this.message = message.filter(value -> !value.isBlank());
    }

    private Optional<String> message() {
        return message;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        SleepContext context = CONTEXT.get();
        if (context == null) {
            return;
        }

        context.cancel().run();
        message.map(Component::literal).ifPresent(component -> player.displayClientMessage(component, true));
    }

    public static PreventSleepAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<String> message = Optional.empty();
        if (json.has("message")) {
            try {
                String raw = GsonHelper.getAsString(json, "message");
                if (!raw.isBlank()) {
                    message = Optional.of(raw);
                }
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Prevent sleep action '{}' has invalid 'message': {}", id, exception.getMessage());
            }
        }

        return new PreventSleepAction(message);
    }

    public static Codec<PreventSleepAction> codec() {
        return CODEC;
    }

    public static void withContext(Runnable cancel, Runnable runnable) {
        CONTEXT.set(new SleepContext(cancel));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    private record SleepContext(Runnable cancel) {
    }
}
