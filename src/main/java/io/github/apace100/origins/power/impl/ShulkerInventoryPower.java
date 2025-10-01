package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.player.Player;

public class ShulkerInventoryPower extends Power {
    public ShulkerInventoryPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        // TODO: Provide access to the Shulk extra inventory
    }
}
