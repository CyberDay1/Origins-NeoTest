package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.registry.ModBlocks;
import io.github.apace100.origins.common.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModItemModelGen extends ItemModelProvider {
    public ModItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Origins.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        blockItem(ModBlocks.ORIGIN_STONE.getId().getPath());
        generatedItem(ModItems.ORB_OF_ORIGIN.getId().getPath(), ResourceLocation.withDefaultNamespace("item/ender_pearl"));
    }

    private void blockItem(String name) {
        withExistingParent(name, Origins.MOD_ID + ":block/" + name);
    }

    private void generatedItem(String name, ResourceLocation texture) {
        getBuilder(name)
            .parent(getExistingFile(mcLoc("item/generated")))
            .texture("layer0", texture.toString());
    }
}
