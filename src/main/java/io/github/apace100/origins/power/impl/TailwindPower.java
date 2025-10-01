package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.player.Player;

public class TailwindPower extends Power {
    public TailwindPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        // TODO: Provide a sprinting speed boost
    }
}
