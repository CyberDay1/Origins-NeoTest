package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Datapack condition that matches an incoming damage source by identifier.
 */
public final class DamageSourceCondition implements Condition<DamageSource> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "damage_source");
    private static final Codec<DamageSourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("source").forGetter(DamageSourceCondition::expectedSource)
    ).apply(instance, DamageSourceCondition::new));

    private final String expectedSource;

    private DamageSourceCondition(String expectedSource) {
        this.expectedSource = expectedSource;
    }

    private String expectedSource() {
        return expectedSource;
    }

    @Override
    public boolean test(DamageSource source) {
        if (source == null) {
            return false;
        }
        return source.getMsgId().equals(expectedSource);
    }

    public static DamageSourceCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("source")) {
            Origins.LOGGER.warn("Damage source condition '{}' is missing required 'source' field", id);
            return null;
        }

        String value = GsonHelper.getAsString(json, "source").trim();
        if (value.isEmpty()) {
            Origins.LOGGER.warn("Damage source condition '{}' specified an empty source id", id);
            return null;
        }

        return new DamageSourceCondition(value);
    }

    public static Codec<DamageSourceCondition> codec() {
        return CODEC;
    }
}
