package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Optional;

/**
 * Datapack condition that compares a living entity attribute value against configured bounds.
 */
public final class AttributeCondition implements Condition<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "attribute");
    private static final Codec<AttributeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(AttributeCondition::attribute),
        Codec.DOUBLE.optionalFieldOf("min", Double.NEGATIVE_INFINITY).forGetter(AttributeCondition::minValue),
        Codec.DOUBLE.optionalFieldOf("max", Double.POSITIVE_INFINITY).forGetter(AttributeCondition::maxValue)
    ).apply(instance, AttributeCondition::new));

    private final Holder<Attribute> attribute;
    private final double minValue;
    private final double maxValue;

    private AttributeCondition(Holder<Attribute> attribute, double minValue, double maxValue) {
        this.attribute = attribute;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    private Holder<Attribute> attribute() {
        return attribute;
    }

    private double minValue() {
        return minValue;
    }

    private double maxValue() {
        return maxValue;
    }

    @Override
    public boolean test(LivingEntity entity) {
        if (entity == null) {
            return false;
        }

        double value = entity.getAttributeValue(attribute);
        return value >= minValue && value <= maxValue;
    }

    public static AttributeCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("attribute")) {
            Origins.LOGGER.warn("Attribute condition '{}' is missing required 'attribute' field", id);
            return null;
        }

        String rawAttribute = GsonHelper.getAsString(json, "attribute");
        ResourceLocation attributeId;
        try {
            attributeId = ResourceLocation.parse(rawAttribute);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Attribute condition '{}' has invalid attribute id '{}': {}", id, rawAttribute, exception.getMessage());
            return null;
        }

        Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeId);
        if (attribute.isEmpty()) {
            Origins.LOGGER.warn("Attribute condition '{}' references unknown attribute '{}'", id, attributeId);
            return null;
        }

        double min = json.has("min") ? GsonHelper.getAsDouble(json, "min") : Double.NEGATIVE_INFINITY;
        double max = json.has("max") ? GsonHelper.getAsDouble(json, "max") : Double.POSITIVE_INFINITY;
        if (max < min) {
            Origins.LOGGER.warn("Attribute condition '{}' has max ({}) smaller than min ({})", id, max, min);
            return null;
        }

        return new AttributeCondition(attribute.get(), min, max);
    }

    public static Codec<AttributeCondition> codec() {
        return CODEC;
    }
}
