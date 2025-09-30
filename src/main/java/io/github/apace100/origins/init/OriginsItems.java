package io.github.apace100.origins.init;

import io.github.apace100.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Origins.MOD_ID);

    public static final DeferredHolder<Item, Item> ORIGIN_STONE_ITEM = ITEMS.register(
        "origin_stone",
        () -> new BlockItem(OriginsBlocks.ORIGIN_STONE.get(), new Item.Properties())
    );

    public static final DeferredHolder<Item, Item> ORB_OF_ORIGIN = ITEMS.register(
        "orb_of_origin",
        () -> new Item(new Item.Properties().stacksTo(1))
    );

    private OriginsItems() {
    }
}
