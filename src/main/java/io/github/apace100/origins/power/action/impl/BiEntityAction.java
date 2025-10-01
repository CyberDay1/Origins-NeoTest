package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * Scaffold implementation for bi-entity datapack actions.
 */
public final class BiEntityAction implements Action<BiEntityAction.BiEntityContext> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "bi_entity");
    private static final Codec<BiEntityAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("relationship").forGetter(BiEntityAction::relationship)
    ).apply(instance, relationship -> new BiEntityAction(relationship.map(Relationship::fromString))));

    private final Optional<Relationship> relationship;

    private BiEntityAction(Optional<Relationship> relationship) {
        this.relationship = relationship;
    }

    public Optional<String> relationship() {
        return relationship.map(Relationship::serializedName);
    }

    @Override
    public void execute(BiEntityContext context) {
        // TODO: Implement Fabric parity behaviour for bi-entity actions (attacker/target interactions).
    }

    public static BiEntityAction fromJson(ResourceLocation id, JsonObject json) {
        Optional<Relationship> parsed = Optional.empty();
        if (json.has("relationship")) {
            String raw = GsonHelper.getAsString(json, "relationship");
            Relationship relation = Relationship.fromString(raw);
            if (relation == null) {
                Origins.LOGGER.warn("Unknown relationship '{}' for bi-entity action '{}'", raw, id);
                return null;
            }
            parsed = Optional.of(relation);
        }
        return new BiEntityAction(parsed);
    }

    public static Codec<BiEntityAction> codec() {
        return CODEC;
    }

    /**
     * Bundles the attacker and target entities for bi-entity actions.
     */
    public record BiEntityContext(Entity actor, Entity target) {
    }

    /**
     * Enumerates the common relationships found in Fabric datapacks.
     */
    public enum Relationship {
        ATTACKER,
        TARGET,
        ANY;

        public static Relationship fromString(String value) {
            try {
                return Relationship.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }

        public String serializedName() {
            return name().toLowerCase();
        }
    }
}
