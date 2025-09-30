package io.github.apace100.origins.registry;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsActions {
    public static final ResourceKey<Registry<Codec<?>>> ACTION_REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(OriginsConstants.MODID, "actions"));
    public static final DeferredRegister<Codec<?>> ACTIONS = DeferredRegister.create(ACTION_REGISTRY, OriginsConstants.MODID);

    private OriginsActions() {
    }
}
