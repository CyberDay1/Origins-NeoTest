package io.github.apace100.origins.power.action.registry;

import com.google.gson.JsonObject;
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.power.action.impl.ApplyEffectAction;
import io.github.apace100.origins.power.action.impl.BiEntityAction;
import io.github.apace100.origins.power.action.impl.BlockAction;
import io.github.apace100.origins.power.action.impl.ClearAllEffectsAction;
import io.github.apace100.origins.power.action.impl.ClearEffectAction;
import io.github.apace100.origins.power.action.impl.DamageEntityAction;
import io.github.apace100.origins.power.action.impl.EntityAction;
import io.github.apace100.origins.power.action.impl.ConsumeItemAction;
import io.github.apace100.origins.power.action.impl.DamageItemAction;
import io.github.apace100.origins.power.action.impl.ExecuteCommandAction;
import io.github.apace100.origins.power.action.impl.ExplosionAction;
import io.github.apace100.origins.power.action.impl.GiveItemAction;
import io.github.apace100.origins.power.action.impl.GrantAdvancementAction;
import io.github.apace100.origins.power.action.impl.HealAction;
import io.github.apace100.origins.power.action.impl.ItemAction;
import io.github.apace100.origins.power.action.impl.LightningAction;
import io.github.apace100.origins.power.action.impl.ModifyAttributeAction;
import io.github.apace100.origins.power.action.impl.ModifyDamageTakenAction;
import io.github.apace100.origins.power.action.impl.ModifyFoodAction;
import io.github.apace100.origins.power.action.impl.ModifyProjectileAction;
import io.github.apace100.origins.power.action.impl.KnockbackAction;
import io.github.apace100.origins.power.action.impl.ParticleAction;
import io.github.apace100.origins.power.action.impl.PlaySoundAction;
import io.github.apace100.origins.power.action.impl.PreventBlockUseAction;
import io.github.apace100.origins.power.action.impl.PreventFoodAction;
import io.github.apace100.origins.power.action.impl.PreventItemUseAction;
import io.github.apace100.origins.power.action.impl.PreventProjectileAction;
import io.github.apace100.origins.power.action.impl.ReplaceEquippedItemAction;
import io.github.apace100.origins.power.action.impl.ResetAttributeAction;
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
        register(ParticleAction.TYPE, ParticleAction::fromJson);
        register(LightningAction.TYPE, LightningAction::fromJson);
        register(ExplosionAction.TYPE, ExplosionAction::fromJson);
        register(ApplyEffectAction.TYPE, ApplyEffectAction::fromJson);
        register(ClearEffectAction.TYPE, ClearEffectAction::fromJson);
        register(ClearAllEffectsAction.TYPE, ClearAllEffectsAction::fromJson);
        register(ModifyAttributeAction.TYPE, ModifyAttributeAction::fromJson);
        register(ResetAttributeAction.TYPE, ResetAttributeAction::fromJson);
        register(ConsumeItemAction.TYPE, ConsumeItemAction::fromJson);
        register(ModifyFoodAction.TYPE, ModifyFoodAction::fromJson);
        register(PreventFoodAction.TYPE, PreventFoodAction::fromJson);
        register(ReplaceEquippedItemAction.TYPE, ReplaceEquippedItemAction::fromJson);
        register(DamageItemAction.TYPE, DamageItemAction::fromJson);
        register(ModifyDamageTakenAction.TYPE, ModifyDamageTakenAction::fromJson);
        register(HealAction.TYPE, HealAction::fromJson);
        register(KnockbackAction.TYPE, KnockbackAction::fromJson);
        register(PreventItemUseAction.TYPE, PreventItemUseAction::fromJson);
        register(PreventBlockUseAction.TYPE, PreventBlockUseAction::fromJson);
        register(ModifyProjectileAction.TYPE, ModifyProjectileAction::fromJson);
        register(PreventProjectileAction.TYPE, PreventProjectileAction::fromJson);
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
