package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonObject;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import io.github.apace100.origins.power.condition.registry.ConditionRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Locale;
import java.util.Optional;

/**
 * Shared JSON parsing utilities for condition factory implementations.
 */
final class ConditionFactoryUtil {
    private ConditionFactoryUtil() {
    }

    static Optional<Condition<?>> resolveNestedCondition(ResourceLocation parentId, JsonObject json, String description, String pathSuffix) {
        Optional<ResourceLocation> typeId = parseType(parentId, json, description);
        if (typeId.isEmpty()) {
            return Optional.empty();
        }

        ResourceLocation nestedId = nestedId(parentId, pathSuffix);
        Optional<Condition<?>> nested = ConditionRegistry.create(typeId.get(), nestedId, json);
        if (nested.isEmpty()) {
            Origins.LOGGER.warn("Condition '{}' {} has unknown type '{}'", parentId, description, typeId.get());
        }
        return nested;
    }

    static Optional<ResourceLocation> parseType(ResourceLocation parentId, JsonObject json, String description) {
        if (!json.has("type")) {
            Origins.LOGGER.warn("Condition '{}' {} is missing required 'type' field", parentId, description);
            return Optional.empty();
        }

        String rawType = GsonHelper.getAsString(json, "type", "");
        if (rawType.isEmpty()) {
            Origins.LOGGER.warn("Condition '{}' {} specified an empty 'type'", parentId, description);
            return Optional.empty();
        }

        try {
            return Optional.of(ResourceLocation.parse(rawType));
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Condition '{}' {} has invalid type '{}': {}", parentId, description, rawType, exception.getMessage());
            return Optional.empty();
        }
    }

    private static ResourceLocation nestedId(ResourceLocation parentId, String suffix) {
        String normalizedSuffix = normalizeSuffix(suffix);
        String parentPath = parentId.getPath();
        if (parentPath.isEmpty()) {
            return ResourceLocation.fromNamespaceAndPath(parentId.getNamespace(), normalizedSuffix);
        }
        String delimiter = parentPath.endsWith("/") ? "" : "/";
        return ResourceLocation.fromNamespaceAndPath(parentId.getNamespace(), parentPath + delimiter + normalizedSuffix);
    }

    private static String normalizeSuffix(String suffix) {
        String normalized = suffix.toLowerCase(Locale.ROOT).replace(' ', '_');
        if (normalized.startsWith("/")) {
            return normalized.substring(1);
        }
        return normalized;
    }
}
