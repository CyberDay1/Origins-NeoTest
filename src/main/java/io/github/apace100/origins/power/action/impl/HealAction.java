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
 * Datapack action that heals a living entity by a fixed amount.
 */
public final class HealAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("heal");
    private static final Codec<HealAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("amount").forGetter(HealAction::amount)
    ).apply(instance, HealAction::new));

    private final float amount;

    private HealAction(float amount) {
        this.amount = amount;
    }

    private float amount() {
        return amount;
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null || amount <= 0) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel)) {
            return;
        }
        entity.heal(amount);
    }

    public static HealAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("amount")) {
            Origins.LOGGER.warn("Heal action '{}' is missing required 'amount' field", id);
            return null;
        }

        float amount;
        try {
            amount = GsonHelper.getAsFloat(json, "amount");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Heal action '{}' has invalid 'amount': {}", id, exception.getMessage());
            return null;
        }

        if (amount <= 0) {
            Origins.LOGGER.warn("Heal action '{}' specified non-positive amount {}", id, amount);
            return null;
        }

        return new HealAction(amount);
    }

    public static Codec<HealAction> codec() {
        return CODEC;
    }
}
