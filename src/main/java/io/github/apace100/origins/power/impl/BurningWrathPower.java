package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class BurningWrathPower extends Power {
    public BurningWrathPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (player.isOnFire()) {
            if (!player.hasEffect(MobEffects.DAMAGE_BOOST) || player.getEffect(MobEffects.DAMAGE_BOOST).getDuration() <= 20) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, 0, false, false, true));
            }
        }
    }
}
