package io.github.apace100.origins.common.item;

import io.github.apace100.origins.client.OriginsClientHooks;
import io.github.apace100.origins.neoforge.capability.OriginCapabilities;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OrbOfOriginItem extends Item {
    public OrbOfOriginItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerOrigin origin = player.getCapability(OriginCapabilities.PLAYER_ORIGIN);
        if (origin != null && origin.hasChosen()) {
            return InteractionResultHolder.pass(stack);
        }

        if (level.isClientSide) {
            OriginsClientHooks.openOriginScreen(stack);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.consume(stack);
    }
}
