package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Datapack action that modifies a living entity attribute using a temporary modifier.
 */
public final class ModifyAttributeAction implements Action<LivingEntity> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "modify_attribute");
    private static final Map<String, AttributeModifier.Operation> OPERATION_LOOKUP = Map.ofEntries(
        Map.entry("add", AttributeModifier.Operation.ADD_VALUE),
        Map.entry("add_value", AttributeModifier.Operation.ADD_VALUE),
        Map.entry("multiply_base", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
        Map.entry("add_multiplied_base", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
        Map.entry("multiply_total", AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
        Map.entry("add_multiplied_total", AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    private static final Map<AttributeModifier.Operation, String> OPERATION_NAMES = Map.of(
        AttributeModifier.Operation.ADD_VALUE, "add_value",
        AttributeModifier.Operation.ADD_MULTIPLIED_BASE, "add_multiplied_base",
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, "add_multiplied_total"
    );
    private static final Codec<AttributeModifier.Operation> OPERATION_CODEC = Codec.STRING.flatXmap(
        value -> {
            AttributeModifier.Operation operation = OPERATION_LOOKUP.get(value.toLowerCase(Locale.ROOT));
            if (operation == null) {
                return DataResult.error(() -> "Unknown attribute operation: " + value);
            }
            return DataResult.success(operation);
        },
        operation -> DataResult.success(OPERATION_NAMES.getOrDefault(operation, operation.getSerializedName()))
    );
    private static final MapCodec<Optional<ResourceLocation>> MODIFIER_ID_CODEC = Codec.STRING.optionalFieldOf("uuid")
        .flatXmap(ModifyAttributeAction::decodeModifierId, ModifyAttributeAction::encodeModifierId);
    private static final Codec<ModifyAttributeAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(ModifyAttributeAction::attribute),
        OPERATION_CODEC.fieldOf("operation").forGetter(ModifyAttributeAction::operation),
        Codec.DOUBLE.fieldOf("value").forGetter(ModifyAttributeAction::value),
        MODIFIER_ID_CODEC.forGetter(ModifyAttributeAction::modifierId)
    ).apply(instance, ModifyAttributeAction::new));

    private final Holder<Attribute> attribute;
    private final AttributeModifier.Operation operation;
    private final double value;
    private final Optional<ResourceLocation> modifierId;
    private final ResourceLocation fallbackModifierId;

    private ModifyAttributeAction(Holder<Attribute> attribute, AttributeModifier.Operation operation, double value, Optional<ResourceLocation> modifierId) {
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
        this.modifierId = modifierId;
        this.fallbackModifierId = modifierId.orElseGet(() -> createFallbackId(attribute, operation, value));
    }

    private Holder<Attribute> attribute() {
        return attribute;
    }

    private AttributeModifier.Operation operation() {
        return operation;
    }

    private double value() {
        return value;
    }

    private Optional<ResourceLocation> modifierId() {
        return modifierId;
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

        ResourceLocation modifierKey = modifierId.orElse(fallbackModifierId);
        AttributeModifier modifier = new AttributeModifier(modifierKey, value, operation);
        instance.addOrUpdateTransientModifier(modifier);
    }

    public static ModifyAttributeAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("attribute")) {
            Origins.LOGGER.warn("Modify attribute action '{}' is missing required 'attribute' field", id);
            return null;
        }

        String rawAttribute = GsonHelper.getAsString(json, "attribute");
        ResourceLocation attributeId;
        try {
            attributeId = ResourceLocation.parse(rawAttribute);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Modify attribute action '{}' has invalid attribute id '{}': {}", id, rawAttribute, exception.getMessage());
            return null;
        }

        Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeId);
        if (attribute.isEmpty()) {
            Origins.LOGGER.warn("Modify attribute action '{}' references unknown attribute '{}'", id, attributeId);
            return null;
        }

        if (!json.has("operation")) {
            Origins.LOGGER.warn("Modify attribute action '{}' is missing required 'operation' field", id);
            return null;
        }

        String rawOperation = GsonHelper.getAsString(json, "operation");
        AttributeModifier.Operation operation = OPERATION_LOOKUP.get(rawOperation.toLowerCase(Locale.ROOT));
        if (operation == null) {
            Origins.LOGGER.warn("Modify attribute action '{}' has unknown operation '{}'", id, rawOperation);
            return null;
        }

        double value;
        try {
            value = GsonHelper.getAsDouble(json, "value");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Modify attribute action '{}' has invalid 'value': {}", id, exception.getMessage());
            return null;
        }

        Optional<ResourceLocation> modifierId = Optional.empty();
        if (json.has("uuid")) {
            String rawUuid = GsonHelper.getAsString(json, "uuid");
            Optional<ResourceLocation> parsed = parseModifierId(rawUuid);
            if (parsed.isEmpty()) {
                Origins.LOGGER.warn("Modify attribute action '{}' has invalid uuid '{}': unable to derive modifier id", id, rawUuid);
                return null;
            }
            modifierId = parsed;
        }

        return new ModifyAttributeAction(attribute.get(), operation, value, modifierId);
    }

    public static Codec<ModifyAttributeAction> codec() {
        return CODEC;
    }

    private static DataResult<Optional<ResourceLocation>> decodeModifierId(Optional<String> input) {
        if (input.isEmpty()) {
            return DataResult.success(Optional.empty());
        }
        Optional<ResourceLocation> parsed = parseModifierId(input.get());
        if (parsed.isEmpty()) {
            return DataResult.error(() -> "Invalid attribute modifier id: " + input.get());
        }
        return DataResult.success(parsed);
    }

    private static DataResult<Optional<String>> encodeModifierId(Optional<ResourceLocation> input) {
        return DataResult.success(input.map(ResourceLocation::toString));
    }

    private static Optional<ResourceLocation> parseModifierId(String raw) {
        ResourceLocation direct = ResourceLocation.tryParse(raw);
        if (direct != null) {
            return Optional.of(direct);
        }

        try {
            UUID uuid = UUID.fromString(raw);
            return Optional.of(Origins.id(uuid.toString()));
        } catch (IllegalArgumentException ignored) {
        }

        String sanitized = sanitizePath(raw);
        ResourceLocation derived = ResourceLocation.tryParse(Origins.MOD_ID + ":" + sanitized);
        return Optional.ofNullable(derived);
    }

    private static String sanitizePath(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder(lower.length());
        for (int index = 0; index < lower.length(); index++) {
            char character = lower.charAt(index);
            if (character >= 'a' && character <= 'z'
                || character >= '0' && character <= '9'
                || character == '/' || character == '.' || character == '-' || character == '_') {
                builder.append(character);
            } else {
                builder.append('_');
            }
        }
        if (builder.isEmpty()) {
            return "attribute_modifier";
        }
        return builder.toString();
    }

    private static ResourceLocation createFallbackId(Holder<Attribute> attribute, AttributeModifier.Operation operation, double value) {
        String attributePath = attribute.unwrapKey().map(ResourceKey::location).map(ResourceLocation::getPath).orElse("attribute");
        String operationName = OPERATION_NAMES.getOrDefault(operation, operation.getSerializedName());
        String amountKey = Long.toHexString(Double.doubleToLongBits(value));
        return Origins.id("datapack_attribute/" + attributePath + "/" + operationName + "/" + amountKey);
    }
}
