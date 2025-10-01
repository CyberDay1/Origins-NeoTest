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
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.power.action.registry.ActionRegistry;
import io.github.apace100.origins.power.condition.Condition;
import io.github.apace100.origins.power.condition.registry.ConditionRegistry;
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
import java.util.stream.Stream;

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
    private static final JsonGatherer ACTION_GATHERER = new JsonGatherer("origins/actions");
    private static final JsonGatherer CONDITION_GATHERER = new JsonGatherer("origins/conditions");
    private static final Map<ResourceLocation, ResourceLocation> POWER_ALIASES = Map.of(
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytra"),
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytra_flight"),
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytrian_flight"),
        ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "elytra_flight")
    );

    private static volatile ReloadStats LAST_STATS = ReloadStats.empty();

    private Map<ResourceLocation, JsonElement> pendingActionJson = Map.of();
    private Map<ResourceLocation, JsonElement> pendingConditionJson = Map.of();
    private Map<ResourceLocation, JsonElement> pendingPowerJson = Map.of();
    private int skippedActions;
    private int skippedConditions;
    private int skippedPowers;
    private int skippedOrigins;
    private final Set<ResourceLocation> unknownActionTypes = new HashSet<>();
    private final Set<ResourceLocation> unknownConditionTypes = new HashSet<>();
    private final Set<ResourceLocation> unknownPowerTypes = new HashSet<>();

    public OriginsDataLoader() {
        super(GSON, "origins/origins");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        skippedActions = 0;
        skippedConditions = 0;
        skippedPowers = 0;
        skippedOrigins = 0;
        unknownActionTypes.clear();
        unknownConditionTypes.clear();
        unknownPowerTypes.clear();
        pendingActionJson = ACTION_GATHERER.gather(resourceManager, profiler);
        pendingConditionJson = CONDITION_GATHERER.gather(resourceManager, profiler);
        pendingPowerJson = POWER_GATHERER.gather(resourceManager, profiler);
        return super.prepare(resourceManager, profiler);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> originJson, ResourceManager resourceManager, ProfilerFiller profiler) {
        ActionRegistry.bootstrap();
        ConditionRegistry.bootstrap();

        Map<ResourceLocation, Action<?>> actions = decodeActions(pendingActionJson);
        ActionRegistry.setAll(actions);

        Map<ResourceLocation, Condition<?>> conditions = decodeConditions(pendingConditionJson);
        ConditionRegistry.setAll(conditions);

        Map<ResourceLocation, ConfiguredPower> powers = decodePowers(pendingPowerJson);
        ConfiguredPowers.setAll(powers);

        Map<ResourceLocation, Origin> origins = decodeOrigins(originJson);
        OriginRegistry.setAll(origins);

        List<ResourceLocation> unknownTypes = Stream.of(unknownPowerTypes, unknownActionTypes, unknownConditionTypes)
            .flatMap(Set::stream)
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .toList();
        int totalSkipped = skippedOrigins + skippedPowers + skippedActions + skippedConditions;
        LAST_STATS = new ReloadStats(origins.size(), powers.size(), actions.size(), conditions.size(), totalSkipped, unknownTypes);

        if (totalSkipped > 0) {
            Origins.LOGGER.info("Loaded {} origins, {} powers, {} actions, and {} conditions from datapacks ({} entries skipped)",
                origins.size(), powers.size(), actions.size(), conditions.size(), totalSkipped);
        } else {
            Origins.LOGGER.info("Loaded {} origins, {} powers, {} actions, and {} conditions from datapacks",
                origins.size(), powers.size(), actions.size(), conditions.size());
        }
    }

    private Map<ResourceLocation, Action<?>> decodeActions(Map<ResourceLocation, JsonElement> actionJson) {
        Map<ResourceLocation, Action<?>> decoded = new HashMap<>();
        actionJson.forEach((id, element) -> decodeAction(id, element).ifPresent(action -> decoded.put(id, action)));
        return decoded;
    }

    private Optional<Action<?>> decodeAction(ResourceLocation id, JsonElement element) {
        JsonObject json = GsonHelper.convertToJsonObject(element, "value");
        ResourceLocation typeId = parseType(id, json, "action");
        if (typeId == null) {
            skippedActions++;
            return Optional.empty();
        }

        Optional<Action<?>> action = ActionRegistry.create(typeId, id, json);
        if (action.isEmpty()) {
            Origins.LOGGER.warn("Unknown action type '{}' for data file '{}'", typeId, id);
            unknownActionTypes.add(typeId);
            skippedActions++;
            return Optional.empty();
        }

        return action;
    }

    private Map<ResourceLocation, Condition<?>> decodeConditions(Map<ResourceLocation, JsonElement> conditionJson) {
        Map<ResourceLocation, Condition<?>> decoded = new HashMap<>();
        conditionJson.forEach((id, element) -> decodeCondition(id, element).ifPresent(condition -> decoded.put(id, condition)));
        return decoded;
    }

    private Optional<Condition<?>> decodeCondition(ResourceLocation id, JsonElement element) {
        JsonObject json = GsonHelper.convertToJsonObject(element, "value");
        ResourceLocation typeId = parseType(id, json, "condition");
        if (typeId == null) {
            skippedConditions++;
            return Optional.empty();
        }

        Optional<Condition<?>> condition = ConditionRegistry.create(typeId, id, json);
        if (condition.isEmpty()) {
            Origins.LOGGER.warn("Unknown condition type '{}' for data file '{}'", typeId, id);
            unknownConditionTypes.add(typeId);
            skippedConditions++;
            return Optional.empty();
        }

        return condition;
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

    private ResourceLocation parseType(ResourceLocation entryId, JsonObject json, String entryKind) {
        String rawType = GsonHelper.getAsString(json, "type", "");
        if (rawType.isEmpty()) {
            Origins.LOGGER.warn("{} {} is missing a type entry", capitalize(entryKind), entryId);
            return null;
        }

        try {
            return ResourceLocation.parse(rawType);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("{} {} has invalid type '{}': {}", capitalize(entryKind), entryId, rawType, exception.getMessage());
            return null;
        }
    }

    private static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
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
        int actionsLoaded,
        int conditionsLoaded,
        int skippedEntries,
        List<ResourceLocation> unknownTypes
    ) {
        static ReloadStats empty() {
            return new ReloadStats(0, 0, 0, 0, 0, List.of());
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
