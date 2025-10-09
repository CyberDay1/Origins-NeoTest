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
 * Datapack action that prevents the current item pickup interaction from
 * succeeding if the item matches the configured identifier.
 */
public final class PreventItemPickupAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_item_pickup");
    private static final ThreadLocal<PickupContext> CONTEXT = new ThreadLocal<>();
    private static final Codec<PreventItemPickupAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(PreventItemPickupAction::item)
    ).apply(instance, PreventItemPickupAction::new));

    private final Item item;

    private PreventItemPickupAction(Item item) {
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

        PickupContext context = CONTEXT.get();
        if (context == null) {
            return;
        }
        if (!context.stack().is(item)) {
            return;
        }

        context.cancel().run();
    }

    public static PreventItemPickupAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("item")) {
            Origins.LOGGER.warn("Prevent item pickup action '{}' is missing required 'item' field", id);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent item pickup action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> resolved = BuiltInRegistries.ITEM.getOptional(itemId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Prevent item pickup action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        return new PreventItemPickupAction(resolved.get());
    }

    public static Codec<PreventItemPickupAction> codec() {
        return CODEC;
    }

    public static void withContext(ItemStack stack, Runnable cancel, Runnable runnable) {
        CONTEXT.set(new PickupContext(stack, cancel));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    private record PickupContext(ItemStack stack, Runnable cancel) {
    }
}
