package io.github.origins.registry;

import io.github.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, Origins.MOD_ID);

    private ModItems() {
    }
}
