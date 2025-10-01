package io.github.apace100.origins.events;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.capability.PlayerOriginUtil;
import io.github.apace100.origins.network.NetUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber(modid = Origins.MOD_ID)
public final class ServerPlayerEvents {
    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;
        PlayerOriginUtil.get(sp).ifPresent(data -> NetUtil.syncOriginTo(sp, data.getOriginId()));
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;
        var original = e.getOriginal();
        var newCap = PlayerOriginUtil.getOrCreate(sp);
        PlayerOriginUtil.get(original).ifPresent(oldCap -> {
            newCap.setOriginId(oldCap.getOriginId());
            NetUtil.syncOriginTo(sp, newCap.getOriginId());
        });
    }
}
