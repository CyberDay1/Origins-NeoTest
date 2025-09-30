package io.github.origins.core.action;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class Actions {
    private Actions() {}
    public static final String MODID = "origins";

    public static final ResourceKey<Registry<Codec<? extends Action>>> REGISTRY_KEY =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "actions"));

    public static final DeferredRegister<Codec<? extends Action>> ACTIONS =
        DeferredRegister.create(REGISTRY_KEY, MODID);

    // Example stub registration (to be replaced with real actions later):
    public static final DeferredHolder<Codec<? extends Action>, Codec<Action>> NO_OP =
        ACTIONS.register("no_op", () -> Codec.unit(NoOpAction::new));

    /** Placeholder action for wiring tests. */
    public static final class NoOpAction implements Action { }
}
