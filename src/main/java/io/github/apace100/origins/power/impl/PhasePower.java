package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.player.Player;

public class PhasePower extends Power {
    public PhasePower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide) {
            return;
        }

        if (player.isShiftKeyDown()) {
            player.noPhysics = true;
            player.resetFallDistance();
        } else if (player.noPhysics) {
            player.noPhysics = false;
        }
    }
}
