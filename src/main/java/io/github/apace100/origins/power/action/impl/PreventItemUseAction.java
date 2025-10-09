package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Optional;

/**
 * Datapack action that cancels item usage for a configured hand and item.
 */
public final class PreventItemUseAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_item_use");
    private static final Codec<PreventItemUseAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.comapFlatMap(PreventItemUseAction::decodeHand, PreventItemUseAction::encodeHand).fieldOf("slot").forGetter(action -> action.hand),
        ResourceLocation.CODEC.fieldOf("item").forGetter(PreventItemUseAction::itemId)
    ).apply(instance, PreventItemUseAction::fromCodec));

    private final InteractionHand hand;
    private final ResourceLocation itemId;
    private final Item item;

    private PreventItemUseAction(InteractionHand hand, ResourceLocation itemId, Item item) {
        this.hand = hand;
        this.itemId = itemId;
        this.item = item;
    }

    private ResourceLocation itemId() {
        return itemId;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }
        if (!(player.level() instanceof ServerLevel)) {
            return;
        }

        ItemStack held = player.getItemInHand(hand);
        if (!held.is(item)) {
            return;
        }

        if (player.isUsingItem() && player.getUsedItemHand() == hand) {
            player.stopUsingItem();
        }
    }

    public static PreventItemUseAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("slot")) {
            Origins.LOGGER.warn("Prevent item use action '{}' is missing required 'slot' field", id);
            return null;
        }
        if (!json.has("item")) {
            Origins.LOGGER.warn("Prevent item use action '{}' is missing required 'item' field", id);
            return null;
        }

        String rawSlot = GsonHelper.getAsString(json, "slot");
        Optional<InteractionHand> parsedHand = parseHand(rawSlot);
        if (parsedHand.isEmpty()) {
            Origins.LOGGER.warn("Prevent item use action '{}' specified unsupported slot '{}'", id, rawSlot);
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent item use action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> resolved = BuiltInRegistries.ITEM.getOptional(itemId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Prevent item use action '{}' references unknown item {}", id, itemId);
            return null;
        }

        return new PreventItemUseAction(parsedHand.get(), itemId, resolved.get());
    }

    public static Codec<PreventItemUseAction> codec() {
        return CODEC;
    }

    private static DataResult<InteractionHand> decodeHand(String value) {
        return parseHand(value)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "Unsupported hand value: " + value));
    }

    private static String encodeHand(InteractionHand hand) {
        return hand == InteractionHand.OFF_HAND ? "offhand" : "mainhand";
    }

    private static Optional<InteractionHand> parseHand(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "mainhand", "main_hand", "main" -> Optional.of(InteractionHand.MAIN_HAND);
            case "offhand", "off_hand", "off" -> Optional.of(InteractionHand.OFF_HAND);
            default -> Optional.empty();
        };
    }

    private static PreventItemUseAction fromCodec(InteractionHand hand, ResourceLocation itemId) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        return new PreventItemUseAction(hand, itemId, item);
    }
}
