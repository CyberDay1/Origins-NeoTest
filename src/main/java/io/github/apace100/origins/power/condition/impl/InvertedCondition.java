package io.github.apace100.origins.power.condition.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

/**
 * Datapack condition that inverts the result of another condition.
 */
public final class InvertedCondition implements Condition<Object> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "inverted");

    private final Condition<Object> condition;

    private InvertedCondition(Condition<Object> condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(Object context) {
        return !condition.test(context);
    }

    public static InvertedCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("condition")) {
            Origins.LOGGER.warn("Inverted condition '{}' is missing required 'condition' object", id);
            return null;
        }

        JsonElement element = json.get("condition");
        if (!element.isJsonObject()) {
            Origins.LOGGER.warn("Inverted condition '{}' provided non-object 'condition' entry", id);
            return null;
        }

        JsonObject childJson = GsonHelper.convertToJsonObject(element, "condition");
        Optional<Condition<?>> nested = ConditionFactoryUtil.resolveNestedCondition(id, childJson, "condition", "condition");
        if (nested.isEmpty()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Condition<Object> casted = (Condition<Object>) nested.get();
        return new InvertedCondition(casted);
    }
}
