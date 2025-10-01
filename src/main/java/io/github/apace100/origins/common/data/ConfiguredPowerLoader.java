package io.github.apace100.origins.common.data;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.power.ConfiguredPower;
import io.github.apace100.origins.common.registry.ConfiguredActions;
import io.github.apace100.origins.common.registry.ConfiguredConditions;
import io.github.apace100.origins.common.registry.ConfiguredPowers;
import io.github.apace100.origins.common.registry.ModPowers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ConfiguredPowerLoader extends CodecJsonReloadListener<ConfiguredPower> {
    public ConfiguredPowerLoader() {
        super("origins/powers");
    }

    @Override
    protected Codec<? extends ConfiguredPower> resolveCodec(ResourceLocation typeId) {
        Codec<ModPowers.PlaceholderPower> codec = findCodec(typeId);
        if (codec == null) {
            return null;
        }

        return codec.xmap(power -> new ConfiguredPower(typeId, power.name(), power.description(), power.actions(), power.condition()),
            configured -> new ModPowers.PlaceholderPower(
                configured.name(),
                configured.description(),
                List.copyOf(configured.actions()),
                configured.condition()
            ));
    }

    @SuppressWarnings("unchecked")
    private Codec<ModPowers.PlaceholderPower> findCodec(ResourceLocation typeId) {
        for (DeferredHolder<Codec<?>, ? extends Codec<?>> holder : ModPowers.POWERS.getEntries()) {
            if (holder.getId().equals(typeId)) {
                return (Codec<ModPowers.PlaceholderPower>) holder.get();
            }
        }
        return null;
    }

    @Override
    protected void applyResolved(Map<ResourceLocation, ConfiguredPower> values) {
        Map<ResourceLocation, ConfiguredPower> validated = values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            ConfiguredPower power = entry.getValue();
            List<ResourceLocation> actions = power.actions().stream().filter(actionId -> {
                boolean present = ConfiguredActions.get(actionId).isPresent();
                if (!present) {
                    Origins.LOGGER.warn("Power {} references unknown action {}", entry.getKey(), actionId);
                }
                return present;
            }).toList();

            if (ConfiguredConditions.get(power.condition()).isEmpty()) {
                Origins.LOGGER.warn("Power {} references unknown condition {}", entry.getKey(), power.condition());
            }

            return new ConfiguredPower(power.type(), power.name(), power.description(), actions, power.condition());
        }));
        ConfiguredPowers.setAll(validated);
    }
}
