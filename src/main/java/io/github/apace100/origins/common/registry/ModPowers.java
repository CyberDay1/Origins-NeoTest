package io.github.apace100.origins.common.registry;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModPowers {
    public static final ResourceLocation REGISTRY_NAME = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "powers");
    public static final DeferredRegister<Codec<?>> POWERS =
        DeferredRegister.create(REGISTRY_NAME, Origins.MOD_ID);

    public static final DeferredHolder<Codec<?>, Codec<PlaceholderPower>> PLACEHOLDER =
        POWERS.register("placeholder", PlaceholderPower::codec);

    private ModPowers() {
    }

    public static void register(IEventBus modBus) {
        POWERS.register(modBus);
    }

    public record PlaceholderPower() {
        private static final Codec<PlaceholderPower> CODEC = Codec.unit(new PlaceholderPower());

        public static Codec<PlaceholderPower> codec() {
            return CODEC;
        }
    }
}
