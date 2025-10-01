package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Scaffold implementation for equipped item datapack conditions.
 */
public final class EquippedItemCondition implements Condition<ItemStack> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "equipped_item");
    private static final Codec<EquippedItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("item").forGetter(EquippedItemCondition::itemId)
    ).apply(instance, EquippedItemCondition::new));

    private final Optional<ResourceLocation> itemId;

    private EquippedItemCondition(Optional<ResourceLocation> itemId) {
        this.itemId = itemId;
    }

    public Optional<ResourceLocation> itemId() {
        return itemId;
    }

    @Override
    public boolean test(ItemStack stack) {
        // TODO: Inspect the equipped stack against the configured identifier and predicates.
        return false;
    }

    public static EquippedItemCondition fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("item")) {
            String raw = GsonHelper.getAsString(json, "item");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Equipped item condition '{}' has invalid item id '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new EquippedItemCondition(parsed);
    }

    public static Codec<EquippedItemCondition> codec() {
        return CODEC;
    }
}
