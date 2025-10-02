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
 * Datapack condition that succeeds when any nested condition passes.
 */
public final class OrCondition implements Condition<Object> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "or");

    private final List<Condition<Object>> conditions;

    private OrCondition(List<Condition<Object>> conditions) {
        this.conditions = List.copyOf(conditions);
    }

    @Override
    public boolean test(Object context) {
        for (Condition<Object> condition : conditions) {
            if (condition.test(context)) {
                return true;
            }
        }
        return false;
    }

    public static OrCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("conditions")) {
            Origins.LOGGER.warn("Or condition '{}' is missing required 'conditions' array", id);
            return null;
        }

        JsonElement element = json.get("conditions");
        if (!element.isJsonArray()) {
            Origins.LOGGER.warn("Or condition '{}' provided non-array 'conditions' entry", id);
            return null;
        }

        JsonArray array = element.getAsJsonArray();
        if (array.isEmpty()) {
            Origins.LOGGER.warn("Or condition '{}' provided an empty 'conditions' array", id);
            return null;
        }

        List<Condition<Object>> children = new ArrayList<>();
        boolean failed = false;
        for (int i = 0; i < array.size(); i++) {
            JsonElement childElement = array.get(i);
            if (!childElement.isJsonObject()) {
                Origins.LOGGER.warn("Or condition '{}' has non-object child at conditions[{}]", id, i);
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

        if (failed || children.isEmpty()) {
            return null;
        }

        return new OrCondition(children);
    }
}
