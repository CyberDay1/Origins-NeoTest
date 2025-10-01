package io.github.apace100.origins.power.action.registry;

import com.google.gson.JsonObject;
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.power.action.impl.BiEntityAction;
import io.github.apace100.origins.power.action.impl.BlockAction;
import io.github.apace100.origins.power.action.impl.DamageEntityAction;
import io.github.apace100.origins.power.action.impl.EntityAction;
import io.github.apace100.origins.power.action.impl.ExecuteCommandAction;
import io.github.apace100.origins.power.action.impl.GiveItemAction;
import io.github.apace100.origins.power.action.impl.GrantAdvancementAction;
import io.github.apace100.origins.power.action.impl.ItemAction;
import io.github.apace100.origins.power.action.impl.PlaySoundAction;
import io.github.apace100.origins.power.action.impl.SetBlockAction;
import io.github.apace100.origins.power.action.impl.SpawnEntityAction;
import io.github.apace100.origins.power.action.impl.WorldAction;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Registry that bridges datapack action type identifiers to their scaffold implementations.
 */
public final class ActionRegistry {
    private static final Map<ResourceLocation, BiFunction<ResourceLocation, JsonObject, ? extends Action<?>>> FACTORIES = new HashMap<>();
    private static final Map<ResourceLocation, Action<?>> ACTIONS = new HashMap<>();
    private static boolean BOOTSTRAPPED;

    private ActionRegistry() {
    }

    /**
     * Ensures the default Fabric parity action stubs are registered.
     */
    public static void bootstrap() {
        if (BOOTSTRAPPED) {
            return;
        }
        BOOTSTRAPPED = true;
        register(ItemAction.TYPE, ItemAction::fromJson);
        register(BlockAction.TYPE, BlockAction::fromJson);
        register(EntityAction.TYPE, EntityAction::fromJson);
        register(BiEntityAction.TYPE, BiEntityAction::fromJson);
        register(WorldAction.TYPE, WorldAction::fromJson);
        register(GiveItemAction.TYPE, GiveItemAction::fromJson);
        register(SetBlockAction.TYPE, SetBlockAction::fromJson);
        register(SpawnEntityAction.TYPE, SpawnEntityAction::fromJson);
        register(DamageEntityAction.TYPE, DamageEntityAction::fromJson);
        register(PlaySoundAction.TYPE, PlaySoundAction::fromJson);
        register(GrantAdvancementAction.TYPE, GrantAdvancementAction::fromJson);
        register(ExecuteCommandAction.TYPE, ExecuteCommandAction::fromJson);
    }

    /**
     * Registers a new datapack action factory under the supplied identifier.
     */
    public static void register(ResourceLocation id, BiFunction<ResourceLocation, JsonObject, ? extends Action<?>> factory) {
        FACTORIES.put(id, factory);
    }

    /**
     * Resolves a datapack action instance for the provided identifier and JSON payload.
     */
    public static Optional<Action<?>> create(ResourceLocation typeId, ResourceLocation id, JsonObject json) {
        bootstrap();
        BiFunction<ResourceLocation, JsonObject, ? extends Action<?>> factory = FACTORIES.get(typeId);
        if (factory == null) {
            return Optional.empty();
        }
        Action<?> action = factory.apply(id, json);
        return Optional.ofNullable(action);
    }

    /**
     * Replaces the loaded datapack actions with the supplied map.
     */
    public static void setAll(Map<ResourceLocation, Action<?>> values) {
        ACTIONS.clear();
        ACTIONS.putAll(values);
    }

    /**
     * Looks up a hydrated datapack action by identifier.
     */
    public static Optional<Action<?>> lookup(ResourceLocation id) {
        return Optional.ofNullable(ACTIONS.get(id));
    }

    /**
     * Exposes the currently loaded datapack actions.
     */
    public static Map<ResourceLocation, Action<?>> entries() {
        return Collections.unmodifiableMap(ACTIONS);
    }
}
