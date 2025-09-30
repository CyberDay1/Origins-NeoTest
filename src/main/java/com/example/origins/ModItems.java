package com.example.origins;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, OriginsNeoForge.MOD_ID);

    public static final DeferredHolder<Item, Item> EXAMPLE_ITEM =
            ITEMS.register("example_item", () -> new Item(new Properties()));
}
