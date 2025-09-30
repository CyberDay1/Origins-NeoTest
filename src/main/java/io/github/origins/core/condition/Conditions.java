package io.github.origins.core.condition;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class Conditions {
    private Conditions() {}
    public static final String MODID = "origins";

    public static final ResourceKey<Registry<Codec<? extends Condition>>> REGISTRY_KEY =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "conditions"));

    public static final DeferredRegister<Codec<? extends Condition>> CONDITIONS =
        DeferredRegister.create(REGISTRY_KEY, MODID);

    public static final DeferredHolder<Codec<? extends Condition>, Codec<Condition>> ALWAYS_TRUE =
        CONDITIONS.register("always_true", () -> Codec.unit(AlwaysTrue::new));

    public static final class AlwaysTrue implements Condition { }
}
