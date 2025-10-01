package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;

public class AquaticPower extends Power {
    public AquaticPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (player.isEyeInFluid(FluidTags.WATER)) {
            player.setAirSupply(player.getMaxAirSupply());
        }
    }
}
