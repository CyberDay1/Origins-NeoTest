package io.github.apace100.origins.datapack;
import io.github.apace100.origins.util.ResourceLocationCompat;

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
import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.power.action.impl.AddXpAction;
import io.github.apace100.origins.power.action.impl.AddXpLevelAction;
import io.github.apace100.origins.power.action.impl.ApplyEffectAction;
import io.github.apace100.origins.power.action.impl.ClearAllEffectsAction;
import io.github.apace100.origins.power.action.impl.ClearInventoryAction;
import io.github.apace100.origins.power.action.impl.ClearEffectAction;
import io.github.apace100.origins.power.action.impl.ConsumeItemAction;
import io.github.apace100.origins.power.action.impl.CriticalHitAction;
import io.github.apace100.origins.power.action.impl.DamageItemAction;
import io.github.apace100.origins.power.action.impl.GiveItemAction;
import io.github.apace100.origins.power.action.impl.LockRecipeAction;
import io.github.apace100.origins.power.action.impl.ModifyAttributeAction;
import io.github.apace100.origins.power.action.impl.ModifyDamageDealtAction;
import io.github.apace100.origins.power.action.impl.ModifyDamageTakenAction;
import io.github.apace100.origins.power.action.impl.ModifyFallingAction;
import io.github.apace100.origins.power.action.impl.ModifyFoodAction;
import io.github.apace100.origins.power.action.impl.ModifyProjectileAction;
import io.github.apace100.origins.power.action.impl.ModifyProjectileDamageAction;
import io.github.apace100.origins.power.action.impl.ModifyInventoryAction;
import io.github.apace100.origins.power.action.impl.NoFallDamageAction;
import io.github.apace100.origins.power.action.impl.PreventBlockUseAction;
import io.github.apace100.origins.power.action.impl.PreventEntitySpawnAction;
import io.github.apace100.origins.power.action.impl.PreventFoodAction;
import io.github.apace100.origins.power.action.impl.PreventItemDropAction;
import io.github.apace100.origins.power.action.impl.PreventItemPickupAction;
import io.github.apace100.origins.power.action.impl.PreventItemUseAction;
import io.github.apace100.origins.power.action.impl.PreventLootAction;
import io.github.apace100.origins.power.action.impl.PreventProjectileAction;
import io.github.apace100.origins.power.action.impl.PreventSleepAction;
import io.github.apace100.origins.power.action.impl.RedirectProjectileAction;
import io.github.apace100.origins.power.action.impl.ReplaceEquippedItemAction;
import io.github.apace100.origins.power.action.impl.ReplaceLootAction;
import io.github.apace100.origins.power.action.impl.ResetAttributeAction;
import io.github.apace100.origins.power.action.impl.RestrictBlockUseAction;
import io.github.apace100.origins.power.action.impl.RestrictItemUseAction;
import io.github.apace100.origins.power.action.impl.UnlockRecipeAction;
import io.github.apace100.origins.power.action.impl.SetXpLevelAction;
import io.github.apace100.origins.power.action.impl.SwapInventoryAction;
import io.github.apace100.origins.power.action.impl.SpawnEntityAction;
import io.github.apace100.origins.power.action.registry.ActionRegistry;
import io.github.apace100.origins.power.condition.Condition;
import io.github.apace100.origins.power.condition.impl.AllOfCondition;
import io.github.apace100.origins.power.condition.impl.AnyOfCondition;
import io.github.apace100.origins.power.condition.impl.AttributeCondition;
import io.github.apace100.origins.power.condition.impl.AttackerCondition;
import io.github.apace100.origins.power.condition.impl.BiomeCondition;
import io.github.apace100.origins.power.condition.impl.BlockRestrictedCondition;
import io.github.apace100.origins.power.condition.impl.DamageSourceCondition;
import io.github.apace100.origins.power.condition.impl.DimensionCondition;
import io.github.apace100.origins.power.condition.impl.DimensionWhitelistCondition;
import io.github.apace100.origins.power.condition.impl.EffectActiveCondition;
import io.github.apace100.origins.power.condition.impl.EntityCondition;
import io.github.apace100.origins.power.condition.impl.FluidCondition;
import io.github.apace100.origins.power.condition.impl.HealthCondition;
import io.github.apace100.origins.power.condition.impl.InvertedCondition;
import io.github.apace100.origins.power.condition.impl.ItemDurabilityCondition;
import io.github.apace100.origins.power.condition.impl.ItemEnchantmentCondition;
import io.github.apace100.origins.power.condition.impl.ItemRestrictedCondition;
import io.github.apace100.origins.power.condition.impl.ItemTagCondition;
import io.github.apace100.origins.power.condition.impl.LightLevelCondition;
import io.github.apace100.origins.power.condition.impl.NotCondition;
import io.github.apace100.origins.power.condition.impl.OrCondition;
import io.github.apace100.origins.power.condition.impl.OnFireCondition;
import io.github.apace100.origins.power.condition.impl.PassengerCondition;
import io.github.apace100.origins.power.condition.impl.PreventBlockUseCondition;
import io.github.apace100.origins.power.condition.impl.PreventEntitySpawnCondition;
import io.github.apace100.origins.power.condition.impl.ProjectileCondition;
import io.github.apace100.origins.power.condition.impl.RecentDamageCondition;
import io.github.apace100.origins.power.condition.impl.SleepCondition;
import io.github.apace100.origins.power.condition.impl.SneakingCondition;
import io.github.apace100.origins.power.condition.registry.ConditionRegistry;
import io.github.apace100.origins.power.condition.impl.TimeOfDayCondition;
import io.github.apace100.origins.power.condition.impl.XorCondition;
import io.github.apace100.origins.power.condition.impl.WeatherCondition;
import io.github.apace100.origins.power.condition.impl.YLevelCondition;
import io.github.apace100.origins.power.condition.impl.BiomeWhitelistCondition;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
    private static final String ORIGINS_DIRECTORY = "origins/origins";
    private static final String POWERS_DIRECTORY = "origins/powers";
    private static final String ACTIONS_DIRECTORY = "origins/actions";
    private static final String CONDITIONS_DIRECTORY = "origins/conditions";

    private static final Codec<OriginData> ORIGIN_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ComponentSerialization.CODEC.fieldOf("name").forGetter(OriginData::name),
        ComponentSerialization.CODEC.fieldOf("description").forGetter(OriginData::description),
        ResourceLocation.CODEC.listOf().optionalFieldOf("powers", List.of()).forGetter(OriginData::powers),
        ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(OriginData::icon),
        Codec.INT.optionalFieldOf("impact").forGetter(OriginData::impact)
    ).apply(instance, OriginData::new));
    private static final JsonGatherer POWER_GATHERER = new JsonGatherer(POWERS_DIRECTORY);
    private static final JsonGatherer ACTION_GATHERER = new JsonGatherer(ACTIONS_DIRECTORY);
    private static final JsonGatherer CONDITION_GATHERER = new JsonGatherer(CONDITIONS_DIRECTORY);
    private static final Map<ResourceLocation, ResourceLocation> POWER_ALIASES = Map.of(
        ResourceLocationCompat.mod("elytra"),
        ResourceLocationCompat.mod("elytra_flight"),
        ResourceLocationCompat.mod("elytrian_flight"),
        ResourceLocationCompat.mod("elytra_flight")
    );

    private static volatile ReloadStats LAST_STATS = ReloadStats.empty();
    private static volatile ParityReport LAST_PARITY_REPORT = ParityReport.empty();

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
    private final ParityAuditCollector parityAudit = new ParityAuditCollector();

    public OriginsDataLoader() {
        super(GSON, ORIGINS_DIRECTORY);
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
        parityAudit.reset(OriginsConfig.debugAuditEnabled());
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
        int effectActionCount = countEffectActions(actions);
        int attributeActionCount = countAttributeActions(actions);
        int itemActionCount = countItemActions(actions);
        int foodActionCount = countFoodActions(actions);
        int blockActionCount = countBlockActions(actions);
        int projectileActionCount = countProjectileActions(actions);
        int experienceActionCount = countExperienceActions(actions);
        int damageActionCount = countDamageActions(actions);
        int fallActionCount = countFallingActions(actions);
        int lootActionCount = countLootActions(actions);
        int recipeActionCount = countRecipeActions(actions);
        int sleepActionCount = countSleepActions(actions);
        int restrictionActionCount = countRestrictionActions(actions);
        int inventoryActionCount = countInventoryActions(actions);
        int spawnActionCount = countSpawnActions(actions);

        Map<ResourceLocation, Condition<?>> conditions = decodeConditions(pendingConditionJson);
        ConditionRegistry.setAll(conditions);
        int effectConditionCount = countEffectConditions(conditions);
        int attributeConditionCount = countAttributeConditions(conditions);
        int itemConditionCount = countItemConditions(conditions);
        int blockConditionCount = countBlockConditions(conditions);
        int projectileConditionCount = countProjectileConditions(conditions);
        int combatConditionCount = countCombatConditions(conditions);
        int environmentConditionCount = countEnvironmentConditions(conditions);
        int sleepConditionCount = countSleepConditions(conditions);
        int restrictionConditionCount = countRestrictionConditions(conditions);
        int spawnConditionCount = countSpawnConditions(conditions);
        int dimensionConditionCount = countDimensionConditions(conditions);
        int biomeConditionCount = countBiomeConditions(conditions);
        int dimensionWhitelistConditionCount = countDimensionWhitelistConditions(conditions);
        int biomeWhitelistConditionCount = countBiomeWhitelistConditions(conditions);

        Map<ResourceLocation, ConfiguredPower> powers = decodePowers(pendingPowerJson);
        ConfiguredPowers.setAll(powers);

        Map<ResourceLocation, Origin> origins = decodeOrigins(originJson);
        OriginRegistry.setAll(origins);

        List<ResourceLocation> unknownTypes = Stream.of(unknownPowerTypes, unknownActionTypes, unknownConditionTypes)
            .flatMap(Set::stream)
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .toList();
        int entityConditionCount = countEntityConditions(conditions);
        int compositeConditionCount = countCompositeConditions(conditions);
        int totalSkipped = skippedOrigins + skippedPowers + skippedActions + skippedConditions;
        LAST_STATS = new ReloadStats(origins.size(), powers.size(), actions.size(), conditions.size(), effectActionCount,
            attributeActionCount, itemActionCount, foodActionCount, blockActionCount, projectileActionCount,
            experienceActionCount, damageActionCount, fallActionCount, lootActionCount, recipeActionCount, sleepActionCount,
            restrictionActionCount, inventoryActionCount, spawnActionCount,
            effectConditionCount, attributeConditionCount, entityConditionCount, compositeConditionCount, itemConditionCount,
            blockConditionCount, projectileConditionCount, combatConditionCount, environmentConditionCount, sleepConditionCount,
            restrictionConditionCount, spawnConditionCount, dimensionConditionCount, biomeConditionCount,
            dimensionWhitelistConditionCount, biomeWhitelistConditionCount, totalSkipped,
            unknownTypes);

        LAST_PARITY_REPORT = parityAudit.buildReport(
            ActionRegistry.implementedTypes(),
            ConditionRegistry.implementedTypes(),
            implementedPowerTypes()
        );

        if (parityAudit.enabled()) {
            parityAudit.logSummary(LAST_PARITY_REPORT);
        }

        if (totalSkipped > 0) {
            Origins.LOGGER.info("Loaded {} origins, {} powers, {} actions ({} effect / {} attribute / {} item / {} food / {} block / {} projectile / {} experience / {} damage / {} fall / {} loot / {} recipe / {} sleep / {} restriction / {} inventory / {} spawn), and {} conditions ({} effect / {} attribute / {} entity / {} composite / {} item / {} block / {} projectile / {} combat / {} environment / {} sleep / {} restriction / {} spawn / {} dimension / {} biome / {} dimension whitelist / {} biome whitelist) from datapacks ({} entries skipped)",
                origins.size(), powers.size(), actions.size(), effectActionCount, attributeActionCount, itemActionCount,
                foodActionCount, blockActionCount, projectileActionCount, experienceActionCount, damageActionCount, fallActionCount,
                lootActionCount, recipeActionCount, sleepActionCount, restrictionActionCount, inventoryActionCount,
                spawnActionCount,
                conditions.size(), effectConditionCount, attributeConditionCount, entityConditionCount, compositeConditionCount,
                itemConditionCount, blockConditionCount, projectileConditionCount, combatConditionCount, environmentConditionCount,
                sleepConditionCount, restrictionConditionCount, spawnConditionCount, dimensionConditionCount, biomeConditionCount,
                dimensionWhitelistConditionCount, biomeWhitelistConditionCount, totalSkipped);
        } else {
            Origins.LOGGER.info("Loaded {} origins, {} powers, {} actions ({} effect / {} attribute / {} item / {} food / {} block / {} projectile / {} experience / {} damage / {} fall / {} loot / {} recipe / {} sleep / {} restriction / {} inventory / {} spawn), and {} conditions ({} effect / {} attribute / {} entity / {} composite / {} item / {} block / {} projectile / {} combat / {} environment / {} sleep / {} restriction / {} spawn / {} dimension / {} biome / {} dimension whitelist / {} biome whitelist) from datapacks",
                origins.size(), powers.size(), actions.size(), effectActionCount, attributeActionCount, itemActionCount,
                foodActionCount, blockActionCount, projectileActionCount, experienceActionCount, damageActionCount, fallActionCount,
                lootActionCount, recipeActionCount, sleepActionCount, restrictionActionCount, inventoryActionCount, spawnActionCount,
                conditions.size(), effectConditionCount, attributeConditionCount, entityConditionCount, compositeConditionCount,
                itemConditionCount, blockConditionCount, projectileConditionCount, combatConditionCount, environmentConditionCount,
                sleepConditionCount, restrictionConditionCount, spawnConditionCount, dimensionConditionCount, biomeConditionCount,
                dimensionWhitelistConditionCount, biomeWhitelistConditionCount);
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

        if (!ActionRegistry.isImplemented(typeId)) {
            unknownActionTypes.add(typeId);
            parityAudit.recordUnknownActionType(typeId, id, ACTIONS_DIRECTORY);
            if (parityAudit.enabled()) {
                Origins.LOGGER.warn("[Origins][Parity] Unknown action type '{}' encountered in {}", typeId,
                    datapackPath(ACTIONS_DIRECTORY, id));
            } else {
                Origins.LOGGER.warn("Unknown action type '{}' for data file '{}'", typeId, id);
            }
            skippedActions++;
            return Optional.empty();
        }

        Optional<Action<?>> action = ActionRegistry.create(typeId, id, json);
        if (action.isEmpty()) {
            Origins.LOGGER.warn("Failed to instantiate action '{}' of type '{}' from {}", id, typeId,
                datapackPath(ACTIONS_DIRECTORY, id));
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

        if (!ConditionRegistry.isImplemented(typeId)) {
            unknownConditionTypes.add(typeId);
            parityAudit.recordUnknownConditionType(typeId, id, CONDITIONS_DIRECTORY);
            if (parityAudit.enabled()) {
                Origins.LOGGER.warn("[Origins][Parity] Unknown condition type '{}' encountered in {}", typeId,
                    datapackPath(CONDITIONS_DIRECTORY, id));
            } else {
                Origins.LOGGER.warn("Unknown condition type '{}' for data file '{}'", typeId, id);
            }
            skippedConditions++;
            return Optional.empty();
        }

        Optional<Condition<?>> condition = ConditionRegistry.create(typeId, id, json);
        if (condition.isEmpty()) {
            Origins.LOGGER.warn("Failed to instantiate condition '{}' of type '{}' from {}", id, typeId,
                datapackPath(CONDITIONS_DIRECTORY, id));
            skippedConditions++;
            return Optional.empty();
        }

        return condition;
    }

    private static int countEffectActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ApplyEffectAction
                || action instanceof ClearEffectAction
                || action instanceof ClearAllEffectsAction)
            .count();
    }

    private static int countAttributeActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ModifyAttributeAction
                || action instanceof ResetAttributeAction)
            .count();
    }

    private static int countItemActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof GiveItemAction
                || action instanceof ConsumeItemAction
                || action instanceof ReplaceEquippedItemAction
                || action instanceof DamageItemAction
                || action instanceof PreventItemPickupAction
                || action instanceof PreventItemDropAction
                || action instanceof RestrictItemUseAction)
            .count();
    }

    private static int countFoodActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ModifyFoodAction || action instanceof PreventFoodAction)
            .count();
    }

    private static int countBlockActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof PreventBlockUseAction
                || action instanceof RestrictBlockUseAction)
            .count();
    }

    private static int countProjectileActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ModifyProjectileAction
                || action instanceof ModifyProjectileDamageAction
                || action instanceof RedirectProjectileAction
                || action instanceof PreventProjectileAction)
            .count();
    }

    private static int countExperienceActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof AddXpAction
                || action instanceof SetXpLevelAction
                || action instanceof AddXpLevelAction)
            .count();
    }

    private static int countDamageActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ModifyDamageTakenAction
                || action instanceof ModifyDamageDealtAction
                || action instanceof CriticalHitAction)
            .count();
    }

    private static int countFallingActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ModifyFallingAction
                || action instanceof NoFallDamageAction)
            .count();
    }

    private static int countLootActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ReplaceLootAction
                || action instanceof PreventLootAction)
            .count();
    }

    private static int countRecipeActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof UnlockRecipeAction
                || action instanceof LockRecipeAction)
            .count();
    }

    private static int countSleepActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof PreventSleepAction)
            .count();
    }

    private static int countRestrictionActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof PreventFoodAction
                || action instanceof PreventItemUseAction
                || action instanceof PreventBlockUseAction
                || action instanceof PreventProjectileAction
                || action instanceof PreventItemPickupAction
                || action instanceof PreventItemDropAction
                || action instanceof RestrictItemUseAction
                || action instanceof RestrictBlockUseAction
                || action instanceof PreventSleepAction
                || action instanceof PreventLootAction)
            .count();
    }

    private static int countInventoryActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof ModifyInventoryAction
                || action instanceof ClearInventoryAction
                || action instanceof SwapInventoryAction)
            .count();
    }

    private static int countSpawnActions(Map<ResourceLocation, Action<?>> actions) {
        return (int) actions.values().stream()
            .filter(action -> action instanceof PreventEntitySpawnAction
                || action instanceof SpawnEntityAction)
            .count();
    }

    private static int countEffectConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof EffectActiveCondition)
            .count();
    }

    private static int countAttributeConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof AttributeCondition)
            .count();
    }

    private static int countEntityConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream().filter(OriginsDataLoader::isEntityCondition).count();
    }

    private static int countCompositeConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream().filter(OriginsDataLoader::isCompositeCondition).count();
    }

    private static int countItemConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof ItemEnchantmentCondition
                || condition instanceof ItemDurabilityCondition
                || condition instanceof ItemTagCondition
                || condition instanceof ItemRestrictedCondition)
            .count();
    }

    private static int countBlockConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof PreventBlockUseCondition
                || condition instanceof BlockRestrictedCondition)
            .count();
    }

    private static int countProjectileConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof ProjectileCondition)
            .count();
    }

    private static int countCombatConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof DamageSourceCondition
                || condition instanceof AttackerCondition
                || condition instanceof RecentDamageCondition)
            .count();
    }

    private static int countEnvironmentConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof TimeOfDayCondition
                || condition instanceof LightLevelCondition
                || condition instanceof WeatherCondition
                || condition instanceof BiomeCondition
                || condition instanceof BiomeWhitelistCondition
                || condition instanceof DimensionCondition
                || condition instanceof DimensionWhitelistCondition
                || condition instanceof FluidCondition
                || condition instanceof YLevelCondition)
            .count();
    }

    private static int countSleepConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof SleepCondition)
            .count();
    }

    private static int countRestrictionConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof PreventBlockUseCondition
                || condition instanceof ItemRestrictedCondition
                || condition instanceof BlockRestrictedCondition
                || condition instanceof SleepCondition)
            .count();
    }

    private static int countSpawnConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof PreventEntitySpawnCondition)
            .count();
    }

    private static int countDimensionConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof DimensionCondition)
            .count();
    }

    private static int countBiomeConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof BiomeCondition)
            .count();
    }

    private static int countDimensionWhitelistConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof DimensionWhitelistCondition)
            .count();
    }

    private static int countBiomeWhitelistConditions(Map<ResourceLocation, Condition<?>> conditions) {
        return (int) conditions.values().stream()
            .filter(condition -> condition instanceof BiomeWhitelistCondition)
            .count();
    }

    private static boolean isEntityCondition(Condition<?> condition) {
        return condition instanceof EntityCondition
            || condition instanceof HealthCondition
            || condition instanceof OnFireCondition
            || condition instanceof SneakingCondition
            || condition instanceof PassengerCondition
            || condition instanceof RecentDamageCondition
            || condition instanceof ProjectileCondition
            || condition instanceof YLevelCondition;
    }

    private static boolean isCompositeCondition(Condition<?> condition) {
        return condition instanceof AllOfCondition
            || condition instanceof AnyOfCondition
            || condition instanceof InvertedCondition
            || condition instanceof OrCondition
            || condition instanceof NotCondition
            || condition instanceof XorCondition;
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
            parityAudit.recordUnknownPowerType(normalized.type(), id, POWERS_DIRECTORY);
            if (parityAudit.enabled()) {
                Origins.LOGGER.warn("[Origins][Parity] Unknown power type '{}' encountered in {}", normalized.type(),
                    datapackPath(POWERS_DIRECTORY, id));
            } else {
                Origins.LOGGER.warn("Unknown power type '{}' for data file '{}'", normalized.type(), id);
            }
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
                parityAudit.recordMissingActionReference(actionId, id);
                if (parityAudit.enabled()) {
                    Origins.LOGGER.warn("[Origins][Parity] Power {} references missing action {} (source: {})", id,
                        actionId, datapackPath(POWERS_DIRECTORY, id));
                } else {
                    Origins.LOGGER.warn("Power {} references unknown action {}", id, actionId);
                }
            }
            return present;
        }).toList();

        if (ConfiguredConditions.get(power.condition()).isEmpty()) {
            parityAudit.recordMissingConditionReference(power.condition(), id);
            if (parityAudit.enabled()) {
                Origins.LOGGER.warn("[Origins][Parity] Power {} references missing condition {} (source: {})", id,
                    power.condition(), datapackPath(POWERS_DIRECTORY, id));
            } else {
                Origins.LOGGER.warn("Power {} references unknown condition {}", id, power.condition());
            }
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
                parityAudit.recordMissingPowerReference(powerId, id);
                if (parityAudit.enabled()) {
                    Origins.LOGGER.warn("[Origins][Parity] Origin {} references missing power {} (source: {})", id,
                        powerId, datapackPath(ORIGINS_DIRECTORY, id));
                } else {
                    Origins.LOGGER.warn("Origin {} references unknown power {}", id, powerId);
                }
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

    public static ParityReport getLastParityReport() {
        return LAST_PARITY_REPORT;
    }

    private Set<ResourceLocation> implementedPowerTypes() {
        return ModPowers.POWERS.getEntries().stream()
            .map(DeferredHolder::getId)
            .collect(Collectors.toCollection(HashSet::new));
    }

    private static String datapackPath(String directory, ResourceLocation id) {
        return "data/%s/%s/%s.json".formatted(id.getNamespace(), directory, id.getPath());
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
        int effectActionsLoaded,
        int attributeActionsLoaded,
        int itemActionsLoaded,
        int foodActionsLoaded,
        int blockActionsLoaded,
        int projectileActionsLoaded,
        int experienceActionsLoaded,
        int damageActionsLoaded,
        int fallActionsLoaded,
        int lootActionsLoaded,
        int recipeActionsLoaded,
        int sleepActionsLoaded,
        int restrictionActionsLoaded,
        int inventoryActionsLoaded,
        int spawnActionsLoaded,
        int effectConditionsLoaded,
        int attributeConditionsLoaded,
        int entityConditionsLoaded,
        int compositeConditionsLoaded,
        int itemConditionsLoaded,
        int blockConditionsLoaded,
        int projectileConditionsLoaded,
        int combatConditionsLoaded,
        int environmentConditionsLoaded,
        int sleepConditionsLoaded,
        int restrictionConditionsLoaded,
        int spawnConditionsLoaded,
        int dimensionConditionsLoaded,
        int biomeConditionsLoaded,
        int dimensionWhitelistConditionsLoaded,
        int biomeWhitelistConditionsLoaded,
        int skippedEntries,
        List<ResourceLocation> unknownTypes
    ) {
        static ReloadStats empty() {
            return new ReloadStats(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                List.<ResourceLocation>of()
            );
        }
    }

    public record ParityReport(
        List<ResourceLocation> implementedActionTypes,
        List<ResourceLocation> implementedConditionTypes,
        List<ResourceLocation> implementedPowerTypes,
        List<MissingEntry> missingActions,
        List<MissingEntry> missingConditions,
        List<MissingEntry> missingPowers
    ) {
        public static ParityReport empty() {
            return new ParityReport(List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        }

        public boolean hasFindings() {
            return !missingActions.isEmpty() || !missingConditions.isEmpty() || !missingPowers.isEmpty();
        }

        public int missingActionOccurrences() {
            return countOccurrences(missingActions);
        }

        public int missingConditionOccurrences() {
            return countOccurrences(missingConditions);
        }

        public int missingPowerOccurrences() {
            return countOccurrences(missingPowers);
        }

        public JsonObject toJson() {
            JsonObject root = new JsonObject();

            JsonObject implemented = new JsonObject();
            implemented.add("actions", toIdArray(implementedActionTypes));
            implemented.add("conditions", toIdArray(implementedConditionTypes));
            implemented.add("powers", toIdArray(implementedPowerTypes));
            root.add("implemented", implemented);

            JsonObject missing = new JsonObject();
            missing.add("actions", toMissingEntriesJson(missingActions));
            missing.add("conditions", toMissingEntriesJson(missingConditions));
            missing.add("powers", toMissingEntriesJson(missingPowers));
            root.add("missing", missing);

            JsonObject summary = new JsonObject();
            summary.addProperty("implementedActionTypes", implementedActionTypes.size());
            summary.addProperty("implementedConditionTypes", implementedConditionTypes.size());
            summary.addProperty("implementedPowerTypes", implementedPowerTypes.size());
            summary.addProperty("missingActionIds", missingActions.size());
            summary.addProperty("missingConditionIds", missingConditions.size());
            summary.addProperty("missingPowerIds", missingPowers.size());
            summary.addProperty("missingActionOccurrences", missingActionOccurrences());
            summary.addProperty("missingConditionOccurrences", missingConditionOccurrences());
            summary.addProperty("missingPowerOccurrences", missingPowerOccurrences());
            summary.addProperty("hasFindings", hasFindings());
            root.add("summary", summary);

            return root;
        }

        private static JsonArray toIdArray(List<ResourceLocation> ids) {
            JsonArray array = new JsonArray();
            ids.stream()
                .map(ResourceLocation::toString)
                .forEach(array::add);
            return array;
        }

        private static JsonObject toMissingEntriesJson(List<MissingEntry> entries) {
            JsonObject object = new JsonObject();
            object.addProperty("count", entries.size());
            object.addProperty("occurrences", countOccurrences(entries));
            JsonArray entryArray = new JsonArray();
            for (MissingEntry entry : entries) {
                JsonObject entryJson = new JsonObject();
                entryJson.addProperty("id", entry.id().toString());
                JsonArray occurrences = new JsonArray();
                for (MissingOccurrence occurrence : entry.occurrences()) {
                    JsonObject occurrenceJson = new JsonObject();
                    occurrenceJson.addProperty("context", occurrence.context());
                    occurrenceJson.addProperty("source", occurrence.sourceId().toString());
                    if (!occurrence.sourcePath().isEmpty()) {
                        occurrenceJson.addProperty("path", occurrence.sourcePath());
                    }
                    if (!occurrence.note().isEmpty()) {
                        occurrenceJson.addProperty("detail", occurrence.note());
                    }
                    occurrences.add(occurrenceJson);
                }
                entryJson.add("occurrences", occurrences);
                entryArray.add(entryJson);
            }
            object.add("entries", entryArray);
            return object;
        }

        private static int countOccurrences(List<MissingEntry> entries) {
            return entries.stream()
                .mapToInt(entry -> entry.occurrences().size())
                .sum();
        }
    }

    public record MissingEntry(ResourceLocation id, List<MissingOccurrence> occurrences) {
    }

    public record MissingOccurrence(String context, ResourceLocation sourceId, String sourcePath, String note) {
        static MissingOccurrence definition(ResourceLocation sourceId, String sourcePath, String note) {
            return new MissingOccurrence("definition", sourceId, sourcePath, note);
        }

        static MissingOccurrence reference(ResourceLocation sourceId, String sourcePath, String note) {
            return new MissingOccurrence("reference", sourceId, sourcePath, note);
        }
    }

    private static final class ParityAuditCollector {
        private final Map<ResourceLocation, List<MissingOccurrence>> missingActions = new LinkedHashMap<>();
        private final Map<ResourceLocation, List<MissingOccurrence>> missingConditions = new LinkedHashMap<>();
        private final Map<ResourceLocation, List<MissingOccurrence>> missingPowers = new LinkedHashMap<>();
        private boolean enabled;

        void reset(boolean enabled) {
            this.enabled = enabled;
            missingActions.clear();
            missingConditions.clear();
            missingPowers.clear();
        }

        boolean enabled() {
            return enabled;
        }

        void recordUnknownActionType(ResourceLocation typeId, ResourceLocation fileId, String directory) {
            record(missingActions, typeId, MissingOccurrence.definition(
                fileId,
                datapackPath(directory, fileId),
                "Action definition skipped because the type is not implemented."
            ));
        }

        void recordUnknownConditionType(ResourceLocation typeId, ResourceLocation fileId, String directory) {
            record(missingConditions, typeId, MissingOccurrence.definition(
                fileId,
                datapackPath(directory, fileId),
                "Condition definition skipped because the type is not implemented."
            ));
        }

        void recordUnknownPowerType(ResourceLocation typeId, ResourceLocation fileId, String directory) {
            record(missingPowers, typeId, MissingOccurrence.definition(
                fileId,
                datapackPath(directory, fileId),
                "Power definition skipped because the type is not implemented."
            ));
        }

        void recordMissingActionReference(ResourceLocation actionId, ResourceLocation powerId) {
            record(missingActions, actionId, MissingOccurrence.reference(
                powerId,
                datapackPath(POWERS_DIRECTORY, powerId),
                "Referenced from power action list."
            ));
        }

        void recordMissingConditionReference(ResourceLocation conditionId, ResourceLocation powerId) {
            record(missingConditions, conditionId, MissingOccurrence.reference(
                powerId,
                datapackPath(POWERS_DIRECTORY, powerId),
                "Referenced from power condition field."
            ));
        }

        void recordMissingPowerReference(ResourceLocation powerId, ResourceLocation originId) {
            record(missingPowers, powerId, MissingOccurrence.reference(
                originId,
                datapackPath(ORIGINS_DIRECTORY, originId),
                "Referenced from origin power list."
            ));
        }

        ParityReport buildReport(Collection<ResourceLocation> implementedActionTypes,
                                 Collection<ResourceLocation> implementedConditionTypes,
                                 Collection<ResourceLocation> implementedPowerTypes) {
            return new ParityReport(
                sortIds(implementedActionTypes),
                sortIds(implementedConditionTypes),
                sortIds(implementedPowerTypes),
                buildEntries(missingActions),
                buildEntries(missingConditions),
                buildEntries(missingPowers)
            );
        }

        void logSummary(ParityReport report) {
            Origins.LOGGER.info(
                "[Origins][Parity] Action types implemented: {} ({} missing across {} occurrences); condition types implemented: {} ({} missing across {} occurrences); power types implemented: {} ({} missing across {} occurrences)",
                report.implementedActionTypes().size(), report.missingActions().size(), report.missingActionOccurrences(),
                report.implementedConditionTypes().size(), report.missingConditions().size(), report.missingConditionOccurrences(),
                report.implementedPowerTypes().size(), report.missingPowers().size(), report.missingPowerOccurrences()
            );
        }

        private void record(Map<ResourceLocation, List<MissingOccurrence>> map, ResourceLocation id,
                            MissingOccurrence occurrence) {
            map.computeIfAbsent(id, key -> new ArrayList<>()).add(occurrence);
        }

        private List<ResourceLocation> sortIds(Collection<ResourceLocation> ids) {
            return ids.stream()
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();
        }

        private List<MissingEntry> buildEntries(Map<ResourceLocation, List<MissingOccurrence>> map) {
            return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(ResourceLocation::toString)))
                .map(entry -> new MissingEntry(entry.getKey(), List.copyOf(entry.getValue())))
                .toList();
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
