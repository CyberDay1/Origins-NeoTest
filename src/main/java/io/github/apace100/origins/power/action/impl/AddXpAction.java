package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Datapack action that grants raw experience points to a player.
 */
public final class AddXpAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("add_xp");
    private static final Codec<AddXpAction> CODEC = RecordCodecBuilder.<AddXpAction>create(instance -> instance.group(
        Codec.INT.fieldOf("amount").forGetter(AddXpAction::amount)
    ).apply(instance, amount -> new AddXpAction(amount))).flatXmap(AddXpAction::validate, AddXpAction::validate);

    private final Optional<ResourceLocation> sourceId;
    private final int amount;

    private AddXpAction(int amount) {
        this(Optional.empty(), amount);
    }

    private AddXpAction(Optional<ResourceLocation> sourceId, int amount) {
        this.sourceId = sourceId;
        this.amount = amount;
    }

    private int amount() {
        return amount;
    }

    @Override
    public void execute(Player player) {
        if (player == null || amount == 0) {
            return;
        }
        player.giveExperiencePoints(amount);
    }

    public static AddXpAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("amount")) {
            Origins.LOGGER.warn("Add xp action '{}' is missing required 'amount' field", id);
            return null;
        }

        int amount;
        try {
            amount = GsonHelper.getAsInt(json, "amount");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Add xp action '{}' has invalid 'amount': {}", id, exception.getMessage());
            return null;
        }

        AddXpAction action = new AddXpAction(Optional.of(id), amount);
        DataResult<AddXpAction> validation = validate(action);
        return validation.resultOrPartial(message ->
            Origins.LOGGER.warn("Add xp action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<AddXpAction> codec() {
        return CODEC;
    }

    private static DataResult<AddXpAction> validate(AddXpAction action) {
        if (action.amount == 0) {
            return DataResult.error(() -> "amount must be non-zero");
        }
        return DataResult.success(action);
    }
}
