package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;

/**
 * Datapack action that unlocks a specific crafting recipe for the invoking player.
 */
public final class UnlockRecipeAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "unlock_recipe");
    private static final Codec<UnlockRecipeAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("recipe").forGetter(UnlockRecipeAction::recipe)
    ).apply(instance, UnlockRecipeAction::new));

    private final ResourceLocation recipe;

    private UnlockRecipeAction(ResourceLocation recipe) {
        this.recipe = recipe;
    }

    private ResourceLocation recipe() {
        return recipe;
    }

    @Override
    public void execute(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Optional<RecipeHolder<?>> recipeHolder = serverPlayer.server.getRecipeManager().byKey(recipe);
        if (recipeHolder.isEmpty()) {
            Origins.LOGGER.warn("Unlock recipe action references unknown recipe '{}'", recipe);
            return;
        }

        serverPlayer.awardRecipes(List.of(recipeHolder.get()));
    }

    public static UnlockRecipeAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("recipe")) {
            Origins.LOGGER.warn("Unlock recipe action '{}' is missing required 'recipe' field", id);
            return null;
        }

        try {
            return new UnlockRecipeAction(ResourceLocation.parse(GsonHelper.getAsString(json, "recipe")));
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Unlock recipe action '{}' has invalid recipe id '{}': {}", id, json.get("recipe"), exception.getMessage());
            return null;
        }
    }

    public static Codec<UnlockRecipeAction> codec() {
        return CODEC;
    }
}
