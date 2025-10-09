package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

/**
 * Datapack condition that checks for a specific enchantment level range on an item stack.
 */
public final class ItemEnchantmentCondition implements Condition<ItemStack> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("item_enchantment");
    private static final RegistryAccess.Frozen BUILTIN_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    private static final Registry<Enchantment> ENCHANTMENT_REGISTRY = BUILTIN_ACCESS.registryOrThrow(Registries.ENCHANTMENT);
    private static final Codec<ItemEnchantmentCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Enchantment.CODEC.fieldOf("enchantment").forGetter(ItemEnchantmentCondition::enchantment),
        Codec.INT.optionalFieldOf("min", 0).forGetter(ItemEnchantmentCondition::minLevel),
        Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(ItemEnchantmentCondition::maxLevel)
    ).apply(instance, ItemEnchantmentCondition::new));

    private final Holder<Enchantment> enchantment;
    private final int minLevel;
    private final int maxLevel;

    private ItemEnchantmentCondition(Holder<Enchantment> enchantment, int minLevel, int maxLevel) {
        this.enchantment = enchantment;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    private Holder<Enchantment> enchantment() {
        return enchantment;
    }

    private int minLevel() {
        return minLevel;
    }

    private int maxLevel() {
        return maxLevel;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean test(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        ItemEnchantments enchantments = stack.getEnchantments();
        int level = enchantments.getLevel(enchantment);
        if (level <= 0) {
            return false;
        }
        if (level < minLevel) {
            return false;
        }
        return level <= maxLevel;
    }

    public static ItemEnchantmentCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("enchantment")) {
            Origins.LOGGER.warn("Item enchantment condition '{}' is missing required 'enchantment' field", id);
            return null;
        }

        String rawEnchantment = GsonHelper.getAsString(json, "enchantment");
        ResourceLocation enchantmentId;
        try {
            enchantmentId = ResourceLocation.parse(rawEnchantment);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Item enchantment condition '{}' has invalid enchantment id '{}': {}", id, rawEnchantment, exception.getMessage());
            return null;
        }

        ResourceKey<Enchantment> enchantmentKey = ResourceKey.create(Registries.ENCHANTMENT, enchantmentId);
        Optional<Holder.Reference<Enchantment>> enchantment = ENCHANTMENT_REGISTRY.getHolder(enchantmentKey);
        if (enchantment.isEmpty()) {
            Origins.LOGGER.warn("Item enchantment condition '{}' references unknown enchantment '{}'", id, enchantmentId);
            return null;
        }

        int min = GsonHelper.getAsInt(json, "min", 0);
        int max = json.has("max") ? GsonHelper.getAsInt(json, "max") : Integer.MAX_VALUE;
        if (min < 0) {
            Origins.LOGGER.warn("Item enchantment condition '{}' has negative min level {}", id, min);
            return null;
        }
        if (max < min) {
            Origins.LOGGER.warn("Item enchantment condition '{}' has max level {} smaller than min level {}", id, max, min);
            return null;
        }

        return new ItemEnchantmentCondition(enchantment.get(), min, max);
    }

    public static Codec<ItemEnchantmentCondition> codec() {
        return CODEC;
    }
}
