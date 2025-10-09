package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Datapack action that scales the fall distance and resulting damage for the current fall event.
 */
public final class ModifyFallingAction implements Action<Player> {
    private static final double MIN_MULTIPLIER = 0.0D;

    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("modify_falling");
    private static final ThreadLocal<FallContext> CONTEXT = new ThreadLocal<>();
    private static final Codec<ModifyFallingAction> CODEC = RecordCodecBuilder.<ModifyFallingAction>create(instance -> instance.group(
        Codec.DOUBLE.fieldOf("multiplier").forGetter(ModifyFallingAction::multiplier)
    ).apply(instance, multiplier -> new ModifyFallingAction(multiplier))).flatXmap(ModifyFallingAction::validate, ModifyFallingAction::validate);

    private final Optional<ResourceLocation> sourceId;
    private final double multiplier;

    private ModifyFallingAction(double multiplier) {
        this(Optional.empty(), multiplier);
    }

    private ModifyFallingAction(Optional<ResourceLocation> sourceId, double multiplier) {
        this.sourceId = sourceId;
        this.multiplier = multiplier;
    }

    private double multiplier() {
        return multiplier;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        if (!applyToCurrentContext(multiplier)) {
            Origins.LOGGER.warn(
                "Modify falling action '{}' executed without fall context for player {}",
                sourceId.map(ResourceLocation::toString).orElse("<untracked>"),
                player.getGameProfile().getName()
            );
        }
    }

    public static ModifyFallingAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("multiplier")) {
            Origins.LOGGER.warn("Modify falling action '{}' is missing required 'multiplier' field", id);
            return null;
        }

        double multiplier;
        try {
            multiplier = GsonHelper.getAsDouble(json, "multiplier");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Modify falling action '{}' has invalid 'multiplier': {}", id, exception.getMessage());
            return null;
        }

        ModifyFallingAction action = new ModifyFallingAction(Optional.of(id), multiplier);
        DataResult<ModifyFallingAction> validation = validate(action);
        return validation.resultOrPartial(message ->
            Origins.LOGGER.warn("Modify falling action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<ModifyFallingAction> codec() {
        return CODEC;
    }

    public static void withContext(float distance, float damageMultiplier, BiConsumer<Float, Float> applier, Runnable runnable) {
        CONTEXT.set(new FallContext(distance, damageMultiplier, applier));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    public static boolean applyToCurrentContext(double multiplier) {
        FallContext context = CONTEXT.get();
        if (context == null) {
            return false;
        }
        context.apply(multiplier);
        return true;
    }

    private static DataResult<ModifyFallingAction> validate(ModifyFallingAction action) {
        if (!Double.isFinite(action.multiplier) || action.multiplier < MIN_MULTIPLIER) {
            return DataResult.error(() -> "multiplier must be at least " + MIN_MULTIPLIER);
        }
        return DataResult.success(action);
    }

    private record FallContext(float distance, float damageMultiplier, BiConsumer<Float, Float> applier) {
        void apply(double multiplier) {
            float scaledDistance = (float) (distance * multiplier);
            float scaledDamage = (float) (damageMultiplier * multiplier);
            applier.accept(scaledDistance, scaledDamage);
        }
    }
}
