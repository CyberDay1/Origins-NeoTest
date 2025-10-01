package io.github.apace100.origins.power.action.impl;

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
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;

import java.util.Optional;

/**
 * Datapack action that cancels projectile launches from a configured weapon.
 */
public final class PreventProjectileAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "prevent_projectile");
    private static final Codec<PreventProjectileAction> CODEC = RecordCodecBuilder.<PreventProjectileAction>create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("item").forGetter(PreventProjectileAction::itemId)
    ).apply(instance, itemId -> fromCodec(itemId))).flatXmap(PreventProjectileAction::validateCodec, PreventProjectileAction::validateCodec);

    private final ResourceLocation itemId;
    private final Item item;

    private PreventProjectileAction(ResourceLocation itemId, Item item) {
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

    public static PreventProjectileAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("item")) {
            Origins.LOGGER.warn("Prevent projectile action '{}' is missing required 'item' field", id);
            return null;
        }

        String raw = GsonHelper.getAsString(json, "item");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(raw);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent projectile action '{}' has invalid item id '{}': {}", id, raw, exception.getMessage());
            return null;
        }

        Optional<Item> resolved = BuiltInRegistries.ITEM.getOptional(itemId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Prevent projectile action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        Item item = resolved.get();
        if (!(item instanceof ProjectileWeaponItem) && !(item instanceof ProjectileItem) && !(item instanceof TridentItem)) {
            Origins.LOGGER.warn("Prevent projectile action '{}' references non-projectile weapon '{}'", id, itemId);
            return null;
        }

        return new PreventProjectileAction(itemId, item);
    }

    public static Codec<PreventProjectileAction> codec() {
        return CODEC;
    }

    private static PreventProjectileAction fromCodec(ResourceLocation itemId) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        return new PreventProjectileAction(itemId, item);
    }

    private static DataResult<PreventProjectileAction> validateCodec(PreventProjectileAction action) {
        if (!(action.item instanceof ProjectileWeaponItem) && !(action.item instanceof ProjectileItem)
            && !(action.item instanceof TridentItem)) {
            return DataResult.error(() -> "Item " + action.itemId + " is not a projectile weapon");
        }
        return DataResult.success(action);
    }
}
