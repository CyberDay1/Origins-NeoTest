package io.github.apace100.origins.common.registry;

import io.github.apace100.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTER =
        DeferredRegister.create(Registries.ITEM, Origins.MOD_ID);

    public static final DeferredHolder<Item, Item> ORIGIN_STONE_ITEM = REGISTER.register(
        "origin_stone",
        () -> new BlockItem(ModBlocks.ORIGIN_STONE.get(), new Item.Properties())
    );

    public static final DeferredHolder<Item, Item> ORB_OF_ORIGIN = REGISTER.register(
        "orb_of_origin",
        () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
    );

    private ModItems() {
    }
}
