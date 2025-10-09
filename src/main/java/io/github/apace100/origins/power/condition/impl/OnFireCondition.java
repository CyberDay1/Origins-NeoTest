package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import static io.github.apace100.origins.Origins.MOD_ID;

/**
 * Datapack condition that checks whether an entity is currently on fire.
 */
public final class OnFireCondition implements Condition<Entity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("on_fire");

    private OnFireCondition() {
    }

    @Override
    public boolean test(Entity entity) {
        return entity != null && entity.isOnFire();
    }

    public static OnFireCondition fromJson(ResourceLocation id, JsonObject json) {
        return new OnFireCondition();
    }
}
