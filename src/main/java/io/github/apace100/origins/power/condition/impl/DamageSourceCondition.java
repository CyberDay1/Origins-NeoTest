package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Optional;

/**
 * Scaffold implementation for damage source datapack conditions.
 */
public final class DamageSourceCondition implements Condition<DamageSource> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "damage_source");
    private static final Codec<DamageSourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("tag").forGetter(DamageSourceCondition::tag)
    ).apply(instance, DamageSourceCondition::new));

    private final Optional<String> tag;

    private DamageSourceCondition(Optional<String> tag) {
        this.tag = tag;
    }

    public Optional<String> tag() {
        return tag;
    }

    @Override
    public boolean test(DamageSource source) {
        // TODO: Inspect the damage source against the configured tag or identifier.
        return false;
    }

    public static DamageSourceCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<String> parsed = Optional.empty();
        if (json.has("tag")) {
            String raw = GsonHelper.getAsString(json, "tag");
            if (raw.isBlank()) {
                Origins.LOGGER.warn("Damage source condition '{}' specified an empty tag", id);
                return null;
            }
            parsed = Optional.of(raw);
        }
        return new DamageSourceCondition(parsed);
    }

    public static Codec<DamageSourceCondition> codec() {
        return CODEC;
    }
}
