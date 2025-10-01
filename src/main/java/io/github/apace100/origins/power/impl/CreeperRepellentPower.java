package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class CreeperRepellentPower extends Power {
    public CreeperRepellentPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        List<Creeper> creepers = player.level().getEntitiesOfClass(Creeper.class, player.getBoundingBox().inflate(8.0D));
        for (Creeper creeper : creepers) {
            if (creeper.getTarget() == player) {
                creeper.setTarget(null);
                creeper.setSwellDir(-1);
            }
        }
    }
}
