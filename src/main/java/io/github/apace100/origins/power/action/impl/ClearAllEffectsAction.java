package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * Datapack action that clears all active status effects from a living entity.
 */
public final class ClearAllEffectsAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("clear_all_effects");
    private static final ClearAllEffectsAction INSTANCE = new ClearAllEffectsAction();
    private static final Codec<ClearAllEffectsAction> CODEC = Codec.unit(INSTANCE);

    private ClearAllEffectsAction() {
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        entity.removeAllEffects();
    }

    public static ClearAllEffectsAction fromJson(ResourceLocation id, JsonObject json) {
        return INSTANCE;
    }

    public static Codec<ClearAllEffectsAction> codec() {
        return CODEC;
    }
}
