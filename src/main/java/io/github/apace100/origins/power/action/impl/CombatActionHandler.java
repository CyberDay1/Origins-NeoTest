package io.github.apace100.origins.power.action.impl;

import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralised handler for combat related datapack actions that need to react to
 * upcoming damage events. Actions like {@link ModifyDamageDealtAction} and
 * {@link CriticalHitAction} enqueue transient modifiers using this helper which
 * are then consumed the next time the owning entity deals damage.
 */
public final class CombatActionHandler {
    private static final Map<UUID, Float> PENDING_DAMAGE = new ConcurrentHashMap<>();
    private static final Map<UUID, List<CriticalHitModifier>> PENDING_CRITS = new ConcurrentHashMap<>();
    private static boolean INITIALISED;

    private CombatActionHandler() {
    }

    /**
     * Ensures the handler is registered on the NeoForge event bus.
     */
    public static void init() {
        if (INITIALISED) {
            return;
        }
        INITIALISED = true;
        NeoForge.EVENT_BUS.addListener(CombatActionHandler::onLivingDamage);
    }

    /**
     * Queues bonus damage to be applied to the next outgoing attack by the
     * supplied living entity. The modifier is consumed as soon as it is applied.
     */
    public static void queueBonusDamage(LivingEntity entity, float amount) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (amount == 0.0F) {
            return;
        }
        PENDING_DAMAGE.merge(entity.getUUID(), amount, Float::sum);
    }

    /**
     * Queues a potential critical hit modifier for the next outgoing attack by
     * the supplied entity. Each queued modifier is evaluated once and then
     * discarded regardless of whether the roll succeeded.
     */
    public static void queueCritical(LivingEntity entity, float chance, float multiplier) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        PENDING_CRITS.computeIfAbsent(entity.getUUID(), unused -> new ArrayList<>())
            .add(new CriticalHitModifier(chance, multiplier));
    }

    private static void onLivingDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) {
            return;
        }
        if (attacker.level().isClientSide) {
            return;
        }

        UUID uuid = attacker.getUUID();
        float damage = event.getNewDamage();

        Float bonus = PENDING_DAMAGE.remove(uuid);
        if (bonus != null) {
            damage += bonus;
        }

        List<CriticalHitModifier> modifiers = PENDING_CRITS.remove(uuid);
        if (modifiers != null && !modifiers.isEmpty()) {
            RandomSource random = attacker.getRandom();
            for (CriticalHitModifier modifier : modifiers) {
                if (modifier.roll(random)) {
                    damage *= modifier.multiplier();
                }
            }
        }

        if (damage < 0.0F) {
            damage = 0.0F;
        }

        event.setNewDamage(damage);
    }

    private record CriticalHitModifier(float chance, float multiplier) {
        boolean roll(RandomSource random) {
            if (chance >= 1.0F) {
                return true;
            }
            if (chance <= 0.0F) {
                return false;
            }
            return random.nextFloat() < chance;
        }
    }
}
