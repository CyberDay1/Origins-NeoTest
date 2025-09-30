package io.github.apace100.origins.platform.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModLoadingContext;

public final class ModConfig {
    public static ModConfigSpec COMMON;

    private ModConfig() {}

    public static void register() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Origins config");
        COMMON = builder.build();
        ModLoadingContext.get().getActiveContainer().registerConfig(
            net.neoforged.fml.config.ModConfig.Type.COMMON,
            COMMON
        );
    }
}
