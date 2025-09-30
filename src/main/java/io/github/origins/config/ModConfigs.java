package io.github.origins.config;

import io.github.origins.Origins;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.ModConfigEvent;

public final class ModConfigs {
    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    private ModConfigs() {
    }

    public static void register(IEventBus modBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, Origins.MOD_ID + "-common.toml");
        modBus.addListener(ModConfigs::handleConfigReload);
    }

    private static void handleConfigReload(ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            COMMON.reload();
        }
    }

    public static final class Common {
        public final ModConfigSpec.BooleanValue syncPowersOnLogin;
        public final ModConfigSpec.IntValue maxTrackedPowers;

        private Common(ModConfigSpec.Builder builder) {
            builder.push("networking");
            syncPowersOnLogin = builder
                .comment(
                    "If true, Origins will request a full power sync from the server whenever a player joins."
                )
                .define("syncPowersOnLogin", true);
            builder.pop();

            builder.push("gameplay");
            maxTrackedPowers = builder
                .comment("Hard limit for the number of simultaneous passive powers tracked on a player.")
                .defineInRange("maxTrackedPowers", 32, 1, 128);
            builder.pop();
        }

        private void reload() {
            // Future hooks will consume updated config values.
        }
    }
}
