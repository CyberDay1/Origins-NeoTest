package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Datapack condition that succeeds when exactly one nested condition passes.
 */
public final class XorCondition implements Condition<Object> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "xor");

    private final List<Condition<Object>> conditions;

    private XorCondition(List<Condition<Object>> conditions) {
        this.conditions = List.copyOf(conditions);
    }

    @Override
    public boolean test(Object context) {
        boolean matched = false;
        for (Condition<Object> condition : conditions) {
            if (condition.test(context)) {
                if (matched) {
                    return false;
                }
                matched = true;
            }
        }
        return matched;
    }

    public static XorCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("conditions")) {
            Origins.LOGGER.warn("Xor condition '{}' is missing required 'conditions' array", id);
            return null;
        }

        JsonElement element = json.get("conditions");
        if (!element.isJsonArray()) {
            Origins.LOGGER.warn("Xor condition '{}' provided non-array 'conditions' entry", id);
            return null;
        }

        JsonArray array = element.getAsJsonArray();
        if (array.size() < 2) {
            Origins.LOGGER.warn("Xor condition '{}' requires at least two child conditions", id);
            return null;
        }

        List<Condition<Object>> children = new ArrayList<>();
        boolean failed = false;
        for (int i = 0; i < array.size(); i++) {
            JsonElement childElement = array.get(i);
            if (!childElement.isJsonObject()) {
                Origins.LOGGER.warn("Xor condition '{}' has non-object child at conditions[{}]", id, i);
                failed = true;
                continue;
            }

            JsonObject childJson = childElement.getAsJsonObject();
            Optional<Condition<?>> nested = ConditionFactoryUtil.resolveNestedCondition(id, childJson,
                "conditions[" + i + "]", "conditions/" + i);
            if (nested.isEmpty()) {
                failed = true;
                continue;
            }

            @SuppressWarnings("unchecked")
            Condition<Object> casted = (Condition<Object>) nested.get();
            children.add(casted);
        }

        if (failed || children.size() < 2) {
            return null;
        }

        return new XorCondition(children);
    }
}
