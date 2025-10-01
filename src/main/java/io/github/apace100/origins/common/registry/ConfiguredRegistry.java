package io.github.apace100.origins.common.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Simple thread-safe registry used for configured datapack content that is resolved via codecs.
 */
public final class ConfiguredRegistry<T> {
    private final Map<ResourceLocation, T> values = new ConcurrentHashMap<>();

    public void setAll(Map<ResourceLocation, T> newValues) {
        values.clear();
        values.putAll(newValues);
    }

    public Optional<T> get(ResourceLocation id) {
        return Optional.ofNullable(values.get(id));
    }

    public Collection<T> values() {
        return Collections.unmodifiableCollection(values.values());
    }

    public Set<ResourceLocation> ids() {
        return Collections.unmodifiableSet(values.keySet());
    }

    public Stream<Map.Entry<ResourceLocation, T>> stream() {
        return values.entrySet().stream();
    }
}
