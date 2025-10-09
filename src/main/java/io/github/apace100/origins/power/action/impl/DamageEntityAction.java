package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

/**
 * Datapack action that applies damage to the context entity.
 */
public final class DamageEntityAction implements Action<Entity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("damage_entity");
    private static final Codec<DamageEntityAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("amount").forGetter(DamageEntityAction::amount),
        ResourceLocation.CODEC.fieldOf("source").forGetter(DamageEntityAction::sourceId)
    ).apply(instance, DamageEntityAction::new));

    private final float amount;
    private final ResourceLocation sourceId;
    private boolean missingDamageTypeLogged;

    private DamageEntityAction(float amount, ResourceLocation sourceId) {
        this.amount = amount;
        this.sourceId = sourceId;
    }

    private float amount() {
        return amount;
    }

    private ResourceLocation sourceId() {
        return sourceId;
    }

    @Override
    public void execute(Entity entity) {
        if (entity == null || amount <= 0) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, sourceId);
        Holder<DamageType> holder = registry.getHolder(key).orElse(null);
        if (holder == null) {
            if (!missingDamageTypeLogged) {
                Origins.LOGGER.warn("Damage entity action could not resolve damage type '{}'", sourceId);
                missingDamageTypeLogged = true;
            }
            return;
        }

        entity.hurt(new DamageSource(holder), amount);
    }

    public static DamageEntityAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("amount")) {
            Origins.LOGGER.warn("Damage entity action '{}' is missing required 'amount' field", id);
            return null;
        }
        if (!json.has("source")) {
            Origins.LOGGER.warn("Damage entity action '{}' is missing required 'source' field", id);
            return null;
        }

        float amount = (float) GsonHelper.getAsDouble(json, "amount");
        if (amount <= 0) {
            Origins.LOGGER.warn("Damage entity action '{}' has non-positive amount {}", id, amount);
            return null;
        }

        String rawSource = GsonHelper.getAsString(json, "source");
        ResourceLocation sourceId;
        try {
            sourceId = ResourceLocation.parse(rawSource);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Damage entity action '{}' has invalid damage source id '{}': {}", id, rawSource, exception.getMessage());
            return null;
        }

        return new DamageEntityAction(amount, sourceId);
    }

    public static Codec<DamageEntityAction> codec() {
        return CODEC;
    }
}
