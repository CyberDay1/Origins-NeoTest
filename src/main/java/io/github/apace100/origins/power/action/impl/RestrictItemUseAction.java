package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Datapack action that blocks the use of any item belonging to the configured
 * item tag for the current interaction.
 */
public final class RestrictItemUseAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("restrict_item_use");
    private static final ThreadLocal<ItemUseContext> CONTEXT = new ThreadLocal<>();
    private static final Codec<RestrictItemUseAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("tag").forGetter(action -> action.tag.location())
    ).apply(instance, id -> new RestrictItemUseAction(TagKey.create(Registries.ITEM, id))));

    private final TagKey<Item> tag;

    private RestrictItemUseAction(TagKey<Item> tag) {
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

        ItemUseContext context = CONTEXT.get();
        if (context != null && context.stack().is(tag)) {
            context.cancel().run();
        }

        if (player.isUsingItem() && player.getUseItem().is(tag)) {
            player.stopUsingItem();
        }

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = player.getItemInHand(hand);
            if (held.is(tag)) {
                if (player.isUsingItem() && player.getUsedItemHand() == hand) {
                    player.stopUsingItem();
                }
            }
        }
    }

    public static RestrictItemUseAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("tag")) {
            Origins.LOGGER.warn("Restrict item use action '{}' is missing required 'tag' field", id);
            return null;
        }

        String rawTag = GsonHelper.getAsString(json, "tag");
        ResourceLocation tagId;
        try {
            tagId = ResourceLocation.parse(rawTag);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Restrict item use action '{}' has invalid tag id '{}': {}", id, rawTag, exception.getMessage());
            return null;
        }

        return new RestrictItemUseAction(TagKey.create(Registries.ITEM, tagId));
    }

    public static Codec<RestrictItemUseAction> codec() {
        return CODEC;
    }

    public static void withContext(ItemStack stack, Runnable cancel, Runnable runnable) {
        CONTEXT.set(new ItemUseContext(stack, cancel));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    private record ItemUseContext(ItemStack stack, Runnable cancel) {
    }
}
