package io.github.apace100.origins.datagen.provider;

import io.github.apace100.origins.registry.OriginsBlocks;
import io.github.apace100.origins.registry.OriginsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class OriginsRecipeProvider extends RecipeProvider {
    public OriginsRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, OriginsItems.ORB_OF_ORIGIN.get())
            .requires(Items.AMETHYST_SHARD)
            .requires(Items.ENDER_PEARL)
            .requires(Items.BOOK)
            .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, OriginsBlocks.ORIGIN_STONE.get(), 4)
            .define('#', Items.AMETHYST_BLOCK)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_amethyst", has(Items.AMETHYST_BLOCK))
            .save(output);
    }
}
