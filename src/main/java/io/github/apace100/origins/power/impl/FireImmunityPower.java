package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class FireImmunityPower extends Power {
    public FireImmunityPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide) {
            return;
        }

        if (!player.hasEffect(MobEffects.FIRE_RESISTANCE) || player.getEffect(MobEffects.FIRE_RESISTANCE).getDuration() <= 40) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 220, 0, false, false, true));
        }

        if (player.isOnFire()) {
            player.clearFire();
        }
    }
}
