package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

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
import net.minecraft.util.GsonHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

/**
 * Scaffold implementation for biome datapack conditions.
 */
public final class BiomeCondition implements Condition<ServerLevel> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("biome");
    private static final ThreadLocal<Optional<BlockPos>> CONTEXT = new ThreadLocal<>();
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
    public boolean test(ServerLevel level) {
        if (level == null) {
            return false;
        }

        Optional<BlockPos> pos = Optional.ofNullable(CONTEXT.get()).flatMap(optional -> optional);
        if (pos.isEmpty()) {
            return false;
        }

        Holder<Biome> biome = level.getBiome(pos.get());
        return biome.is(biomeKey);
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
