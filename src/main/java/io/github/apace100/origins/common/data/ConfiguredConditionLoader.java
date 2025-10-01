package io.github.apace100.origins.common.data;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.Condition;
import io.github.apace100.origins.common.condition.ConfiguredCondition;
import io.github.apace100.origins.common.registry.ConfiguredConditions;
import io.github.apace100.origins.common.registry.ModConditions;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

public final class ConfiguredConditionLoader extends CodecJsonReloadListener<ConfiguredCondition> {
    public ConfiguredConditionLoader() {
        super("origins/conditions");
    }

    @Override
    protected Codec<? extends ConfiguredCondition> resolveCodec(ResourceLocation typeId) {
        Codec<Condition<Void>> codec = findCodec(typeId);
        return codec == null ? null : codec.xmap(condition -> new ConfiguredCondition(typeId, condition), ConfiguredCondition::condition);
    }

    @SuppressWarnings("unchecked")
    private Codec<Condition<Void>> findCodec(ResourceLocation typeId) {
        for (DeferredHolder<Codec<?>, ? extends Codec<?>> holder : ModConditions.CONDITIONS.getEntries()) {
            if (holder.getId().equals(typeId)) {
                return (Codec<Condition<Void>>) holder.get();
            }
        }
        return null;
    }

    @Override
    protected void applyResolved(Map<ResourceLocation, ConfiguredCondition> values) {
        ConfiguredConditions.setAll(values);
    }
}
