package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Datapack condition that checks whether an item stack belongs to a restricted
 * item tag.
 */
public final class ItemRestrictedCondition implements Condition<ItemStack> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "item_restricted");
    private static final Codec<ItemRestrictedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("tag").forGetter(condition -> condition.tag.location())
    ).apply(instance, id -> new ItemRestrictedCondition(TagKey.create(Registries.ITEM, id))));

    private final TagKey<Item> tag;

    private ItemRestrictedCondition(TagKey<Item> tag) {
        this.tag = tag;
    }

    private TagKey<Item> tag() {
        return tag;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(tag);
    }

    public static ItemRestrictedCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("tag")) {
            Origins.LOGGER.warn("Item restricted condition '{}' is missing required 'tag' field", id);
            return null;
        }

        String rawTag = GsonHelper.getAsString(json, "tag");
        ResourceLocation tagId;
        try {
            tagId = ResourceLocation.parse(rawTag);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Item restricted condition '{}' has invalid tag id '{}': {}", id, rawTag, exception.getMessage());
            return null;
        }

        return new ItemRestrictedCondition(TagKey.create(Registries.ITEM, tagId));
    }

    public static Codec<ItemRestrictedCondition> codec() {
        return CODEC;
    }
}
