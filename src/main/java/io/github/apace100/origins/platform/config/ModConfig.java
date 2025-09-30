package io.github.apace100.origins.platform.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

public class ModConfig {
    public static ModConfigSpec COMMON;

    public static void register() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Origins config");
        COMMON = builder.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON);
    }
}
