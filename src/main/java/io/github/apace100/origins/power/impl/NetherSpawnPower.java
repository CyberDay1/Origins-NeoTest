package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NetherSpawnPower extends Power {
    public NetherSpawnPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.getRespawnDimension() != Level.NETHER) {
            BlockPos pos = serverPlayer.blockPosition();
            serverPlayer.setRespawnPosition(Level.NETHER, pos, serverPlayer.getYRot(), true, false);
        }
    }
}
