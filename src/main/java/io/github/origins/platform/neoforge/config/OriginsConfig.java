package io.github.origins.platform.neoforge.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class OriginsConfig {
    private OriginsConfig() {}
    public static final ModConfigSpec COMMON_SPEC;
    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        // Example: b.comment("Origins settings").push("general");
        // b.pop();
        COMMON_SPEC = b.build();
    }

    public static void register() {
        // Registered via ModLoadingContext in the entrypoint
    }

    public static void onReload(ModConfigEvent.Reloading e) {
        if (e.getConfig().getType() == ModConfig.Type.COMMON) {
            // apply live changes if needed
        }
    }
}
