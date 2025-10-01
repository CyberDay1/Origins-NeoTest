package io.github.apace100.origins.events;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.network.NetUtil;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import net.minecraft.resources.ResourceLocation;
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
        PlayerOrigin origin = PlayerOriginManager.get(sp);
        if (origin != null) {
            NetUtil.syncOriginTo(sp, origin.getOriginIdOptional().map(ResourceLocation::toString).orElse(""));
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;
        var original = e.getOriginal();
        PlayerOrigin newCap = PlayerOriginManager.get(sp);
        PlayerOrigin oldCap = PlayerOriginManager.get(original);
        if (newCap != null && oldCap != null) {
            newCap.copyFrom(oldCap);
            PlayerOriginManager.save(sp, newCap);
            NetUtil.syncOriginTo(sp, newCap.getOriginIdOptional().map(ResourceLocation::toString).orElse(""));
        }
    }
}
