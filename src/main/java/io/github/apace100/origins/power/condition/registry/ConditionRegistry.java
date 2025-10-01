package io.github.apace100.origins.power.condition.registry;

import com.google.gson.JsonObject;
import io.github.apace100.origins.power.condition.Condition;
import io.github.apace100.origins.power.condition.impl.BiomeCondition;
import io.github.apace100.origins.power.condition.impl.AllOfCondition;
import io.github.apace100.origins.power.condition.impl.AnyOfCondition;
import io.github.apace100.origins.power.condition.impl.BlockStateCondition;
import io.github.apace100.origins.power.condition.impl.DamageSourceCondition;
import io.github.apace100.origins.power.condition.impl.DimensionCondition;
import io.github.apace100.origins.power.condition.impl.EquippedItemCondition;
import io.github.apace100.origins.power.condition.impl.FluidCondition;
import io.github.apace100.origins.power.condition.impl.FoodCondition;
import io.github.apace100.origins.power.condition.impl.HealthCondition;
import io.github.apace100.origins.power.condition.impl.InvertedCondition;
import io.github.apace100.origins.power.condition.impl.OnFireCondition;
import io.github.apace100.origins.power.condition.impl.PassengerCondition;
import io.github.apace100.origins.power.condition.impl.SneakingCondition;
import io.github.apace100.origins.power.condition.impl.TimeOfDayCondition;
import io.github.apace100.origins.power.condition.impl.EntityCondition;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Registry that bridges datapack condition identifiers to their scaffold implementations.
 */
public final class ConditionRegistry {
    private static final Map<ResourceLocation, BiFunction<ResourceLocation, JsonObject, ? extends Condition<?>>> FACTORIES = new HashMap<>();
    private static final Map<ResourceLocation, Condition<?>> CONDITIONS = new HashMap<>();
    private static boolean BOOTSTRAPPED;

    private ConditionRegistry() {
    }

    /**
     * Ensures the default Fabric parity condition stubs are registered.
     */
    public static void bootstrap() {
        if (BOOTSTRAPPED) {
            return;
        }
        BOOTSTRAPPED = true;
        register(BiomeCondition.TYPE, BiomeCondition::fromJson);
        register(DimensionCondition.TYPE, DimensionCondition::fromJson);
        register(TimeOfDayCondition.TYPE, TimeOfDayCondition::fromJson);
        register(DamageSourceCondition.TYPE, DamageSourceCondition::fromJson);
        register(BlockStateCondition.TYPE, BlockStateCondition::fromJson);
        register(EquippedItemCondition.TYPE, EquippedItemCondition::fromJson);
        register(FluidCondition.TYPE, FluidCondition::fromJson);
        register(FoodCondition.TYPE, FoodCondition::fromJson);
        register(EntityCondition.TYPE, EntityCondition::fromJson);
        register(HealthCondition.TYPE, HealthCondition::fromJson);
        register(OnFireCondition.TYPE, OnFireCondition::fromJson);
        register(SneakingCondition.TYPE, SneakingCondition::fromJson);
        register(PassengerCondition.TYPE, PassengerCondition::fromJson);
        register(AllOfCondition.TYPE, AllOfCondition::fromJson);
        register(AnyOfCondition.TYPE, AnyOfCondition::fromJson);
        register(InvertedCondition.TYPE, InvertedCondition::fromJson);
    }

    /**
     * Registers a new datapack condition factory under the supplied identifier.
     */
    public static void register(ResourceLocation id, BiFunction<ResourceLocation, JsonObject, ? extends Condition<?>> factory) {
        FACTORIES.put(id, factory);
    }

    /**
     * Resolves a datapack condition instance for the provided identifier and JSON payload.
     */
    public static Optional<Condition<?>> create(ResourceLocation typeId, ResourceLocation id, JsonObject json) {
        bootstrap();
        BiFunction<ResourceLocation, JsonObject, ? extends Condition<?>> factory = FACTORIES.get(typeId);
        if (factory == null) {
            return Optional.empty();
        }
        Condition<?> condition = factory.apply(id, json);
        return Optional.ofNullable(condition);
    }

    /**
     * Replaces the loaded datapack conditions with the supplied map.
     */
    public static void setAll(Map<ResourceLocation, Condition<?>> values) {
        CONDITIONS.clear();
        CONDITIONS.putAll(values);
    }

    /**
     * Looks up a hydrated datapack condition by identifier.
     */
    public static Optional<Condition<?>> lookup(ResourceLocation id) {
        return Optional.ofNullable(CONDITIONS.get(id));
    }

    /**
     * Exposes the currently loaded datapack conditions.
     */
    public static Map<ResourceLocation, Condition<?>> entries() {
        return Collections.unmodifiableMap(CONDITIONS);
    }
}
