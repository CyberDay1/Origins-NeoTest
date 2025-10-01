package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class FeatherweightPower extends Power {
    public FeatherweightPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (!player.hasEffect(MobEffects.SLOW_FALLING) || player.getEffect(MobEffects.SLOW_FALLING).getDuration() <= 40) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 220, 0, false, false, true));
        }
    }
}
