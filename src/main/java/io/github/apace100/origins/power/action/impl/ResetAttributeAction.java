package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.Optional;

/**
 * Datapack action that removes all modifiers from a living entity attribute.
 */
public final class ResetAttributeAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "reset_attribute");
    private static final Codec<ResetAttributeAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(ResetAttributeAction::attribute)
    ).apply(instance, ResetAttributeAction::new));

    private final Holder<Attribute> attribute;

    private ResetAttributeAction(Holder<Attribute> attribute) {
        this.attribute = attribute;
    }

    private Holder<Attribute> attribute() {
        return attribute;
    }

    @Override
    public void execute(LivingEntity entity) {
        if (entity == null) {
            return;
        }

        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        instance.removeModifiers();
    }

    public static ResetAttributeAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("attribute")) {
            Origins.LOGGER.warn("Reset attribute action '{}' is missing required 'attribute' field", id);
            return null;
        }

        String rawAttribute = GsonHelper.getAsString(json, "attribute");
        ResourceLocation attributeId;
        try {
            attributeId = ResourceLocation.parse(rawAttribute);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Reset attribute action '{}' has invalid attribute id '{}': {}", id, rawAttribute, exception.getMessage());
            return null;
        }

        Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeId);
        if (attribute.isEmpty()) {
            Origins.LOGGER.warn("Reset attribute action '{}' references unknown attribute '{}'", id, attributeId);
            return null;
        }

        return new ResetAttributeAction(attribute.get());
    }

    public static Codec<ResetAttributeAction> codec() {
        return CODEC;
    }
}
