package io.github.apace100.origins.platform.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "origins");

    public static final DeferredHolder<Item, Item> ORB_OF_ORIGIN = ITEMS.register(
        "orb_of_origin",
        () -> new Item(new Item.Properties())
    );
}
