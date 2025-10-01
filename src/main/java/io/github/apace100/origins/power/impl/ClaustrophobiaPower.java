package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ClaustrophobiaPower extends Power {
    public ClaustrophobiaPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        BlockPos pos = player.blockPosition();
        boolean cramped = !player.level().isEmptyBlock(pos.above()) && !player.level().isEmptyBlock(pos.above(2));
        if (cramped) {
            if (!player.hasEffect(MobEffects.WEAKNESS) || player.getEffect(MobEffects.WEAKNESS).getDuration() <= 20) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false, true));
            }
            if (!player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) || player.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getDuration() <= 20) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, false, true));
            }
        }
    }
}
