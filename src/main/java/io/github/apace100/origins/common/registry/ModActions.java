package io.github.apace100.origins.common.registry;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.Action;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModActions {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Origins.MOD_ID, "actions");
    public static final DeferredRegister<Codec<?>> ACTIONS =
        DeferredRegister.createSimple(Codec.class, REGISTRY_NAME);

    public static final DeferredHolder<Codec<?>, Codec<NoOpAction>> NO_OP =
        ACTIONS.register("noop", NoOpAction::codec);

    private ModActions() {
    }

    public static void register(IEventBus modBus) {
        ACTIONS.register(modBus);
    }

    public record NoOpAction() implements Action<Void> {
        private static final Codec<NoOpAction> CODEC = Codec.unit(new NoOpAction());

        public static Codec<NoOpAction> codec() {
            return CODEC;
        }

        @Override
        public void run(Void context) {
        }
    }
}
