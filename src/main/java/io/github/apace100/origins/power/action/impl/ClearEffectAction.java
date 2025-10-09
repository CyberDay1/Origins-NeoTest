package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

/**
 * Datapack action that clears a specific status effect from a living entity.
 */
public final class ClearEffectAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("clear_effect");
    private static final Codec<ClearEffectAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(ClearEffectAction::effect)
    ).apply(instance, ClearEffectAction::new));

    private final Holder<MobEffect> effect;

    private ClearEffectAction(Holder<MobEffect> effect) {
        this.effect = effect;
    }

    private Holder<MobEffect> effect() {
        return effect;
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        entity.removeEffect(effect);
    }

    public static ClearEffectAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("effect")) {
            Origins.LOGGER.warn("Clear effect action '{}' is missing required 'effect' field", id);
            return null;
        }

        String rawEffect = GsonHelper.getAsString(json, "effect");
        ResourceLocation effectId;
        try {
            effectId = ResourceLocation.parse(rawEffect);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Clear effect action '{}' has invalid effect id '{}': {}", id, rawEffect, exception.getMessage());
            return null;
        }

        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(effectId);
        if (effect.isEmpty()) {
            Origins.LOGGER.warn("Clear effect action '{}' references unknown effect '{}'", id, effectId);
            return null;
        }

        return new ClearEffectAction(effect.get());
    }

    public static Codec<ClearEffectAction> codec() {
        return CODEC;
    }
}
