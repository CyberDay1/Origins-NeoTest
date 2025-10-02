package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

/**
 * Datapack action that applies a critical hit multiplier to the next outgoing
 * attack if the configured probability check succeeds.
 */
public final class CriticalHitAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "critical_hit");
    private static final Codec<CriticalHitAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("chance").forGetter(CriticalHitAction::chance),
        Codec.FLOAT.fieldOf("multiplier").forGetter(CriticalHitAction::multiplier)
    ).apply(instance, CriticalHitAction::new));

    private final float chance;
    private final float multiplier;

    private CriticalHitAction(float chance, float multiplier) {
        this.chance = chance;
        this.multiplier = multiplier;
    }

    private float chance() {
        return chance;
    }

    private float multiplier() {
        return multiplier;
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel)) {
            return;
        }

        CombatActionHandler.queueCritical(entity, chance, multiplier);
    }

    public static CriticalHitAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("chance")) {
            Origins.LOGGER.warn("Critical hit action '{}' is missing required 'chance' field", id);
            return null;
        }
        if (!json.has("multiplier")) {
            Origins.LOGGER.warn("Critical hit action '{}' is missing required 'multiplier' field", id);
            return null;
        }

        float chance;
        float multiplier;
        try {
            chance = GsonHelper.getAsFloat(json, "chance");
            multiplier = GsonHelper.getAsFloat(json, "multiplier");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Critical hit action '{}' has invalid numeric value: {}", id, exception.getMessage());
            return null;
        }

        if (chance < 0.0F || chance > 1.0F) {
            Origins.LOGGER.warn("Critical hit action '{}' specified out-of-range chance {}; expected 0.0-1.0", id, chance);
            return null;
        }
        if (multiplier <= 0.0F) {
            Origins.LOGGER.warn("Critical hit action '{}' specified non-positive multiplier {}", id, multiplier);
            return null;
        }

        return new CriticalHitAction(chance, multiplier);
    }

    public static Codec<CriticalHitAction> codec() {
        return CODEC;
    }
}
