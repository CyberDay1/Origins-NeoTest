package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Datapack condition that checks whether the current biome is included in a configured whitelist.
 */
public final class BiomeWhitelistCondition implements Condition<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("biome_whitelist");
    private static final ThreadLocal<Optional<BlockPos>> CONTEXT = new ThreadLocal<>();
    private static final Codec<BiomeWhitelistCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biomes")
            .forGetter(condition -> List.copyOf(condition.biomes))
    ).apply(instance, keys -> new BiomeWhitelistCondition(new HashSet<>(keys))));

    private final Set<ResourceKey<Biome>> biomes;

    private BiomeWhitelistCondition(Set<ResourceKey<Biome>> biomes) {
        this.biomes = biomes;
    }

    private Set<ResourceKey<Biome>> biomes() {
        return biomes;
    }

    @Override
    public boolean test(ServerLevel level) {
        if (level == null) {
            return false;
        }

        Optional<BlockPos> pos = Optional.ofNullable(CONTEXT.get()).flatMap(optional -> optional);
        if (pos.isEmpty()) {
            return false;
        }

        Holder<Biome> biome = level.getBiome(pos.get());
        return biomes.stream().anyMatch(biome::is);
    }

    public static BiomeWhitelistCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("biomes")) {
            Origins.LOGGER.warn("Biome whitelist condition '{}' is missing required 'biomes' field", id);
            return null;
        }

        JsonArray array = GsonHelper.getAsJsonArray(json, "biomes");
        if (array.isEmpty()) {
            Origins.LOGGER.warn("Biome whitelist condition '{}' provided an empty 'biomes' array", id);
            return null;
        }

        Set<ResourceKey<Biome>> keys = new HashSet<>();
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                Origins.LOGGER.warn("Biome whitelist condition '{}' has non-string biome entry {}", id, element);
                return null;
            }

            String raw = element.getAsString();
            ResourceLocation biomeId;
            try {
                biomeId = ResourceLocation.parse(raw);
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Biome whitelist condition '{}' has invalid biome id '{}': {}", id, raw, exception.getMessage());
                return null;
            }

            keys.add(ResourceKey.create(Registries.BIOME, biomeId));
        }

        return new BiomeWhitelistCondition(keys);
    }

    public static Codec<BiomeWhitelistCondition> codec() {
        return CODEC;
    }

    public static void withContext(BlockPos pos, Runnable runnable) {
        Optional<BlockPos> previous = CONTEXT.get();
        CONTEXT.set(Optional.ofNullable(pos));
        try {
            runnable.run();
        } finally {
            if (previous == null) {
                CONTEXT.remove();
            } else {
                CONTEXT.set(previous);
            }
        }
    }
}
