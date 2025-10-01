package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EnderStepPower extends Power {
    public EnderStepPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (player.isShiftKeyDown() && player.tickCount % 60 == 0) {
            Vec3 look = player.getLookAngle();
            Vec3 destination = player.position().add(look.scale(6.0D));
            serverPlayer.teleportTo(destination.x, destination.y, destination.z);
        }
    }
}
