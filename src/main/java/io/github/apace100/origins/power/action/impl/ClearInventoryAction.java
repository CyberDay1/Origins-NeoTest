package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Datapack action that removes all items from a player's inventory matching a configured item tag.
 */
public final class ClearInventoryAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "clear_inventory");
    private static final Codec<ClearInventoryAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("tag").forGetter(action -> action.tag.location())
    ).apply(instance, id -> new ClearInventoryAction(TagKey.create(Registries.ITEM, id))));

    private final TagKey<Item> tag;

    private ClearInventoryAction(TagKey<Item> tag) {
        this.tag = tag;
    }

    private TagKey<Item> tag() {
        return tag;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        Inventory inventory = player.getInventory();
        boolean changed = false;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty() && stack.is(tag)) {
                inventory.setItem(slot, ItemStack.EMPTY);
                changed = true;
            }
        }

        if (changed) {
            inventory.setChanged();
        }
    }

    public static ClearInventoryAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("tag")) {
            Origins.LOGGER.warn("Clear inventory action '{}' is missing required 'tag' field", id);
            return null;
        }

        String rawTag = GsonHelper.getAsString(json, "tag");
        ResourceLocation tagId;
        try {
            tagId = ResourceLocation.parse(rawTag);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Clear inventory action '{}' has invalid tag id '{}': {}", id, rawTag, exception.getMessage());
            return null;
        }

        TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
        return new ClearInventoryAction(tag);
    }

    public static Codec<ClearInventoryAction> codec() {
        return CODEC;
    }
}
