package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LaunchIntoAirPower extends Power {
    public LaunchIntoAirPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (!player.onGround() || !player.isShiftKeyDown()) {
            return;
        }

        if (player.tickCount % 120 == 0) {
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.x, 1.0D, movement.z);
        }
    }
}
