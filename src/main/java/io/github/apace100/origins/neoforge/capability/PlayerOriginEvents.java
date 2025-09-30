package io.github.apace100.origins.neoforge.capability;

import io.github.apace100.origins.Origins;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod.EventBusSubscriber(modid = Origins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerOriginEvents {
    private PlayerOriginEvents() {
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        PlayerOriginManager.copy(event.getOriginal(), event.getEntity());
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PlayerOrigin origin = PlayerOriginManager.get(player);
        if (origin != null) {
            PlayerOriginManager.save(player, origin);
        }

        PlayerOriginManager.remove(player);
    }
}
