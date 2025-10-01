package io.github.apace100.origins.common.data;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.Action;
import io.github.apace100.origins.common.action.ConfiguredAction;
import io.github.apace100.origins.common.registry.ConfiguredActions;
import io.github.apace100.origins.common.registry.ModActions;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

public final class ConfiguredActionLoader extends CodecJsonReloadListener<ConfiguredAction> {
    public ConfiguredActionLoader() {
        super("origins/actions");
    }

    @Override
    protected Codec<? extends ConfiguredAction> resolveCodec(ResourceLocation typeId) {
        Codec<Action<Void>> codec = findCodec(typeId);
        return codec == null ? null : codec.xmap(action -> new ConfiguredAction(typeId, action), ConfiguredAction::action);
    }

    @SuppressWarnings("unchecked")
    private Codec<Action<Void>> findCodec(ResourceLocation typeId) {
        for (DeferredHolder<Codec<?>, Codec<?>> holder : ModActions.ACTIONS.getEntries()) {
            if (holder.getId().equals(typeId)) {
                return (Codec<Action<Void>>) holder.get();
            }
        }
        return null;
    }

    @Override
    protected void applyResolved(Map<ResourceLocation, ConfiguredAction> values) {
        ConfiguredActions.setAll(values);
    }
}
