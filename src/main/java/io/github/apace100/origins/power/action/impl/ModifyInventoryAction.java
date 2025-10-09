package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.util.InventorySlotUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Datapack action that replaces the contents of a specific player inventory slot.
 */
public final class ModifyInventoryAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("modify_inventory");
    private static final Codec<ModifyInventoryAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        InventorySlotUtil.CODEC.fieldOf("slot").forGetter(ModifyInventoryAction::slot),
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ModifyInventoryAction::item),
        Codec.INT.optionalFieldOf("count", 1).forGetter(ModifyInventoryAction::count)
    ).apply(instance, ModifyInventoryAction::new));

    private final int slot;
    private final Item item;
    private final int count;

    private ModifyInventoryAction(int slot, Item item, int count) {
        this.slot = slot;
        this.item = item;
        this.count = count;
    }

    private int slot() {
        return slot;
    }

    private Item item() {
        return item;
    }

    private int count() {
        return count;
    }

    @Override
    public void execute(Player player) {
        if (player == null || count <= 0) {
            return;
        }

        Inventory inventory = player.getInventory();
        if (slot < 0 || slot >= inventory.getContainerSize()) {
            return;
        }

        ItemStack stack = new ItemStack(item, count);
        int max = stack.getMaxStackSize();
        if (stack.getCount() > max) {
            stack.setCount(max);
        }
        inventory.setItem(slot, stack);
        inventory.setChanged();
    }

    public static ModifyInventoryAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("slot")) {
            Origins.LOGGER.warn("Modify inventory action '{}' is missing required 'slot' field", id);
            return null;
        }
        String rawSlot = GsonHelper.getAsString(json, "slot");
        Optional<Integer> slot = InventorySlotUtil.decode(rawSlot)
            .resultOrPartial(message -> Origins.LOGGER.warn(
                "Modify inventory action '{}' has invalid slot '{}': {}", id, rawSlot, message));
        if (slot.isEmpty()) {
            return null;
        }

        if (!json.has("item")) {
            Origins.LOGGER.warn("Modify inventory action '{}' is missing required 'item' field", id);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Modify inventory action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
        if (item.isEmpty()) {
            Origins.LOGGER.warn("Modify inventory action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        int count = GsonHelper.getAsInt(json, "count", 1);
        if (count <= 0) {
            Origins.LOGGER.warn("Modify inventory action '{}' has non-positive count {}", id, count);
            return null;
        }

        return new ModifyInventoryAction(slot.get(), item.get(), count);
    }

    public static Codec<ModifyInventoryAction> codec() {
        return CODEC;
    }
}
