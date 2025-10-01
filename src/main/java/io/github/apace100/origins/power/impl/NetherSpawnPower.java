package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.player.Player;

public class NetherSpawnPower extends Power {
    public NetherSpawnPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        // TODO: Spawn the player in the Nether dimension
    }
}
