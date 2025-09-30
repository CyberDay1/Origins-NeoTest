package io.github.apace100.origins.platform.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "origins");

    public static final RegistryObject<Item> ORB_OF_ORIGIN = ITEMS.register("orb_of_origin",
            () -> new Item(new Item.Properties()));
}
