package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;

public class WeakArmsPower extends Power {
    public WeakArmsPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (!(player.getMainHandItem().getItem() instanceof PickaxeItem)) {
            if (!player.hasEffect(MobEffects.DIG_SLOWDOWN) || player.getEffect(MobEffects.DIG_SLOWDOWN).getDuration() <= 20) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1, false, false, true));
            }
        }
    }
}
