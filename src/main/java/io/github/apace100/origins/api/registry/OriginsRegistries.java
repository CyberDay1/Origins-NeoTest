package io.github.apace100.origins.api.registry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public final class OriginsRegistries {
    private OriginsRegistries() {
    }

    public static <T> OriginsRegistry<T> createSimple() {
        return new MapBackedOriginsRegistry<>();
    }

    private static final class MapBackedOriginsRegistry<T> implements OriginsRegistry<T> {
        private final Map<ResourceLocation, T> entries = new ConcurrentHashMap<>();

        @Override
        public void register(ResourceLocation id, T value) {
            entries.put(id, value);
        }

        @Override
        public Optional<T> get(ResourceLocation id) {
            return Optional.ofNullable(entries.get(id));
        }

        @Override
        public Stream<Map.Entry<ResourceLocation, T>> stream() {
            return entries.entrySet().stream();
        }
    }

    public interface OriginsRegistry<T> {
        void register(ResourceLocation id, T value);

        Optional<T> get(ResourceLocation id);

        Stream<Map.Entry<ResourceLocation, T>> stream();
    }
}
