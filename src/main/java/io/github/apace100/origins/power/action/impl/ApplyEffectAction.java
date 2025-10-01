package io.github.apace100.origins.power.action.impl;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

/**
 * Datapack action that applies a configured status effect to a living entity.
 */
public final class ApplyEffectAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "apply_effect");
    private static final Codec<ApplyEffectAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(ApplyEffectAction::effect),
        Codec.INT.optionalFieldOf("duration", 20).forGetter(ApplyEffectAction::duration),
        Codec.INT.optionalFieldOf("amplifier", 0).forGetter(ApplyEffectAction::amplifier),
        Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(ApplyEffectAction::showParticles),
        Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(ApplyEffectAction::showIcon)
    ).apply(instance, ApplyEffectAction::new));

    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;
    private final boolean showParticles;
    private final boolean showIcon;

    private ApplyEffectAction(Holder<MobEffect> effect, int duration, int amplifier, boolean showParticles, boolean showIcon) {
        this.effect = effect;
        this.duration = Math.max(duration, 0);
        this.amplifier = Math.max(amplifier, 0);
        this.showParticles = showParticles;
        this.showIcon = showIcon;
    }

    private Holder<MobEffect> effect() {
        return effect;
    }

    private int duration() {
        return duration;
    }

    private int amplifier() {
        return amplifier;
    }

    private boolean showParticles() {
        return showParticles;
    }

    private boolean showIcon() {
        return showIcon;
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null || duration <= 0) {
            return;
        }
        MobEffectInstance instance = new MobEffectInstance(effect, duration, amplifier, false, showParticles, showIcon);
        entity.addEffect(instance);
    }

    public static ApplyEffectAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("effect")) {
            Origins.LOGGER.warn("Apply effect action '{}' is missing required 'effect' field", id);
            return null;
        }

        String rawEffect = GsonHelper.getAsString(json, "effect");
        ResourceLocation effectId;
        try {
            effectId = ResourceLocation.parse(rawEffect);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Apply effect action '{}' has invalid effect id '{}': {}", id, rawEffect, exception.getMessage());
            return null;
        }

        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(effectId);
        if (effect.isEmpty()) {
            Origins.LOGGER.warn("Apply effect action '{}' references unknown effect '{}'", id, effectId);
            return null;
        }

        int duration = GsonHelper.getAsInt(json, "duration", 20);
        if (duration <= 0) {
            Origins.LOGGER.warn("Apply effect action '{}' has non-positive duration {}", id, duration);
            return null;
        }

        int amplifier = GsonHelper.getAsInt(json, "amplifier", 0);
        if (amplifier < 0) {
            Origins.LOGGER.warn("Apply effect action '{}' has negative amplifier {}", id, amplifier);
            return null;
        }

        boolean showParticles = GsonHelper.getAsBoolean(json, "show_particles", true);
        boolean showIcon = GsonHelper.getAsBoolean(json, "show_icon", true);
        return new ApplyEffectAction(effect.get(), duration, amplifier, showParticles, showIcon);
    }

    public static Codec<ApplyEffectAction> codec() {
        return CODEC;
    }
}
