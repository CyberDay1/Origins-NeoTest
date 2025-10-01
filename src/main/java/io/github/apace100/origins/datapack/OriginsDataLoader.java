package io.github.apace100.origins.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.origin.Origin;
import io.github.apace100.origins.common.origin.OriginImpact;
import io.github.apace100.origins.common.power.ConfiguredPower;
import io.github.apace100.origins.common.registry.ConfiguredActions;
import io.github.apace100.origins.common.registry.ConfiguredConditions;
import io.github.apace100.origins.common.registry.ConfiguredPowers;
import io.github.apace100.origins.common.registry.ModPowers;
import io.github.apace100.origins.common.registry.OriginRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
        ResourceLocation.CODEC.listOf().optionalFieldOf("powers", List.of()).forGetter(OriginData::powers),
        ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(OriginData::icon),
        Codec.INT.optionalFieldOf("impact").forGetter(OriginData::impact)
    ).apply(instance, OriginData::new));
    private static final JsonGatherer POWER_GATHERER = new JsonGatherer("origins/powers");
    private static final Map<ResourceLocation, ResourceLocation> POWER_ALIASES = Map.of(
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytra"),
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytra_flight"),
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytrian_flight"),
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytra_flight")
    );

    private static volatile ReloadStats LAST_STATS = ReloadStats.empty();

    private Map<ResourceLocation, JsonElement> pendingPowerJson = Map.of();
    private int skippedPowers;
    private int skippedOrigins;
    private final Set<ResourceLocation> unknownPowerTypes = new HashSet<>();

    public OriginsDataLoader() {
        super(GSON, "origins/origins");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        skippedPowers = 0;
        skippedOrigins = 0;
        unknownPowerTypes.clear();
        pendingPowerJson = POWER_GATHERER.gather(resourceManager, profiler);
        return super.prepare(resourceManager, profiler);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> originJson, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, ConfiguredPower> powers = decodePowers(pendingPowerJson);
        ConfiguredPowers.setAll(powers);

        Map<ResourceLocation, Origin> origins = decodeOrigins(originJson);
        OriginRegistry.setAll(origins);

        List<ResourceLocation> unknownTypes = unknownPowerTypes.stream()
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .toList();
        LAST_STATS = new ReloadStats(origins.size(), powers.size(), skippedOrigins + skippedPowers, unknownTypes);

        if (skippedOrigins + skippedPowers > 0) {
            Origins.LOGGER.info("Loaded {} origins and {} powers from datapacks ({} entries skipped)",
                origins.size(), powers.size(), skippedOrigins + skippedPowers);
        } else {
            Origins.LOGGER.info("Loaded {} origins and {} powers from datapacks", origins.size(), powers.size());
        }
    }

    private Map<ResourceLocation, ConfiguredPower> decodePowers(Map<ResourceLocation, JsonElement> powerJson) {
        Map<ResourceLocation, ConfiguredPower> decoded = new HashMap<>();
        powerJson.forEach((id, element) -> decodePower(id, element)
            .ifPresent(power -> decoded.put(id, validatePower(id, power))));
        return decoded;
    }

    private Optional<ConfiguredPower> decodePower(ResourceLocation id, JsonElement element) {
        JsonObject json = GsonHelper.convertToJsonObject(element, "value");
        NormalizedPowerJson normalized = normalizePowerJson(id, json);
        if (normalized == null) {
            skippedPowers++;
            return Optional.empty();
        }

        Codec<? extends ConfiguredPower> codec = resolveCodec(normalized.type());
        if (codec == null) {
            Origins.LOGGER.warn("Unknown power type '{}' for data file '{}'", normalized.type(), id);
            unknownPowerTypes.add(normalized.type());
            skippedPowers++;
            return Optional.empty();
        }

        DataResult<? extends ConfiguredPower> result = codec.parse(JsonOps.INSTANCE, normalized.json());
        Optional<? extends ConfiguredPower> parsed = result.resultOrPartial(message ->
            Origins.LOGGER.error("Failed to decode power {}: {}", id, message));
        if (parsed.isEmpty()) {
            skippedPowers++;
            return Optional.empty();
        }
        return parsed.map(ConfiguredPower.class::cast);
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
        originJson.forEach((id, element) -> {
            DataResult<Origin> result = decodeOrigin(id, element);
            Optional<Origin> origin = result.resultOrPartial(message -> {
                Origins.LOGGER.error("Failed to decode origin {}: {}", id, message);
                skippedOrigins++;
            });
            origin.ifPresent(value -> decoded.put(id, validateOrigin(id, value)));
        });
        return decoded;
    }

    private DataResult<Origin> decodeOrigin(ResourceLocation id, JsonElement element) {
        return ORIGIN_CODEC.parse(JsonOps.INSTANCE, element)
            .map(data -> new Origin(
                id,
                data.name(),
                data.description(),
                new ArrayList<>(data.powers()),
                resolveIcon(id, data.icon()),
                resolveImpact(id, data.impact())
            ));
    }

    private Origin validateOrigin(ResourceLocation id, Origin origin) {
        List<ResourceLocation> powers = origin.powers().stream().filter(powerId -> {
            boolean present = ConfiguredPowers.get(powerId).isPresent();
            if (!present) {
                Origins.LOGGER.warn("Origin {} references unknown power {}", id, powerId);
            }
            return present;
        }).collect(Collectors.toList());
        return new Origin(origin.id(), origin.name(), origin.description(), powers, origin.icon().copy(), origin.impact());
    }

    private ItemStack resolveIcon(ResourceLocation originId, Optional<ResourceLocation> iconId) {
        if (iconId.isEmpty()) {
            return new ItemStack(Items.PLAYER_HEAD);
        }

        ResourceLocation itemId = iconId.get();
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
        if (item.isEmpty()) {
            Origins.LOGGER.warn("Origin {} references unknown icon {}", originId, itemId);
            return new ItemStack(Items.PLAYER_HEAD);
        }

        return new ItemStack(item.get());
    }

    private OriginImpact resolveImpact(ResourceLocation originId, Optional<Integer> impactId) {
        if (impactId.isEmpty()) {
            return OriginImpact.NONE;
        }

        int value = impactId.get();
        OriginImpact impact = OriginImpact.fromId(value);
        if (impact.id() != value) {
            Origins.LOGGER.warn("Origin {} has out of range impact value {}", originId, value);
        }
        return impact;
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

    public static ReloadStats getLastReloadStats() {
        return LAST_STATS;
    }

    private NormalizedPowerJson normalizePowerJson(ResourceLocation powerId, JsonObject original) {
        String rawType = GsonHelper.getAsString(original, "type", "");
        if (rawType.isEmpty()) {
            Origins.LOGGER.warn("Power {} is missing a type entry", powerId);
            return null;
        }

        ResourceLocation parsedType;
        try {
            parsedType = ResourceLocation.parse(rawType);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Power {} has invalid type '{}': {}", powerId, rawType, exception.getMessage());
            return null;
        }

        ResourceLocation canonicalType = POWER_ALIASES.getOrDefault(parsedType, parsedType);
        if (!canonicalType.equals(parsedType)) {
            Origins.LOGGER.info("Remapped power {} type {} -> {}", powerId, parsedType, canonicalType);
        }

        JsonObject normalized = original.deepCopy();
        normalized.addProperty("type", canonicalType.toString());
        normalizeConditionField(powerId, normalized);
        return new NormalizedPowerJson(canonicalType, normalized);
    }

    private void normalizeConditionField(ResourceLocation powerId, JsonObject json) {
        if (json.has("condition")) {
            parseConditionElement(powerId, "condition", json.get("condition"))
                .ifPresent(id -> json.addProperty("condition", id.toString()));
            return;
        }

        for (String alias : List.of("conditions", "predicate")) {
            if (json.has(alias)) {
                parseConditionElement(powerId, alias, json.get(alias))
                    .ifPresent(id -> json.addProperty("condition", id.toString()));
                json.remove(alias);
                break;
            }
        }
    }

    private Optional<ResourceLocation> parseConditionElement(ResourceLocation powerId, String field, JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return Optional.empty();
        }

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String value = element.getAsString();
            try {
                return Optional.of(ResourceLocation.parse(value));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Power {} has invalid {} value '{}': {}", powerId, field, value, exception.getMessage());
                return Optional.empty();
            }
        }

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            List<ResourceLocation> identifiers = new ArrayList<>();
            array.forEach(entry -> {
                if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) {
                    String value = entry.getAsString();
                    try {
                        identifiers.add(ResourceLocation.parse(value));
                    } catch (IllegalArgumentException exception) {
                        Origins.LOGGER.warn("Power {} has invalid {} array value '{}': {}", powerId, field, value, exception.getMessage());
                    }
                } else {
                    Origins.LOGGER.warn("Power {} has unsupported {} array element {}", powerId, field, entry);
                }
            });

            if (identifiers.isEmpty()) {
                Origins.LOGGER.warn("Power {} provided an empty {} array", powerId, field);
                return Optional.empty();
            }

            if (identifiers.size() > 1) {
                Origins.LOGGER.warn("Power {} {} array has multiple entries; using {}", powerId, field, identifiers.get(0));
            }
            return Optional.of(identifiers.get(0));
        }

        Origins.LOGGER.warn("Power {} has unsupported {} format; expected string or array of strings", powerId, field);
        return Optional.empty();
    }

    private record OriginData(
        Component name,
        Component description,
        List<ResourceLocation> powers,
        Optional<ResourceLocation> icon,
        Optional<Integer> impact
    ) {
    }

    public record ReloadStats(
        int originsLoaded,
        int powersLoaded,
        int skippedEntries,
        List<ResourceLocation> unknownTypes
    ) {
        static ReloadStats empty() {
            return new ReloadStats(0, 0, 0, List.of());
        }
    }

    private record NormalizedPowerJson(ResourceLocation type, JsonObject json) {
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
