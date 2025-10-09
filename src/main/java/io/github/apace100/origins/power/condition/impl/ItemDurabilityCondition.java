package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

/**
 * Datapack condition that checks whether an item stack's durability ratio falls within a range.
 */
public final class ItemDurabilityCondition implements Condition<ItemStack> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("item_durability");
    private static final Codec<ItemDurabilityCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.optionalFieldOf("min", 0.0D).forGetter(ItemDurabilityCondition::minRatio),
        Codec.DOUBLE.optionalFieldOf("max", 1.0D).forGetter(ItemDurabilityCondition::maxRatio)
    ).apply(instance, ItemDurabilityCondition::new));

    private final double minRatio;
    private final double maxRatio;

    private ItemDurabilityCondition(double minRatio, double maxRatio) {
        this.minRatio = minRatio;
        this.maxRatio = maxRatio;
    }

    private double minRatio() {
        return minRatio;
    }

    private double maxRatio() {
        return maxRatio;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.isDamageableItem()) {
            return false;
        }

        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) {
            return false;
        }

        double ratio = (double) stack.getDamageValue() / maxDamage;
        return ratio >= minRatio && ratio <= maxRatio;
    }

    public static ItemDurabilityCondition fromJson(ResourceLocation id, JsonObject json) {
        double min = GsonHelper.getAsDouble(json, "min", 0.0D);
        double max = GsonHelper.getAsDouble(json, "max", 1.0D);
        if (min < 0.0D || min > 1.0D) {
            Origins.LOGGER.warn("Item durability condition '{}' has min ratio {} outside of [0.0, 1.0]", id, min);
            return null;
        }
        if (max < 0.0D || max > 1.0D) {
            Origins.LOGGER.warn("Item durability condition '{}' has max ratio {} outside of [0.0, 1.0]", id, max);
            return null;
        }
        if (max < min) {
            Origins.LOGGER.warn("Item durability condition '{}' has max ratio {} smaller than min ratio {}", id, max, min);
            return null;
        }

        return new ItemDurabilityCondition(min, max);
    }

    public static Codec<ItemDurabilityCondition> codec() {
        return CODEC;
    }
}
