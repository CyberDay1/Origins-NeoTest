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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Datapack action that stops players from eating a configured item.
 */
public final class PreventFoodAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_food");
    private static final Codec<PreventFoodAction> CODEC = RecordCodecBuilder.<PreventFoodAction>create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("item").forGetter(PreventFoodAction::itemId)
    ).apply(instance, itemId -> fromCodec(itemId))).flatXmap(PreventFoodAction::validateCodec, PreventFoodAction::validateCodec);

    private final ResourceLocation itemId;
    private final Item item;

    private PreventFoodAction(ResourceLocation itemId, Item item) {
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

        boolean matched = false;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(item)) {
                continue;
            }
            matched = true;
            if (player.isUsingItem() && player.getUsedItemHand() == hand) {
                player.stopUsingItem();
            }
        }

        if (matched && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCooldowns().addCooldown(item, 5);
        }
    }

    public static PreventFoodAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("item")) {
            Origins.LOGGER.warn("Prevent food action '{}' is missing required 'item' field", id);
            return null;
        }

        String raw = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(raw);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent food action '{}' has invalid item id '{}': {}", id, raw, exception.getMessage());
            return null;
        }

        Optional<Item> resolved = BuiltInRegistries.ITEM.getOptional(itemId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Prevent food action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        return new PreventFoodAction(itemId, resolved.get());
    }

    public static Codec<PreventFoodAction> codec() {
        return CODEC;
    }

    private static PreventFoodAction fromCodec(ResourceLocation itemId) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        return new PreventFoodAction(itemId, item);
    }

    private static DataResult<PreventFoodAction> validateCodec(PreventFoodAction action) {
        return DataResult.success(action);
    }
}
