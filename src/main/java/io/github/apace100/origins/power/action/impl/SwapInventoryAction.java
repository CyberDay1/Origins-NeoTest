package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.util.InventorySlotUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Datapack action that swaps the contents of two inventory slots.
 */
public final class SwapInventoryAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("swap_inventory");
    private static final Codec<SwapInventoryAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        InventorySlotUtil.CODEC.fieldOf("slot_a").forGetter(SwapInventoryAction::slotA),
        InventorySlotUtil.CODEC.fieldOf("slot_b").forGetter(SwapInventoryAction::slotB)
    ).apply(instance, SwapInventoryAction::new));

    private final int slotA;
    private final int slotB;

    private SwapInventoryAction(int slotA, int slotB) {
        this.slotA = slotA;
        this.slotB = slotB;
    }

    private int slotA() {
        return slotA;
    }

    private int slotB() {
        return slotB;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        if (slotA == slotB) {
            return;
        }

        Inventory inventory = player.getInventory();
        int size = inventory.getContainerSize();
        if (slotA < 0 || slotA >= size || slotB < 0 || slotB >= size) {
            return;
        }

        ItemStack stackA = inventory.getItem(slotA).copy();
        ItemStack stackB = inventory.getItem(slotB).copy();
        inventory.setItem(slotA, stackB);
        inventory.setItem(slotB, stackA);
        inventory.setChanged();
    }

    public static SwapInventoryAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("slot_a")) {
            Origins.LOGGER.warn("Swap inventory action '{}' is missing required 'slot_a' field", id);
            return null;
        }
        if (!json.has("slot_b")) {
            Origins.LOGGER.warn("Swap inventory action '{}' is missing required 'slot_b' field", id);
            return null;
        }

        String rawA = GsonHelper.getAsString(json, "slot_a");
        Optional<Integer> slotA = InventorySlotUtil.decode(rawA)
            .resultOrPartial(message -> Origins.LOGGER.warn(
                "Swap inventory action '{}' has invalid slot_a '{}': {}", id, rawA, message));
        if (slotA.isEmpty()) {
            return null;
        }

        String rawB = GsonHelper.getAsString(json, "slot_b");
        Optional<Integer> slotB = InventorySlotUtil.decode(rawB)
            .resultOrPartial(message -> Origins.LOGGER.warn(
                "Swap inventory action '{}' has invalid slot_b '{}': {}", id, rawB, message));
        if (slotB.isEmpty()) {
            return null;
        }

        return new SwapInventoryAction(slotA.get(), slotB.get());
    }

    public static Codec<SwapInventoryAction> codec() {
        return CODEC;
    }
}
