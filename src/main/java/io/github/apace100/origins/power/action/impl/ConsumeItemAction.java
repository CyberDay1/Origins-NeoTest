package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Datapack action that consumes a configured quantity of an item from the player's inventory.
 */
public final class ConsumeItemAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "consume_item");
    private static final Codec<ConsumeItemAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ConsumeItemAction::item),
        Codec.INT.optionalFieldOf("count", 1).forGetter(ConsumeItemAction::count)
    ).apply(instance, ConsumeItemAction::new));

    private final Item item;
    private final int count;

    private ConsumeItemAction(Item item, int count) {
        this.item = item;
        this.count = count;
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

        int remaining = count;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty() || !stack.is(item)) {
                continue;
            }

            int removed = Math.min(stack.getCount(), remaining);
            stack.shrink(removed);
            remaining -= removed;
            if (remaining <= 0) {
                break;
            }
        }
    }

    public static ConsumeItemAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("item")) {
            Origins.LOGGER.warn("Consume item action '{}' is missing required 'item' field", id);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Consume item action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
        if (item.isEmpty()) {
            Origins.LOGGER.warn("Consume item action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        int count = GsonHelper.getAsInt(json, "count", 1);
        if (count <= 0) {
            Origins.LOGGER.warn("Consume item action '{}' has non-positive count {}", id, count);
            return null;
        }

        return new ConsumeItemAction(item.get(), count);
    }

    public static Codec<ConsumeItemAction> codec() {
        return CODEC;
    }
}
