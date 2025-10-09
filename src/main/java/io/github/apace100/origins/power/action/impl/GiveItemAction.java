package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

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
 * Datapack action that gives a configured item stack to the invoking player.
 */
public final class GiveItemAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("give_item");
    private static final Codec<GiveItemAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(GiveItemAction::item),
        Codec.INT.optionalFieldOf("count", 1).forGetter(GiveItemAction::count)
    ).apply(instance, GiveItemAction::new));

    private final Item item;
    private final int count;

    private GiveItemAction(Item item, int count) {
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

        ItemStack stack = new ItemStack(item, count);
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }

    public static GiveItemAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("item")) {
            Origins.LOGGER.warn("Give item action '{}' is missing required 'item' field", id);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Give item action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
        if (item.isEmpty()) {
            Origins.LOGGER.warn("Give item action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        int count = GsonHelper.getAsInt(json, "count", 1);
        if (count <= 0) {
            Origins.LOGGER.warn("Give item action '{}' has non-positive count {}", id, count);
            return null;
        }

        return new GiveItemAction(item.get(), count);
    }

    public static Codec<GiveItemAction> codec() {
        return CODEC;
    }
}
