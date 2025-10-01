package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class PhantomInvisibilityPower extends Power {
    public PhantomInvisibilityPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (player.isShiftKeyDown()) {
            if (!player.hasEffect(MobEffects.INVISIBILITY) || player.getEffect(MobEffects.INVISIBILITY).getDuration() <= 40) {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false, true));
            }
        }
    }
}
