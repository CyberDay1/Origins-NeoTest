package io.github.origins.core.power;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class Powers {
    private Powers() {}
    public static final String MODID = "origins";

    public static final ResourceKey<Registry<Codec<? extends Power>>> REGISTRY_KEY =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "powers"));

    public static final DeferredRegister<Codec<? extends Power>> POWERS =
        DeferredRegister.create(REGISTRY_KEY, MODID);

    public static final DeferredHolder<Codec<? extends Power>, Codec<Power>> EMPTY =
        POWERS.register("empty", () -> Codec.unit(EmptyPower::new));

    public static final class EmptyPower implements Power { }
}
