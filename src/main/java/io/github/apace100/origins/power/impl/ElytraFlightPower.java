package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

public class ElytraFlightPower extends Power {
    public ElytraFlightPower(PowerType<?> type) {
        super(type);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(Player player) {
        if (player.isSpectator()) {
            return;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean hasElytraEquipped = !chestItem.isEmpty() && chestItem.getItem() instanceof ElytraItem;
        boolean shouldEnable = !player.isCreative() && !hasElytraEquipped;

        if (shouldEnable) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
            player.resetFallDistance();
        } else if (!player.isCreative() && player.getAbilities().mayfly) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}
