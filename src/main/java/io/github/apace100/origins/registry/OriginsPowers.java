package io.github.apace100.origins.registry;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.util.OriginsConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsPowers {
    public static final ResourceKey<Registry<Codec<?>>> POWER_REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(OriginsConstants.MODID, "powers"));
    public static final DeferredRegister<Codec<?>> POWERS = DeferredRegister.create(POWER_REGISTRY, OriginsConstants.MODID);

    private OriginsPowers() {
    }
}
