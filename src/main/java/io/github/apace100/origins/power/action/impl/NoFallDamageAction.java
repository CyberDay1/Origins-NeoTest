package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Datapack action that cancels fall damage for the current fall event.
 */
public final class NoFallDamageAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("no_fall_damage");
    private static final Codec<NoFallDamageAction> CODEC = Codec.unit(new NoFallDamageAction());

    private NoFallDamageAction() {
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        if (!ModifyFallingAction.applyToCurrentContext(0.0D)) {
            Origins.LOGGER.warn(
                "No fall damage action executed without fall context for player {}",
                player.getGameProfile().getName()
            );
        }
    }

    public static NoFallDamageAction fromJson(ResourceLocation id, JsonObject json) {
        return new NoFallDamageAction();
    }

    public static Codec<NoFallDamageAction> codec() {
        return CODEC;
    }
}
