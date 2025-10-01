package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShulkerInventoryPower extends Power {
    public ShulkerInventoryPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chestItem.isEmpty()) {
            ItemStack toStore = chestItem.copy();
            player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            if (!player.getInventory().add(toStore)) {
                player.drop(toStore, true);
            }
        }
    }
}
