package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Datapack action that adds experience levels to a player.
 */
public final class AddXpLevelAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("add_xp_level");
    private static final Codec<AddXpLevelAction> CODEC = RecordCodecBuilder.<AddXpLevelAction>create(instance -> instance.group(
        Codec.INT.fieldOf("levels").forGetter(AddXpLevelAction::levels)
    ).apply(instance, levels -> new AddXpLevelAction(levels))).flatXmap(AddXpLevelAction::validate, AddXpLevelAction::validate);

    private final Optional<ResourceLocation> sourceId;
    private final int levels;

    private AddXpLevelAction(int levels) {
        this(Optional.empty(), levels);
    }

    private AddXpLevelAction(Optional<ResourceLocation> sourceId, int levels) {
        this.sourceId = sourceId;
        this.levels = levels;
    }

    private int levels() {
        return levels;
    }

    @Override
    public void execute(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Origins.LOGGER.warn(
                "Add xp level action '{}' attempted to run on non-server player {}",
                sourceId.map(ResourceLocation::toString).orElse("<untracked>"), player
            );
            return;
        }

        if (levels == 0) {
            return;
        }
        serverPlayer.giveExperienceLevels(levels);
    }

    public static AddXpLevelAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("levels")) {
            Origins.LOGGER.warn("Add xp level action '{}' is missing required 'levels' field", id);
            return null;
        }

        int levels;
        try {
            levels = GsonHelper.getAsInt(json, "levels");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Add xp level action '{}' has invalid 'levels': {}", id, exception.getMessage());
            return null;
        }

        AddXpLevelAction action = new AddXpLevelAction(Optional.of(id), levels);
        DataResult<AddXpLevelAction> validation = validate(action);
        return validation.resultOrPartial(message ->
            Origins.LOGGER.warn("Add xp level action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<AddXpLevelAction> codec() {
        return CODEC;
    }

    private static DataResult<AddXpLevelAction> validate(AddXpLevelAction action) {
        if (action.levels == 0) {
            return DataResult.error(() -> "levels must be non-zero");
        }
        return DataResult.success(action);
    }
}
