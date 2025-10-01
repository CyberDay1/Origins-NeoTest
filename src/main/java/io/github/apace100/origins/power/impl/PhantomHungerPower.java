package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class PhantomHungerPower extends Power {
    public PhantomHungerPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide) {
            return;
        }

        BlockPos pos = player.blockPosition();
        if (player.level().isDay() && player.level().canSeeSkyFromBelowWater(pos)) {
            if (player.tickCount % 20 == 0) {
                player.causeFoodExhaustion(0.75F);
            }
        }
    }
}
