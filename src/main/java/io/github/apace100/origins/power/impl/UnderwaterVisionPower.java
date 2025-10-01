package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.config.OriginsConfigValues;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class UnderwaterVisionPower extends Power {
    public UnderwaterVisionPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        OriginsConfigValues.Merling config = OriginsConfig.get().merling();
        boolean underwater = player.isEyeInFluid(FluidTags.WATER);
        if (config.underwaterVisionEnabled() && underwater) {
            if (!player.hasEffect(MobEffects.NIGHT_VISION) || player.getEffect(MobEffects.NIGHT_VISION).getDuration() <= 40) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, true));
            }
        } else if (player.hasEffect(MobEffects.NIGHT_VISION)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
