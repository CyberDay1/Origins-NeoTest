package io.github.apace100.origins.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.registry.ModConditions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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

    public record PlaceholderPower(
        Component name,
        Component description,
        java.util.List<ResourceLocation> actions,
        ResourceLocation condition
    ) {
        private static final Codec<PlaceholderPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(PlaceholderPower::name),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(PlaceholderPower::description),
            ResourceLocation.CODEC.listOf().optionalFieldOf("actions", List.of()).forGetter(PlaceholderPower::actions),
            ResourceLocation.CODEC.optionalFieldOf("condition", ModConditions.ALWAYS_TRUE.getId())
                .forGetter(PlaceholderPower::condition)
        ).apply(instance, PlaceholderPower::new));

        public static Codec<PlaceholderPower> codec() {
            return CODEC;
        }
    }
}
