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
import net.minecraft.world.entity.LivingEntity;

/**
 * Datapack action that adjusts the amount of damage dealt by the invoking
 * entity's next attack.
 */
public final class ModifyDamageDealtAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("modify_damage_dealt");
    private static final Codec<ModifyDamageDealtAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("amount").forGetter(ModifyDamageDealtAction::amount)
    ).apply(instance, ModifyDamageDealtAction::new));

    private final float amount;

    private ModifyDamageDealtAction(float amount) {
        this.amount = amount;
    }

    private float amount() {
        return amount;
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null || amount == 0.0F) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel)) {
            return;
        }

        CombatActionHandler.queueBonusDamage(entity, amount);
    }

    public static ModifyDamageDealtAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("amount")) {
            Origins.LOGGER.warn("Modify damage dealt action '{}' is missing required 'amount' field", id);
            return null;
        }

        float amount;
        try {
            amount = GsonHelper.getAsFloat(json, "amount");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Modify damage dealt action '{}' has invalid 'amount': {}", id, exception.getMessage());
            return null;
        }

        if (amount == 0.0F) {
            Origins.LOGGER.warn("Modify damage dealt action '{}' specified a zero modifier; ignoring", id);
            return null;
        }

        return new ModifyDamageDealtAction(amount);
    }

    public static Codec<ModifyDamageDealtAction> codec() {
        return CODEC;
    }
}
