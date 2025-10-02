package io.github.apace100.origins.power.action.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

final class LootActionContext {
    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    private LootActionContext() {
    }

    static void withContext(ResourceLocation lootTable, List<ItemStack> loot, Runnable runnable) {
        CONTEXT.set(new Context(lootTable, loot));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    static Optional<Context> current() {
        return Optional.ofNullable(CONTEXT.get());
    }

    record Context(ResourceLocation table, List<ItemStack> loot) {
    }
}
