package io.github.apace100.origins.registry;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsConditions {
    public static final ResourceKey<Registry<Codec<?>>> CONDITION_REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(OriginsConstants.MODID, "conditions"));
    public static final DeferredRegister<Codec<?>> CONDITIONS = DeferredRegister.create(CONDITION_REGISTRY, OriginsConstants.MODID);

    private OriginsConditions() {
    }
}
