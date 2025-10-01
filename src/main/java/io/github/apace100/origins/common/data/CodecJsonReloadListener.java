package io.github.apace100.origins.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class CodecJsonReloadListener<T> extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setLenient().create();

    protected CodecJsonReloadListener(String directory) {
        super(GSON, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, T> resolved = new HashMap<>();
        object.forEach((id, element) -> decodeEntry(id, element, resolved::put));
        applyResolved(resolved);
    }

    private void decodeEntry(ResourceLocation id, JsonElement element, BiConsumer<ResourceLocation, T> registrar) {
        JsonObject json = GsonHelper.convertToJsonObject(element, "value");
        ResourceLocation typeId = ResourceLocation.parse(GsonHelper.getAsString(json, "type"));
        Codec<? extends T> codec = resolveCodec(typeId);
        if (codec == null) {
            Origins.LOGGER.warn("Unknown codec '{}' for data file '{}'", typeId, id);
            return;
        }

        DataResult<? extends T> result = codec.parse(JsonOps.INSTANCE, json);
        result.resultOrPartial(message -> Origins.LOGGER.error("Failed to decode {}: {}", id, message))
            .ifPresent(value -> registrar.accept(id, wrap(typeId, value)));
    }

    protected abstract Codec<? extends T> resolveCodec(ResourceLocation typeId);

    protected T wrap(ResourceLocation typeId, T value) {
        return value;
    }

    protected abstract void applyResolved(Map<ResourceLocation, T> values);
}
