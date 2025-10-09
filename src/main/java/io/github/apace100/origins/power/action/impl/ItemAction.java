package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Scaffold implementation for item based datapack actions.
 */
public final class ItemAction implements Action<ItemStack> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("item");
    private static final Codec<ItemAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("item").forGetter(ItemAction::itemId)
    ).apply(instance, ItemAction::new));

    private final Optional<ResourceLocation> itemId;

    private ItemAction(Optional<ResourceLocation> itemId) {
        this.itemId = itemId;
    }

    public Optional<ResourceLocation> itemId() {
        return itemId;
    }

    @Override
    public void execute(ItemStack stack) {
        // TODO: Implement Fabric parity behaviour for item actions (give/modify).
    }

    public static ItemAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<ResourceLocation> parsed = Optional.empty();
        if (json.has("item")) {
            String raw = GsonHelper.getAsString(json, "item");
            try {
                parsed = Optional.of(ResourceLocation.parse(raw));
            } catch (IllegalArgumentException exception) {
                Origins.LOGGER.warn("Failed to parse item action '{}' item id '{}': {}", id, raw, exception.getMessage());
                return null;
            }
        }
        return new ItemAction(parsed);
    }

    public static Codec<ItemAction> codec() {
        return CODEC;
    }
}
