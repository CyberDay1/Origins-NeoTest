package io.github.origins.registry;

import io.github.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, Origins.MOD_ID);
    public static final RegistryObject<Item> ORB_OF_ORIGIN = REGISTRY.register(
        "orb_of_origin",
        () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> ORIGIN_STONE = REGISTRY.register(
        "origin_stone",
        () -> new BlockItem(ModBlocks.ORIGIN_STONE.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
