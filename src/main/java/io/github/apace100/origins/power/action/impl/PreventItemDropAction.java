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
 * Datapack action that prevents a player from dropping the configured item
 * during the current drop interaction.
 */
public final class PreventItemDropAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_item_drop");
    private static final ThreadLocal<DropContext> CONTEXT = new ThreadLocal<>();
    private static final Codec<PreventItemDropAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(PreventItemDropAction::item)
    ).apply(instance, PreventItemDropAction::new));

    private final Item item;

    private PreventItemDropAction(Item item) {
        this.item = item;
    }

    private Item item() {
        return item;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        DropContext context = CONTEXT.get();
        if (context == null) {
            return;
        }
        if (!context.stack().is(item)) {
            return;
        }

        context.cancel().run();
    }

    public static PreventItemDropAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("item")) {
            Origins.LOGGER.warn("Prevent item drop action '{}' is missing required 'item' field", id);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent item drop action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> resolved = BuiltInRegistries.ITEM.getOptional(itemId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Prevent item drop action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        return new PreventItemDropAction(resolved.get());
    }

    public static Codec<PreventItemDropAction> codec() {
        return CODEC;
    }

    public static void withContext(ItemStack stack, Runnable cancel, Runnable runnable) {
        CONTEXT.set(new DropContext(stack, cancel));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    private record DropContext(ItemStack stack, Runnable cancel) {
    }
}
