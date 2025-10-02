package io.github.apace100.origins.power.action.impl;

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
 * Datapack action that sets a player's experience level.
 */
public final class SetXpLevelAction implements Action<Player> {
    private static final int MIN_LEVEL = 0;

    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "set_xp_level");
    private static final Codec<SetXpLevelAction> CODEC = RecordCodecBuilder.<SetXpLevelAction>create(instance -> instance.group(
        Codec.INT.fieldOf("level").forGetter(SetXpLevelAction::level)
    ).apply(instance, level -> new SetXpLevelAction(level))).flatXmap(SetXpLevelAction::validate, SetXpLevelAction::validate);

    private final Optional<ResourceLocation> sourceId;
    private final int level;

    private SetXpLevelAction(int level) {
        this(Optional.empty(), level);
    }

    private SetXpLevelAction(Optional<ResourceLocation> sourceId, int level) {
        this.sourceId = sourceId;
        this.level = level;
    }

    private int level() {
        return level;
    }

    @Override
    public void execute(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Origins.LOGGER.warn(
                "Set xp level action '{}' attempted to run on non-server player {}",
                sourceId.map(ResourceLocation::toString).orElse("<untracked>"), player
            );
            return;
        }

        serverPlayer.setExperienceLevels(level);
        serverPlayer.setExperiencePoints(0);
    }

    public static SetXpLevelAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("level")) {
            Origins.LOGGER.warn("Set xp level action '{}' is missing required 'level' field", id);
            return null;
        }

        int level;
        try {
            level = GsonHelper.getAsInt(json, "level");
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Set xp level action '{}' has invalid 'level': {}", id, exception.getMessage());
            return null;
        }

        SetXpLevelAction action = new SetXpLevelAction(Optional.of(id), level);
        DataResult<SetXpLevelAction> validation = validate(action);
        return validation.resultOrPartial(message ->
            Origins.LOGGER.warn("Set xp level action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<SetXpLevelAction> codec() {
        return CODEC;
    }

    private static DataResult<SetXpLevelAction> validate(SetXpLevelAction action) {
        if (action.level < MIN_LEVEL) {
            return DataResult.error(() -> "level must be at least " + MIN_LEVEL);
        }
        return DataResult.success(action);
    }
}
