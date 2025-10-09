package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * Datapack condition that validates whether any passenger on an entity satisfies a nested condition.
 */
public final class PassengerCondition implements Condition<Entity> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("passenger");

    private final Condition<Entity> passengerCondition;

    private PassengerCondition(Condition<Entity> passengerCondition) {
        this.passengerCondition = passengerCondition;
    }

    @Override
    public boolean test(Entity entity) {
        if (entity == null || passengerCondition == null) {
            return false;
        }
        for (Entity passenger : entity.getPassengers()) {
            if (passengerCondition.test(passenger)) {
                return true;
            }
        }
        return false;
    }

    public static PassengerCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("passenger")) {
            Origins.LOGGER.warn("Passenger condition '{}' is missing required 'passenger' object", id);
            return null;
        }

        JsonElement element = json.get("passenger");
        if (!element.isJsonObject()) {
            Origins.LOGGER.warn("Passenger condition '{}' provided non-object 'passenger' entry", id);
            return null;
        }

        JsonObject passengerJson = GsonHelper.convertToJsonObject(element, "passenger");
        Optional<Condition<?>> parsed = ConditionFactoryUtil.resolveNestedCondition(id, passengerJson, "passenger", "passenger");
        if (parsed.isEmpty()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Condition<Entity> passengerCondition = (Condition<Entity>) parsed.get();
        return new PassengerCondition(passengerCondition);
    }
}
