package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.player.Player;

public class LaunchIntoAirPower extends Power {
    public LaunchIntoAirPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        // TODO: Implement Elytrian launch ability
    }
}
