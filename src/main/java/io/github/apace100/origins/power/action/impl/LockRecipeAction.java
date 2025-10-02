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
 * Datapack action that removes a specific recipe from the invoking player's recipe book.
 */
public final class LockRecipeAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "lock_recipe");
    private static final Codec<LockRecipeAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("recipe").forGetter(LockRecipeAction::recipe)
    ).apply(instance, LockRecipeAction::new));

    private final ResourceLocation recipe;

    private LockRecipeAction(ResourceLocation recipe) {
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
            Origins.LOGGER.warn("Lock recipe action references unknown recipe '{}'", recipe);
            return;
        }

        serverPlayer.resetRecipes(List.of(recipeHolder.get()));
    }

    public static LockRecipeAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("recipe")) {
            Origins.LOGGER.warn("Lock recipe action '{}' is missing required 'recipe' field", id);
            return null;
        }

        try {
            return new LockRecipeAction(ResourceLocation.parse(GsonHelper.getAsString(json, "recipe")));
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Lock recipe action '{}' has invalid recipe id '{}': {}", id, json.get("recipe"), exception.getMessage());
            return null;
        }
    }

    public static Codec<LockRecipeAction> codec() {
        return CODEC;
    }
}
