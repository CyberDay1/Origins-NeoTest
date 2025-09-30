package io.github.apace100.origins.common.registry;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.Condition;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModConditions {
    public static final ResourceLocation REGISTRY_NAME = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "conditions");
    public static final DeferredRegister<Codec<?>> CONDITIONS =
        DeferredRegister.create(REGISTRY_NAME, Origins.MOD_ID);

    public static final DeferredHolder<Codec<?>, Codec<AlwaysTrueCondition>> ALWAYS_TRUE =
        CONDITIONS.register("always_true", AlwaysTrueCondition::codec);

    private ModConditions() {
    }

    public static void register(IEventBus modBus) {
        CONDITIONS.register(modBus);
    }

    public record AlwaysTrueCondition() implements Condition<Void> {
        private static final Codec<AlwaysTrueCondition> CODEC = Codec.unit(new AlwaysTrueCondition());

        public static Codec<AlwaysTrueCondition> codec() {
            return CODEC;
        }

        @Override
        public boolean test(Void context) {
            return true;
        }
    }
}
