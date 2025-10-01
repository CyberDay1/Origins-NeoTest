package io.github.apace100.origins.neoforge.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.power.ConfiguredPower;
import io.github.apace100.origins.common.registry.ConfiguredActions;
import io.github.apace100.origins.common.registry.ConfiguredConditions;
import io.github.apace100.origins.common.registry.ConfiguredPowers;
import io.github.apace100.origins.common.registry.ModPowers;
import io.github.apace100.origins.common.registry.OriginRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Combined reload listener that reads the default Origins datapack structure
 * (powers and origins) and feeds the decoded data into the runtime registries.
 */
public final class OriginsDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final Codec<OriginData> ORIGIN_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ComponentSerialization.CODEC.fieldOf("name").forGetter(OriginData::name),
        ComponentSerialization.CODEC.fieldOf("description").forGetter(OriginData::description),
        ResourceLocation.CODEC.listOf().optionalFieldOf("powers", List.of()).forGetter(OriginData::powers)
    ).apply(instance, OriginData::new));
    private static final JsonGatherer POWER_GATHERER = new JsonGatherer("origins/powers");

    private Map<ResourceLocation, JsonElement> pendingPowerJson = Map.of();

    public OriginsDataLoader() {
        super(GSON, "origins/origins");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        pendingPowerJson = POWER_GATHERER.gather(resourceManager, profiler);
        return super.prepare(resourceManager, profiler);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> originJson, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, ConfiguredPower> powers = decodePowers(pendingPowerJson);
        ConfiguredPowers.setAll(powers);

        Map<ResourceLocation, Origin> origins = decodeOrigins(originJson);
        OriginRegistry.setAll(origins);

        Origins.LOGGER.info("Loaded {} origins and {} powers from datapacks", origins.size(), powers.size());
    }

    private Map<ResourceLocation, ConfiguredPower> decodePowers(Map<ResourceLocation, JsonElement> powerJson) {
        Map<ResourceLocation, ConfiguredPower> decoded = new HashMap<>();
        powerJson.forEach((id, element) -> decodePower(id, element)
            .ifPresent(power -> decoded.put(id, validatePower(id, power))));
        return decoded;
    }

    private Optional<ConfiguredPower> decodePower(ResourceLocation id, JsonElement element) {
        JsonObject json = GsonHelper.convertToJsonObject(element, "value");
        ResourceLocation typeId;
        try {
            typeId = ResourceLocation.parse(GsonHelper.getAsString(json, "type"));
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.error("Failed to parse power {}: {}", id, exception.getMessage());
            return Optional.empty();
        }

        Codec<? extends ConfiguredPower> codec = resolveCodec(typeId);
        if (codec == null) {
            Origins.LOGGER.warn("Unknown power type '{}' for data file '{}'", typeId, id);
            return Optional.empty();
        }

        DataResult<? extends ConfiguredPower> result = codec.parse(JsonOps.INSTANCE, json);
        return result.resultOrPartial(message -> Origins.LOGGER.error("Failed to decode power {}: {}", id, message))
            .map(ConfiguredPower.class::cast);
    }

    private ConfiguredPower validatePower(ResourceLocation id, ConfiguredPower power) {
        List<ResourceLocation> actions = power.actions().stream().filter(actionId -> {
            boolean present = ConfiguredActions.get(actionId).isPresent();
            if (!present) {
                Origins.LOGGER.warn("Power {} references unknown action {}", id, actionId);
            }
            return present;
        }).toList();

        if (ConfiguredConditions.get(power.condition()).isEmpty()) {
            Origins.LOGGER.warn("Power {} references unknown condition {}", id, power.condition());
        }

        return new ConfiguredPower(power.type(), power.name(), power.description(), actions, power.condition());
    }

    private Map<ResourceLocation, Origin> decodeOrigins(Map<ResourceLocation, JsonElement> originJson) {
        Map<ResourceLocation, Origin> decoded = new HashMap<>();
        originJson.forEach((id, element) -> decodeOrigin(id, element)
            .resultOrPartial(message -> Origins.LOGGER.error("Failed to decode origin {}: {}", id, message))
            .ifPresent(origin -> decoded.put(id, validateOrigin(id, origin))));
        return decoded;
    }

    private DataResult<Origin> decodeOrigin(ResourceLocation id, JsonElement element) {
        return ORIGIN_CODEC.parse(JsonOps.INSTANCE, element)
            .map(data -> new Origin(id, data.name(), data.description(), new ArrayList<>(data.powers())));
    }

    private Origin validateOrigin(ResourceLocation id, Origin origin) {
        List<ResourceLocation> powers = origin.powers().stream().filter(powerId -> {
            boolean present = ConfiguredPowers.get(powerId).isPresent();
            if (!present) {
                Origins.LOGGER.warn("Origin {} references unknown power {}", id, powerId);
            }
            return present;
        }).collect(Collectors.toList());
        return new Origin(origin.id(), origin.name(), origin.description(), powers);
    }

    private Codec<? extends ConfiguredPower> resolveCodec(ResourceLocation typeId) {
        Codec<ModPowers.PlaceholderPower> codec = findCodec(typeId);
        if (codec == null) {
            return null;
        }

        return codec.xmap(power -> new ConfiguredPower(typeId, power.name(), power.description(), power.actions(), power.condition()),
            configured -> new ModPowers.PlaceholderPower(
                configured.name(),
                configured.description(),
                List.copyOf(configured.actions()),
                configured.condition()
            ));
    }

    @SuppressWarnings("unchecked")
    private Codec<ModPowers.PlaceholderPower> findCodec(ResourceLocation typeId) {
        for (DeferredHolder<Codec<?>, ? extends Codec<?>> holder : ModPowers.POWERS.getEntries()) {
            if (holder.getId().equals(typeId)) {
                return (Codec<ModPowers.PlaceholderPower>) holder.get();
            }
        }
        return null;
    }

    private record OriginData(Component name, Component description, List<ResourceLocation> powers) {
    }

    private static final class JsonGatherer extends SimpleJsonResourceReloadListener {
        JsonGatherer(String directory) {
            super(GSON, directory);
        }

        Map<ResourceLocation, JsonElement> gather(ResourceManager resourceManager, ProfilerFiller profiler) {
            return super.prepare(resourceManager, profiler);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
            // no-op, this helper only exposes the protected prepare method
        }
    }
}
