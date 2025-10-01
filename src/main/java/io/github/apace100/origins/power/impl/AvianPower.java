package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.config.OriginsConfigValues;
import io.github.apace100.origins.power.OriginPowerManager;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;

public class AvianPower extends Power {
    private static final Component SLEEP_BLOCKED_MESSAGE = Component.translatable("power.origins.avian.sleep_restriction");

    public AvianPower(PowerType<?> type) {
        super(type);
        NeoForge.EVENT_BUS.addListener(this::onPlayerSleep);
    }

    @Override
    public void tick(Player player) {
        OriginsConfigValues.Avian config = OriginsConfig.get().avian();

        if (config.slowFallingEnabled()) {
            if (!player.hasEffect(MobEffects.SLOW_FALLING) || player.getEffect(MobEffects.SLOW_FALLING).getDuration() <= 40) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 220, 0, false, false, true));
            }
        } else if (player.hasEffect(MobEffects.SLOW_FALLING)) {
            player.removeEffect(MobEffects.SLOW_FALLING);
        }
    }

    private void onPlayerSleep(CanPlayerSleepEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) {
            return;
        }

        if (!OriginPowerManager.hasPower(player, OriginPowerManager.AVIAN)) {
            return;
        }

        BlockPos pos = event.getPos();
        OriginsConfigValues.Avian config = OriginsConfig.get().avian();
        if (pos != null && pos.getY() > config.sleepMaxY()) {
            player.displayClientMessage(SLEEP_BLOCKED_MESSAGE, true);
            event.setProblem(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
        }
    }
}
