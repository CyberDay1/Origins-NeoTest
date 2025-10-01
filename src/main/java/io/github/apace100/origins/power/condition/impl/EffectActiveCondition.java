package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

/**
 * Datapack condition that checks whether a living entity has an active status effect.
 */
public final class EffectActiveCondition implements Condition<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "effect_active");
    private static final Codec<EffectActiveCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(EffectActiveCondition::effect)
    ).apply(instance, EffectActiveCondition::new));

    private final Holder<MobEffect> effect;

    private EffectActiveCondition(Holder<MobEffect> effect) {
        this.effect = effect;
    }

    private Holder<MobEffect> effect() {
        return effect;
    }

    @Override
    public boolean test(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        return entity.hasEffect(effect);
    }

    public static EffectActiveCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("effect")) {
            Origins.LOGGER.warn("Effect active condition '{}' is missing required 'effect' field", id);
            return null;
        }

        String rawEffect = GsonHelper.getAsString(json, "effect");
        ResourceLocation effectId;
        try {
            effectId = ResourceLocation.parse(rawEffect);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Effect active condition '{}' has invalid effect id '{}': {}", id, rawEffect, exception.getMessage());
            return null;
        }

        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(effectId);
        if (effect.isEmpty()) {
            Origins.LOGGER.warn("Effect active condition '{}' references unknown effect '{}'", id, effectId);
            return null;
        }

        return new EffectActiveCondition(effect.get());
    }

    public static Codec<EffectActiveCondition> codec() {
        return CODEC;
    }
}
