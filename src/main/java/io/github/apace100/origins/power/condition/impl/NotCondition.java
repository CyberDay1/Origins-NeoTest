package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

/**
 * Datapack condition that negates a nested condition.
 */
public final class NotCondition implements Condition<Object> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("not");

    private final Condition<Object> condition;

    private NotCondition(Condition<Object> condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(Object context) {
        return !condition.test(context);
    }

    public static NotCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("condition")) {
            Origins.LOGGER.warn("Not condition '{}' is missing required 'condition' object", id);
            return null;
        }

        JsonElement element = json.get("condition");
        if (!element.isJsonObject()) {
            Origins.LOGGER.warn("Not condition '{}' provided non-object 'condition' entry", id);
            return null;
        }

        JsonObject childJson = GsonHelper.convertToJsonObject(element, "condition");
        Optional<Condition<?>> nested = ConditionFactoryUtil.resolveNestedCondition(id, childJson, "condition", "condition");
        if (nested.isEmpty()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Condition<Object> casted = (Condition<Object>) nested.get();
        return new NotCondition(casted);
    }
}
