package io.github.apace100.origins.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.registry.ConfiguredPowers;
import io.github.apace100.origins.common.registry.OriginRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OriginLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final Codec<OriginData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ComponentSerialization.CODEC.fieldOf("name").forGetter(OriginData::name),
        ComponentSerialization.CODEC.fieldOf("description").forGetter(OriginData::description),
        ResourceLocation.CODEC.listOf().optionalFieldOf("powers", List.of()).forGetter(OriginData::powers)
    ).apply(instance, OriginData::new));

    public OriginLoader() {
        super(GSON, "origins/origins");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Origin> resolved = new HashMap<>();
        object.forEach((id, element) -> decodeOrigin(id, element)
            .resultOrPartial(message -> Origins.LOGGER.error("Failed to decode origin {}: {}", id, message))
            .ifPresent(origin -> resolved.put(id, origin)));
        OriginRegistry.setAll(resolved);
    }

    private DataResult<Origin> decodeOrigin(ResourceLocation id, JsonElement element) {
        return CODEC.parse(JsonOps.INSTANCE, element)
            .map(data -> {
                List<ResourceLocation> resolvedPowers = data.powers().stream().filter(powerId -> {
                    boolean present = ConfiguredPowers.get(powerId).isPresent();
                    if (!present) {
                        Origins.LOGGER.warn("Origin {} references unknown power {}", id, powerId);
                    }
                    return present;
                }).toList();
                return new Origin(id, data.name(), data.description(), resolvedPowers);
            });
    }

    private record OriginData(Component name, Component description, List<ResourceLocation> powers) {
    }
}
