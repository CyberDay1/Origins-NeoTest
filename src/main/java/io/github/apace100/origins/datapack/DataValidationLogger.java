package io.github.apace100.origins.datapack;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.datapack.OriginsDataLoader.ReloadStats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.stream.Collectors;

public final class DataValidationLogger extends SimplePreparableReloadListener<Void> {
    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void value, ResourceManager resourceManager, ProfilerFiller profiler) {
        ReloadStats stats = OriginsDataLoader.getLastReloadStats();
        Origins.LOGGER.info("[Origins] Datapack reload summary: {} origins, {} powers", stats.originsLoaded(), stats.powersLoaded());
        if (stats.skippedEntries() > 0) {
            Origins.LOGGER.info("[Origins] Datapack skipped {} entries during reload", stats.skippedEntries());
        }
        if (!stats.unknownTypes().isEmpty()) {
            String joined = stats.unknownTypes().stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));
            Origins.LOGGER.warn("[Origins] Datapack unknown power types: {}", joined);
        }
    }
}
