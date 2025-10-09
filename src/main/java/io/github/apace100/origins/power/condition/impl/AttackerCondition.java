package io.github.apace100.origins.power.condition.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.Condition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * Datapack condition that delegates to a nested entity condition for the attacker.
 */
public final class AttackerCondition implements Condition<DamageSource> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("attacker");
    private static final Codec<JsonObject> JSON_OBJECT_CODEC = Codec.PASSTHROUGH.flatXmap(dynamic -> {
        JsonElement element = dynamic.convert(JsonOps.INSTANCE).getValue();
        if (!element.isJsonObject()) {
            return DataResult.error(() -> "Expected attacker condition entity_condition to be an object");
        }
        return DataResult.success(element.getAsJsonObject());
    }, json -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, json)));
    private static final Codec<AttackerCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        JSON_OBJECT_CODEC.fieldOf("entity_condition").forGetter(AttackerCondition::definition)
    ).apply(instance, AttackerCondition::fromCodec));

    private final Condition<Entity> attackerCondition;
    private final JsonObject definition;

    private AttackerCondition(Condition<Entity> attackerCondition, JsonObject definition) {
        this.attackerCondition = attackerCondition;
        this.definition = definition;
    }

    private JsonObject definition() {
        return definition;
    }

    @Override
    public boolean test(DamageSource source) {
        if (source == null || attackerCondition == null) {
            return false;
        }
        Entity attacker = source.getEntity();
        if (attacker == null) {
            return false;
        }
        return attackerCondition.test(attacker);
    }

    public static AttackerCondition fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("entity_condition")) {
            Origins.LOGGER.warn("Attacker condition '{}' is missing required 'entity_condition' object", id);
            return null;
        }

        JsonElement element = json.get("entity_condition");
        if (!element.isJsonObject()) {
            Origins.LOGGER.warn("Attacker condition '{}' provided non-object 'entity_condition'", id);
            return null;
        }

        JsonObject nestedJson = element.getAsJsonObject();
        Optional<Condition<?>> nested = ConditionFactoryUtil.resolveNestedCondition(id, nestedJson, "entity_condition", "entity_condition");
        if (nested.isEmpty()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Condition<Entity> attackerCondition = (Condition<Entity>) nested.get();
        return new AttackerCondition(attackerCondition, nestedJson.deepCopy());
    }

    public static Codec<AttackerCondition> codec() {
        return CODEC;
    }

    private static AttackerCondition fromCodec(JsonObject nested) {
        ResourceLocation tempId = ResourceLocationCompat.mod("attacker/codec");
        Optional<Condition<?>> resolved = ConditionFactoryUtil.resolveNestedCondition(tempId, nested, "entity_condition", "entity_condition");
        if (resolved.isEmpty()) {
            return new AttackerCondition(context -> false, nested.deepCopy());
        }
        @SuppressWarnings("unchecked")
        Condition<Entity> attackerCondition = (Condition<Entity>) resolved.get();
        return new AttackerCondition(attackerCondition, nested.deepCopy());
    }

}
