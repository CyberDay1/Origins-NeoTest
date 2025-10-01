package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
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
 * Datapack condition that checks a specific equipment slot for an item.
 */
public final class EquippedItemCondition implements Condition<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "equipped_item");
    private static final Codec<EquippedItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        EquipmentSlotUtil.codec().fieldOf("slot").forGetter(EquippedItemCondition::slot),
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(EquippedItemCondition::item)
    ).apply(instance, EquippedItemCondition::new));

    private final EquipmentSlot slot;
    private final Item item;

    private EquippedItemCondition(EquipmentSlot slot, Item item) {
        this.slot = slot;
        this.item = item;
    }

    private EquipmentSlot slot() {
        return slot;
    }

    private Item item() {
        return item;
    }

    @Override
    public boolean test(Player player) {
        if (player == null) {
            return false;
        }
        ItemStack stack = player.getItemBySlot(slot);
        return !stack.isEmpty() && stack.is(item);
    }

    public static EquippedItemCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("slot")) {
            Origins.LOGGER.warn("Equipped item condition '{}' is missing required 'slot' field", id);
            return null;
        }
        if (!json.has("item")) {
            Origins.LOGGER.warn("Equipped item condition '{}' is missing required 'item' field", id);
            return null;
        }

        String rawSlot = GsonHelper.getAsString(json, "slot");
        EquipmentSlot slot = EquipmentSlotUtil.parse(rawSlot);
        if (slot == null) {
            Origins.LOGGER.warn("Equipped item condition '{}' references unknown slot '{}'", id, rawSlot);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Equipped item condition '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
        if (item.isEmpty()) {
            Origins.LOGGER.warn("Equipped item condition '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        return new EquippedItemCondition(slot, item.get());
    }

    public static Codec<EquippedItemCondition> codec() {
        return CODEC;
    }
}
