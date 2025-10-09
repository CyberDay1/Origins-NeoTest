package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.util.EquipmentSlotUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Datapack action that swaps out an equipped item for a configured replacement.
 */
public final class ReplaceEquippedItemAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("replace_equipped_item");
    private static final Codec<ReplaceEquippedItemAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        EquipmentSlotUtil.codec().fieldOf("slot").forGetter(ReplaceEquippedItemAction::slot),
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ReplaceEquippedItemAction::item),
        Codec.INT.optionalFieldOf("count", 1).forGetter(ReplaceEquippedItemAction::count)
    ).apply(instance, ReplaceEquippedItemAction::new));

    private final EquipmentSlot slot;
    private final Item item;
    private final int count;

    private ReplaceEquippedItemAction(EquipmentSlot slot, Item item, int count) {
        this.slot = slot;
        this.item = item;
        this.count = count;
    }

    private EquipmentSlot slot() {
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

        ItemStack replacement = new ItemStack(item, count);
        ItemStack previous = player.getItemBySlot(slot);
        player.setItemSlot(slot, replacement);
        if (!previous.isEmpty()) {
            if (!player.getInventory().add(previous)) {
                player.drop(previous, false);
            }
        }
    }

    public static ReplaceEquippedItemAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("slot")) {
            Origins.LOGGER.warn("Replace equipped item action '{}' is missing required 'slot' field", id);
            return null;
        }
        if (!json.has("item")) {
            Origins.LOGGER.warn("Replace equipped item action '{}' is missing required 'item' field", id);
            return null;
        }

        String rawSlot = GsonHelper.getAsString(json, "slot");
        EquipmentSlot slot = EquipmentSlotUtil.parse(rawSlot);
        if (slot == null) {
            Origins.LOGGER.warn("Replace equipped item action '{}' references unknown slot '{}'", id, rawSlot);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Replace equipped item action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
        if (item.isEmpty()) {
            Origins.LOGGER.warn("Replace equipped item action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        int count = GsonHelper.getAsInt(json, "count", 1);
        if (count <= 0) {
            Origins.LOGGER.warn("Replace equipped item action '{}' has non-positive count {}", id, count);
            return null;
        }

        return new ReplaceEquippedItemAction(slot, item.get(), count);
    }

    public static Codec<ReplaceEquippedItemAction> codec() {
        return CODEC;
    }
}
