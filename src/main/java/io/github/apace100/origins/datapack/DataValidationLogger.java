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
        Origins.LOGGER.info("[Origins] Datapack reload summary: {} origins, {} powers, {} actions ({} effect / {} attribute / {} item), {} conditions ({} effect / {} attribute / {} entity / {} composite / {} item / {} combat / {} environment)",
            stats.originsLoaded(), stats.powersLoaded(), stats.actionsLoaded(), stats.effectActionsLoaded(), stats.attributeActionsLoaded(), stats.itemActionsLoaded(),
            stats.conditionsLoaded(), stats.effectConditionsLoaded(), stats.attributeConditionsLoaded(), stats.entityConditionsLoaded(),
            stats.compositeConditionsLoaded(), stats.itemConditionsLoaded(), stats.combatConditionsLoaded(), stats.environmentConditionsLoaded());
        if (stats.skippedEntries() > 0) {
            Origins.LOGGER.info("[Origins] Datapack skipped {} entries during reload", stats.skippedEntries());
        }
        if (!stats.unknownTypes().isEmpty()) {
            String joined = stats.unknownTypes().stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));
            Origins.LOGGER.warn("[Origins] Datapack unknown entry types: {}", joined);
        }
    }
}
