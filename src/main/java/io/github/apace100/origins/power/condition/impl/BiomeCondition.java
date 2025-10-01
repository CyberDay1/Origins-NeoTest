package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;

/**
 * Scaffold implementation for biome datapack conditions.
 */
public final class BiomeCondition implements Condition<Holder<Biome>> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "biome");
    private static final Codec<BiomeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(BiomeCondition::biomeKey)
    ).apply(instance, BiomeCondition::new));

    private final ResourceKey<Biome> biomeKey;

    private BiomeCondition(ResourceKey<Biome> biomeKey) {
        this.biomeKey = biomeKey;
    }

    public ResourceKey<Biome> biomeKey() {
        return biomeKey;
    }

    @Override
    public boolean test(Holder<Biome> biome) {
        return biome != null && biome.is(biomeKey);
    }

    public static BiomeCondition fromJson(ResourceLocation id, JsonObject json) {
        String raw = GsonHelper.getAsString(json, "biome", "");
        if (raw.isEmpty()) {
            Origins.LOGGER.warn("Biome condition '{}' is missing required 'biome' field", id);
            return null;
        }
        try {
            ResourceLocation biomeId = ResourceLocation.parse(raw);
            return new BiomeCondition(ResourceKey.create(Registries.BIOME, biomeId));
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Biome condition '{}' has invalid biome id '{}': {}", id, raw, exception.getMessage());
            return null;
        }
    }

    public static Codec<BiomeCondition> codec() {
        return CODEC;
    }
}
