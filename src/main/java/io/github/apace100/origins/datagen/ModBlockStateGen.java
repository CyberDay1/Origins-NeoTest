package io.github.apace100.origins.datagen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModBlockStateGen extends BlockStateProvider {
    public ModBlockStateGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Origins.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleStone(ModBlocks.ORIGIN_STONE);
    }

    private void simpleStone(DeferredHolder<Block, Block> holder) {
        String name = holder.getId().getPath();
        ModelFile model = models().cubeAll(name, ResourceLocation.withDefaultNamespace("block/stone"));
        getVariantBuilder(holder.get()).partialState().setModels(new ConfiguredModel(model));
    }
}
