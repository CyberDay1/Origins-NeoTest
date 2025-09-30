package io.github.apace100.origins.registry;

import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OriginsConstants.MODID);

    public static final DeferredItem<Item> ORB_OF_ORIGIN = ITEMS.register("orb_of_origin", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<BlockItem> ORIGIN_STONE = ITEMS.registerSimpleBlockItem(OriginsBlocks.ORIGIN_STONE);

    private OriginsItems() {
    }
}
