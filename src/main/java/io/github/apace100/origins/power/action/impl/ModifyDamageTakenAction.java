package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Datapack action which adjusts the most recent damage taken by a living entity.
 */
public final class ModifyDamageTakenAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("modify_damage_taken");
    private static final Codec<ModifyDamageTakenAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("amount").forGetter(ModifyDamageTakenAction::amount)
    ).apply(instance, ModifyDamageTakenAction::new));

    private final float amount;

    private ModifyDamageTakenAction(float amount) {
        this.amount = amount;
    }

    private float amount() {
        return amount;
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null || amount == 0) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        if (amount < 0) {
            entity.heal(-amount);
            return;
        }

        DamageSource source = entity.getLastDamageSource();
        if (source == null) {
            source = level.damageSources().generic();
        }
        entity.hurt(source, amount);
    }

    public static ModifyDamageTakenAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("amount")) {
            Origins.LOGGER.warn("Modify damage taken action '{}' is missing required 'amount' field", id);
            return null;
        }

        float amount;
        try {
            amount = GsonHelper.getAsFloat(json, "amount");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Modify damage taken action '{}' has invalid 'amount': {}", id, exception.getMessage());
            return null;
        }

        if (amount == 0) {
            Origins.LOGGER.warn("Modify damage taken action '{}' specified a zero modifier; ignoring", id);
            return null;
        }

        return new ModifyDamageTakenAction(amount);
    }

    public static Codec<ModifyDamageTakenAction> codec() {
        return CODEC;
    }
}
