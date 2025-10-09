package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static io.github.apace100.origins.Origins.MOD_ID;

/**
 * Datapack condition that checks whether a player is sneaking.
 */
public final class SneakingCondition implements Condition<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("sneaking");

    private SneakingCondition() {
    }

    @Override
    public boolean test(Player player) {
        return player != null && player.isCrouching();
    }

    public static SneakingCondition fromJson(ResourceLocation id, JsonObject json) {
        return new SneakingCondition();
    }
}
