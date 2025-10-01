package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

/**
 * Datapack action that grants a configured advancement to the invoking player.
 */
public final class GrantAdvancementAction implements Action<ServerPlayer> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "grant_advancement");
    private static final Codec<GrantAdvancementAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("advancement").forGetter(GrantAdvancementAction::advancementId)
    ).apply(instance, GrantAdvancementAction::new));

    private final ResourceLocation advancementId;
    private boolean missingLogged;

    private GrantAdvancementAction(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    private ResourceLocation advancementId() {
        return advancementId;
    }

    @Override
    public void execute(ServerPlayer player) {
        if (player == null) {
            return;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        AdvancementHolder holder = server.getAdvancements().get(advancementId);
        if (holder == null) {
            if (!missingLogged) {
                Origins.LOGGER.warn("Grant advancement action could not resolve advancement '{}'", advancementId);
                missingLogged = true;
            }
            return;
        }

        PlayerAdvancements advancements = player.getAdvancements();
        AdvancementProgress progress = advancements.getOrStartProgress(holder);
        if (progress.isDone()) {
            return;
        }

        for (String remaining : progress.getRemainingCriteria()) {
            advancements.award(holder, remaining);
        }
    }

    public static GrantAdvancementAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("advancement")) {
            Origins.LOGGER.warn("Grant advancement action '{}' is missing required 'advancement' field", id);
            return null;
        }

        String rawAdvancement = GsonHelper.getAsString(json, "advancement");
        ResourceLocation advancementId;
        try {
            advancementId = ResourceLocation.parse(rawAdvancement);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Grant advancement action '{}' has invalid advancement id '{}': {}", id, rawAdvancement, exception.getMessage());
            return null;
        }

        return new GrantAdvancementAction(advancementId);
    }

    public static Codec<GrantAdvancementAction> codec() {
        return CODEC;
    }
}
