package io.github.apace100.origins.platform.neoforge.init;

import io.github.apace100.origins.OriginsNeoForge;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(Registries.ITEM, OriginsNeoForge.MOD_ID);

    public static final DeferredHolder<Item, Item> PLACEHOLDER =
            REGISTER.register("placeholder", () -> new Item(new Item.Properties()));

    private ModItems() {
    }
}
