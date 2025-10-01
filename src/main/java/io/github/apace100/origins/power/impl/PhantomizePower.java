package io.github.apace100.origins.power.impl;

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
    private static final int HUNGER_INTERVAL = 80;
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

        applyInvisibility(player);
        handleMovement(player);
        drainHungerIfNeeded(player);
    }

    private void applyInvisibility(Player player) {
        if (!player.level().isClientSide) {
            MobEffectInstance current = player.getEffect(MobEffects.INVISIBILITY);
            if (current == null || current.getDuration() <= 40) {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false, true));
            }
        }
    }

    private void handleMovement(Player player) {
        if (player.isShiftKeyDown() && !player.isSpectator()) {
            player.noPhysics = true;
            player.resetFallDistance();
        } else if (player.noPhysics && !player.isSpectator()) {
            player.noPhysics = false;
        }
    }

    private void drainHungerIfNeeded(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.isCreative() || player.isSpectator()) {
            return;
        }

        if (serverPlayer.tickCount % HUNGER_INTERVAL != 0) {
            return;
        }

        int currentFood = serverPlayer.getFoodData().getFoodLevel();
        if (currentFood > 0) {
            serverPlayer.getFoodData().setFoodLevel(currentFood - 1);
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
