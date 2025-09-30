package io.github.apace100.origins.datagen;

import io.github.apace100.origins.common.registry.ModBlocks;
import io.github.apace100.origins.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ORIGIN_STONE.get(), 4)
            .define('#', Items.STONE)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_stone", has(Items.STONE))
            .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORB_OF_ORIGIN.get())
            .requires(Items.AMETHYST_SHARD)
            .requires(Items.ENDER_EYE)
            .requires(Items.GHAST_TEAR)
            .unlockedBy("has_ghast_tear", has(Items.GHAST_TEAR))
            .save(output);
    }
}
