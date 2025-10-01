package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.config.OriginsConfigValues;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class PhantomizePower extends Power {
    private static final Component OUT_OF_HUNGER_MESSAGE = Component.translatable("power.origins.phantomize.out_of_hunger");

    public PhantomizePower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        PlayerOrigin origin = PlayerOriginManager.get(player);
        if (origin == null) {
            disablePhasing(player);
            return;
        }

        boolean phantomized = origin.isPhantomized();
        if (!phantomized) {
            disablePhasing(player);
            return;
        }

        OriginsConfigValues.Phantom config = OriginsConfig.get().phantom();

        applyInvisibility(player);
        handleMovement(player, config.allowWallPhasing());
        drainHungerIfNeeded(player, config);
    }

    private void applyInvisibility(Player player) {
        if (!player.level().isClientSide) {
            MobEffectInstance current = player.getEffect(MobEffects.INVISIBILITY);
            if (current == null || current.getDuration() <= 40) {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false, true));
            }
        }
    }

    private void handleMovement(Player player, boolean allowWallPhasing) {
        if (allowWallPhasing && player.isShiftKeyDown() && !player.isSpectator()) {
            player.noPhysics = true;
            player.resetFallDistance();
        } else if (player.noPhysics && !player.isSpectator()) {
            player.noPhysics = false;
        }
    }

    private void drainHungerIfNeeded(Player player, OriginsConfigValues.Phantom config) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.isCreative() || player.isSpectator()) {
            return;
        }

        int interval = Math.max(1, config.hungerDrainIntervalTicks());
        if (serverPlayer.tickCount % interval != 0) {
            return;
        }

        int drainAmount = Math.max(0, config.hungerDrainPerInterval());
        if (drainAmount <= 0) {
            return;
        }

        int currentFood = serverPlayer.getFoodData().getFoodLevel();
        if (currentFood > 0) {
            serverPlayer.getFoodData().setFoodLevel(Math.max(0, currentFood - drainAmount));
        } else {
            serverPlayer.displayClientMessage(OUT_OF_HUNGER_MESSAGE, true);
            PlayerOriginManager.setPhantomized(serverPlayer, false);
            disablePhasing(serverPlayer);
        }
    }

    private void disablePhasing(Player player) {
        if (!player.isSpectator()) {
            player.noPhysics = false;
        }
        if (!player.level().isClientSide && player.hasEffect(MobEffects.INVISIBILITY)) {
            player.removeEffect(MobEffects.INVISIBILITY);
        }
    }
}
